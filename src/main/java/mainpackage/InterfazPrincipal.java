package mainpackage;

import java.awt.BorderLayout;
import java.awt.CardLayout; // Porque estamos haciendo una interfaz gráfica
import java.awt.Color; // Cuando usamos CheckBox
import java.awt.Dimension; // Porque vamos a trabajar con colores
import java.awt.Font; // Porque vamos a trabajar con eventos: botones, combox, ...
import java.awt.GridLayout; // Para usar Casilla
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class InterfazPrincipal extends JFrame{
    private static final int SIZE = 10;  // Tamaño del tablero 10x10
    public JButton[][] botones = new JButton[SIZE][SIZE];   
    private Point elementoSeleccionado = null;  // Guarda la posición del elemento seleccionado
    public boolean accionRealizada = false;
    
    public CardLayout cardLayout;
    public JPanel panelTablero, panelDerechoPrincipal, panelBotonesPermanentes;
    public Arma armaActiva;
    
    PanelInicio panelInicio;
    PanelMenuJugador panelMenuJugador;
    PanelHistoriales panelHistoriales;
    
    public int nJugadores = 0;
    private String[] nombresZombis = {"Z.Ca.N", "Z.Co.N", "Z.Ab.N", "Z.Ca.B", "Z.Co.B", "Z.Ab.B", "Z.Ca.T", "Z.Co.T", "Z.Ab.T"};
    
    public Partida partida;
    private Tablero tablero;
                
    public InterfazPrincipal(Partida partida){
        this.partida = partida;
        tablero = partida.getTablero();
        
        setTitle("Juego");
        setBounds(0, 0, 1145, 745);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout()); // BorderLayout() divide el la ventana en 5 zonas (Norte, Sur, Este, Oeste y Centro)
        
        // Panel del tablero (a la izquierda)
        panelTablero = new JPanel();
        panelTablero.setLayout(new GridLayout(SIZE, SIZE));
        // GridLayout(,) organiza los componentes en una cuadrícula rectangular con un número fijo de filas y columnas,
        // donde todos los componentes tienen el mismo tamaño
        panelTablero.setPreferredSize(new Dimension(745, 745));  // Tamaño fijo para el tablero
        
        // Manejador de Paneles
        cardLayout = new CardLayout();
        // Panel de derecho principal (a la derecha)
        panelDerechoPrincipal = new JPanel(cardLayout);
        panelDerechoPrincipal.setPreferredSize(new Dimension(400, 745));
        
        // Añadimos voy las clases panel al panel de derecho principal
        panelInicio = new PanelInicio(partida, this);
        panelDerechoPrincipal.add(panelInicio,"PanelInicio");
        
        // Añadir paneles al JFrame
        add(panelTablero, BorderLayout.CENTER);  // Tablero en el centro (ocupa la izquierda)
        add(panelDerechoPrincipal, BorderLayout.EAST);    // Panel de control a la derecha
        inicializarTablero();
        setVisible(true);
    }
    
    public void inicializarPaneles(){
        panelMenuJugador = new PanelMenuJugador(partida,this);
        panelDerechoPrincipal.add(panelMenuJugador,"PanelMenuJugador");
        panelDerechoPrincipal.revalidate();
        panelDerechoPrincipal.repaint();
        
        panelHistoriales = new PanelHistoriales(partida.getAlmacenDeAtaques(),this, partida);
        panelDerechoPrincipal.add(panelHistoriales,"PanelHistoriales");
        panelDerechoPrincipal.revalidate();
        panelDerechoPrincipal.repaint();
    }
    
    public void actualizacionGeneralPanelMenuJugador(){
        panelMenuJugador.actualizarLabels();
        panelMenuJugador.activacionBotones(true);
    }
    
    // Inicializar el tablero con Casillas y Botones
    public void inicializarTablero() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                botones[i][j] = new JButton();
                botones[i][j].setFocusPainted(false);
                botones[i][j].setBackground(Color.LIGHT_GRAY);
                botones[i][j].setForeground(Color.BLACK);
                botones[i][j].setFont(new Font("Arial",0,10));
                botones[i][j].setText("<html></html>");
                panelTablero.add(botones[i][j]);
            }
        }
    }
    
    public void activarActionListener(){
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int I = i, J = j;
                botones[i][j].addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e){
                        accionBotonesTablero(botones[I][J],I,J);
                    }
                });
            }
        }
        StringBuilder sb = new StringBuilder();
        String aux = botones[9][9].getText();
        aux = aux.replace("</html>", "");
        sb.append(aux);
        sb.append("Meta");
        sb.append("<br>");
        sb.append("</html>");
        botones[9][9].setText(sb.toString());
        botones[9][9].setBackground(Color.GREEN);
    }
    
    public void accionBotonesTablero(JButton boton, int x, int y){
        Superviviente supervivienteActual = partida.getSupervivienteActual();
        if(panelMenuJugador.movimientoActivado){
            moverElemento(boton, x, y);
            if(elementoSeleccionado != null){
                actualizarCasillas(tablero.getCasilla(elementoSeleccionado.x, elementoSeleccionado.y), tablero.getCasilla(x,y));
            }else{
                actualizarCasillas(tablero.getCasilla(x, y), tablero.getCasilla(x,y));
            }
        } else if(!panelMenuJugador.movimientoActivado){
            JOptionPane.showMessageDialog(this,"No puede moverse en este momento");
        }else if(panelMenuJugador.atacarActivado){
            if(armaActiva != null){
                atacar(botones[x][y], x, y);
                if(elementoSeleccionado != null){
                    actualizarCasillas(tablero.getCasilla(elementoSeleccionado.x, elementoSeleccionado.y), tablero.getCasilla(x,y));
                }else{
                    actualizarCasillas(tablero.getCasilla(x, y), tablero.getCasilla(x,y));
                }
            } else{
                JOptionPane.showMessageDialog(this,"Debes seleccionar un arma");
                panelMenuJugador.activacionBotones(true);
            }
        }else if(!panelMenuJugador.atacarActivado){
            JOptionPane.showMessageDialog(this,"No puede atacar en este momento");
            panelMenuJugador.activacionBotones(true);
        }
    }
    
    public void reiniciarTablero() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                botones[i][j].setText("<html></html>");
                botones[i][j].setBackground(Color.LIGHT_GRAY);
                botones[i][j].setForeground(Color.BLACK);
                tablero.reiniciarTablero();
            }
        }
    }

    public void actualizarCasillas(Casilla origen, Casilla destino){
        Casilla[] casillas = {origen, destino};
        for (int i = 0; i < 2; i++){
            StringBuilder sb1 = new StringBuilder("<html>");
            StringBuilder sb2 = new StringBuilder();   
            if(casillas[i].getContadorSupervivientes() != 0){
                for (int k = 0; k < casillas[i].getContadorSupervivientes(); k++){
                        String nombreTemp = casillas[i].getSuperviviente(k).getNombre();
                        sb1.append(nombreTemp);
                        sb1.append("<br>");
                    }
                }
            if(casillas[i].getContadorZombis() != 0){
                for (int k = 0; k < casillas[i].getContadorZombis(); k++){
                    String zombiTemp = casillas[i].getZombi(k).getZombiParaBoton(); 
                    sb2.append(zombiTemp);      
                }

            }
            sb1.append(sb2.toString());
            sb1.append("</html>");
            botones[casillas[i].getX()][casillas[i].getY()].setText(sb1.toString());
        }
    }
    
    public static int contarApariciones(String texto, String patron) {
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(texto);
        int contador = 0;
    
        while (matcher.find()) {
            contador++;
        }
        return contador;
    }
    public static String eliminarPatron(String texto, String patron, int veces) {
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(texto);
        StringBuilder resultado = new StringBuilder(texto);
        int eliminados = 0;
    
        while (matcher.find() && eliminados < veces) {
            int start = matcher.start();
            int end = matcher.end();
            resultado.replace(start, end, ""); // Eliminar el patrón encontrado
            matcher.reset(resultado); // Reiniciar el matcher con el nuevo texto
            eliminados++;
        }
        return resultado.toString();
    }
    

    // Acción al hacer clic en una casilla del tablero
    public void moverElemento(JButton boton, int x, int y){
        if(panelMenuJugador.movimientoActivado){
            Superviviente supervivienteActual = partida.getSupervivienteActual();
            if (elementoSeleccionado == null && boton.getText().contains(supervivienteActual.getNombre())) {
                // Selecciona un elemento para mover
                elementoSeleccionado = new Point(x, y);
                boton.setBackground(Color.DARK_GRAY);  // Resaltar elemento
                boton.setForeground(Color.WHITE);
            } else if (elementoSeleccionado != null) {
                if(x == supervivienteActual.getCasillaActual().getX() && y == supervivienteActual.getCasillaActual().getY()){
                    JOptionPane.showMessageDialog(this,"No puede moverse a la misma casilla");   
                    return;
                }
                // Verificar si el movimiento es a una casilla adyacente 
                if (Math.abs(elementoSeleccionado.x - x) <= 1 && Math.abs(elementoSeleccionado.y - y) <= 1) {                  
                    // Mover el superviviente a la nueva casilla y marcarla como ocupada
                    partida.activarSuperviviente(0, x, y);

                    tablero.posicionesOcupadas[x][y] = true;
                    botones[elementoSeleccionado.x][elementoSeleccionado.y].setBackground(Color.LIGHT_GRAY);
                    botones[elementoSeleccionado.x][elementoSeleccionado.y].setForeground(Color.BLACK);
                    tablero.posicionesOcupadas[x][y] = true;
                    botones[elementoSeleccionado.x][elementoSeleccionado.y].setBackground(Color.LIGHT_GRAY);
                    botones[elementoSeleccionado.x][elementoSeleccionado.y].setForeground(Color.BLACK);

                    // Coger el texto del nuevo boton y añadirle el superviviente
                    StringBuilder sb = new StringBuilder();
                    String textoBotonDestino = botones[x][y].getText();
                    textoBotonDestino = textoBotonDestino.replace("</html>", ""); // Quitamos el cierre de HTML
                    sb.append(textoBotonDestino);
                    sb.append(partida.getSupervivienteActual().getNombre());
                    sb.append("<br>"); // Salto de linea en HTML
                    sb.append("</html>"); // Colocamos el cierre HTML
                    botones[x][y].setText(sb.toString());

                    // Quitamos del boton el nombre del superviviente
                    String textoBotonOrigen = botones[elementoSeleccionado.x][elementoSeleccionado.y].getText();
                    textoBotonOrigen = textoBotonOrigen.replace(supervivienteActual.getNombre() + "<br>","");
                    if(tablero.getCasilla(elementoSeleccionado.x, elementoSeleccionado.y).getContadorSupervivientes() == 0){
                        if(tablero.getCasilla(elementoSeleccionado.x, elementoSeleccionado.y).getContadorZombis() == 0){
                            // Marcamos la casilla como vacia
                            tablero.posicionesOcupadas[elementoSeleccionado.x][elementoSeleccionado.y] = false;
                            botones[elementoSeleccionado.x][elementoSeleccionado.y].setText("<html></html>"); // Vacio
                        } else{
                            botones[elementoSeleccionado.x][elementoSeleccionado.y].setText(textoBotonOrigen);
                        }
                    } else{
                        botones[elementoSeleccionado.x][elementoSeleccionado.y].setText(textoBotonOrigen);
                    }
                    elementoSeleccionado = null;
                    panelMenuJugador.movimientoActivado = false;
                    panelMenuJugador.activacionBotones(true);
                }
            }
        } else{
            JOptionPane.showMessageDialog(this,"No puede moverse en este momento");
        }
    }

    public void atacar(JButton boton, int x, int y){
        if(panelMenuJugador.atacarActivado){
            Superviviente supervivienteActual = partida.getSupervivienteActual();
            ArrayList<Casilla> casillasAlcance = supervivienteActual.elegirObjetivo(armaActiva);
            for (int i = 0; i < casillasAlcance.size(); i++){
                botones[casillasAlcance.get(i).getX()][casillasAlcance.get(i).getY()].setBackground(Color.RED);
                botones[casillasAlcance.get(i).getX()][casillasAlcance.get(i).getY()].setForeground(Color.WHITE);
            }
            if(elementoSeleccionado == null && boton.getText().contains(supervivienteActual.getNombre())){
                elementoSeleccionado = new Point(x, y);
                boton.setBackground(Color.DARK_GRAY);
                boton.setForeground(Color.WHITE);
            } else if(elementoSeleccionado != null){
                if(tablero.getCasilla(x,y).getContadorZombis() > 0){
                    // Atacar al zombi
                    partida.activarSuperviviente(panelMenuJugador.ranuraElegida, x, y);
                    // En caso de que haya zombis muertos ya se encarga partida
                    // Restablecer valores
                    elementoSeleccionado = null;
                    panelMenuJugador.atacarActivado = false;
                    panelMenuJugador.activacionBotones(true);
                }else{
                    JOptionPane.showMessageDialog(this,"No hay zombis en la casilla seleccionada");
                }
            }
            for (int i = 0; i < casillasAlcance.size(); i++){
                botones[casillasAlcance.get(i).getX()][casillasAlcance.get(i).getY()].setBackground(Color.LIGHT_GRAY);
                botones[casillasAlcance.get(i).getX()][casillasAlcance.get(i).getY()].setForeground(Color.BLACK);
            }
        }
    }
         
    
    public static void main(String args[]){ 
        Juego juego = new Juego();
        juego.iniciarPartida();
    }
}
