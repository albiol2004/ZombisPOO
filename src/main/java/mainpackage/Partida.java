package mainpackage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import javax.swing.*;

public class Partida implements Serializable{
    public ArrayList<Superviviente> supervivientes;
    public ArrayList<Zombi> zombis;
    private int turnoActual = 0;
//    private Scanner scanner = new Scanner(System.in);
    private Tablero tablero;
    private Equipo[] inventarioActual;
    private Superviviente supervivienteActual;
    private AlmacenDeAtaques almacen;
    private InterfazPrincipal interfazPrincipal;
        
    public Superviviente getSupervivienteActual() {
        if (supervivientes == null || supervivientes.isEmpty()) {
            throw new IllegalStateException("No hay supervivientes en la lista.");
        }
        return supervivientes.get(turnoActual);
    }
    
    public Superviviente getSupervivienteIndice(int indice) {
        if (indice >= 0 && indice < supervivientes.size()) {
            return supervivientes.get(indice);
        }
        return null;
    }
    
    public Tablero getTablero() {
        return tablero;
    }
    public int getTurnoActual(){
        return turnoActual;
    }
    
    public void avanzarTurno() {
        turnoActual = (turnoActual + 1) % supervivientes.size();
    }

    public void introducirSupervivientes(String[] nombres){
        for (String nombre : nombres) {
            supervivientes.add(new Superviviente(nombre, null));
        }
    }

    public void colocarElementosIniciales(String[] nombres){
        supervivientes = new ArrayList<Superviviente>(interfazPrincipal.nJugadores);
        Casilla.inicializarArrayList();
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                interfazPrincipal.reiniciarTablero();
            }
        });
        
        StringBuilder sb1 = new StringBuilder();
        sb1.append("<html>"); // Inicio con HTML
        for(int i = 0; i<interfazPrincipal.nJugadores; i++){
            Superviviente s = new Superviviente(nombres[i], tablero.getCasilla(0,0));
            tablero.getCasilla(0,0).addSuperviviente(s);
            supervivientes.add(s);
            sb1.append(s.getNombre());
            sb1.append("<br>"); // Salto de linea en HTML
        }
        sb1.append("</html>"); // Final con HTML
        String textoBotonSupervivientes = sb1.toString();
        // Poner texto en el boton[0][0]
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                interfazPrincipal.botones[0][0].setText(textoBotonSupervivientes);
            }
        });
        tablero.posicionesOcupadas[0][0] = true; // Marcar la [0][0] como ocupada
        // Spawnear Zombis
        for (int i = 0; i < 3; i++) {
            faseApariciónZombi();
        }
        // Cambiar el panel derecho
        Partida estaPartida = this;
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                interfazPrincipal.añadirActionListener();
                interfazPrincipal.inicializarPaneles(estaPartida);
                interfazPrincipal.cardLayout.show(interfazPrincipal.panelDerechoPrincipal, "PanelMenuJugador");
            }
        });    
    }

    private void faseSuperviviente(int eleccion){ //eleccion viene del input de la interfaz
        supervivienteActual = this.getSupervivienteActual();
        inventarioActual = supervivienteActual.getInventario();
        supervivienteActual.setAcciones(3);
        System.out.println("Turno de " + supervivienteActual.getNombre());
        while(supervivienteActual.getAcciones() > 0){
            System.out.println("Acciones restantes: " + supervivienteActual.getAcciones());
            switch(eleccion){
                case 1:
                    //Interfaz dara el input para el movimiento; int casillaObjetivo = [0-8]
                    supervivienteActual.setSeleccion(Entidad.accion.MOVER);
                    //supervivienteActual.activar(casillaObjetivo);
                    continue;
                case 2:
                    //Interfaz dara el input para el arma a usar
                    supervivienteActual.setSeleccion(Entidad.accion.ATACAR);
                    //int alcanceTemp = supervivienteActual.getArma(a).getAlcance();
                    //supervivienteActual.activar(int a); Este es el arma [0-1];
                    Ataque ataque = supervivienteActual.getUltimoAtaque();
                    //List<Casilla> objetivo = supervivienteActual.elegirObjetivo(supervivienteActual.getArma(a));
                    Casilla casillaObjetivo = null;
                    /* if(objetivo == null){
                        int intento = 0;
                        while(intento < supervivienteActual.getCasillaActual().getContadorZombis()){
                            try {
                                supervivienteActual.getCasillaActual().getZombi(intento).reaccion(supervivienteActual.getArma(a), ataque.numExitos(almacen));
                            } catch (IllegalArgumentException e) {
                                if ("Alcance".equals(e.getMessage())) { //Si es Berserker
                                    supervivienteActual.addAcciones(); // Devuelve la accion para seguir intentando
                                    intento++; // Siguiente zombi
                                }
                            }
                        
                        }
                    } else {
                     // Asociar de alguna forma las opciones disponibles objetivo con un input y la interfaz
                    // Las casillas objetivo estan en la List<Casilla> objetivo
                    // Guardar en casillaObjetivo la casilla seleccionada
                    int intento = 0;
                    while(intento < casillaObjetivo.getContadorZombis()){
                        try {
                            casillaObjetivo.getZombi(intento).reaccion(supervivienteActual.getArma(a), ataque.numExitos(almacen));
                        } catch (IllegalArgumentException e) {
                            if ("Alcance".equals(e.getMessage())) { //Si es Berserker
                                supervivienteActual.addAcciones(); // Devuelve la accion para seguir intentando
                                intento++; // Siguiente zombi
                            }
                        }
                    }
                } */continue;  

                case 3: //Buscar
                    supervivienteActual.setSeleccion(Entidad.accion.BUSCAR);
                    int slotInventario = 0;
                    // Input de interfaz para elegir el slot del inventario
                    //supervivienteActual.activar(slotInventario);
                    continue;
                case 4: //Elegir arma o usar provision
                    int objetoInventario = 0;
                    int ranuraObjetivo = 0;
                    // Input de interfaz para elegir el objeto del inventario, si es un arma tambien la ranura
                    supervivienteActual.elegirArma(objetoInventario, ranuraObjetivo);
                    supervivienteActual.setAcciones(supervivienteActual.getAcciones()+1);
                    continue;
                case 5: //Nada
                    break;
            }
        }
    }
    

    public void faseZombie(){
        for (int i = 0; i < zombis.size(); i++){
            zombis.get(i).activar();
        }
    }

    public void faseApariciónZombi(){
        // Generar casilla random no ocupada
        Random random = new Random();
        int x, y;
        do { 
            x = random.nextInt(10); 
            y = random.nextInt(10); 
        } while (tablero.posicionesOcupadas[x][y]);        
        tablero.posicionesOcupadas[x][y] = true; // Marcar la nueva casilla como ocupada
        // Crear Zombi
        Zombi z =  null;
        int subtipo = random.nextInt(3);
        switch(subtipo){
            case 0:
                z = new Zombi(tablero,tablero.getCasilla(x, y),"NORMAL");
                break;
            case 1:
                z = new Toxico(tablero,tablero.getCasilla(x, y),"TOXICO");
                break;
            case 2:
                z = new Berserker(tablero,tablero.getCasilla(x, y),"BERSERKER");
                break;
        }
        // Añadir al tablero
        tablero.getCasilla(x, y).addEntidad(z);
        // Aparecer en la interfaz
        int X = x, Y = y;
        Zombi Z = z;
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                interfazPrincipal.botones[X][Y].setText(Z.getZombiParaBoton());  // Mostrar el tipo de Zombi
            }
        });
        
    }
    
//    public void inicializarSupervivientes(int nJugadores){
//        supervivientes = new ArrayList<Superviviente>(nJugadores);
//    }

    public Partida(){
//    public Partida(int numJugadores){
        tablero = new Tablero();
        almacen =  new AlmacenDeAtaques();
//        supervivientes = new ArrayList<Superviviente>(4);
//        String nombre = "";
//        for(int i = 0; i < 4; i++){
//            supervivientes.add(new Superviviente(nombre, tablero.getCasilla(0, 0)));
//        }

//        interfazPrincipal = new InterfazPrincipal(this);
        // LLamamos a la InterfazPrincipal
        Partida estaPartida = this;
        SwingUtilities.invokeLater(new Runnable() {
           @Override public void run() {
                interfazPrincipal = new InterfazPrincipal(estaPartida);
            }
        });
    }
}