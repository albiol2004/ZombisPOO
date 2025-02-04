package mainpackage;

import java.util.Random;

public class Arma extends Equipo{
    private int potencia, dados, alcance, exito;

    public Arma(){
        switch (generarTipoArma()){
            case 1:
                nombre = "Espada";
                potencia = 3;
                dados = 3;
                alcance = 0;
                exito = 3;
                break;
            case 2:
                nombre = "Escopeta";
                potencia = 5;
                dados = 1;
                alcance = 1;
                exito = 4;
                break;
            case 3:
                nombre = "Pistola";
                potencia = 1;
                dados = 2;
                alcance = 2;
                exito= 2;
                break;
            case 4:
                nombre = "Francotirador";
                potencia = 4;
                dados = 2;
                alcance = 3;
                exito = 3;
                break;
            default:
            System.out.println("Tipo de arma no válida");
            break;
        }
    }

    public Arma(int tipo){
        switch (tipo){
            case 1:
                nombre = "Espada";
                potencia = 3;
                dados = 3;
                alcance = 0;
                exito = 3;
                break;
            case 2:
                nombre = "Escopeta";
                potencia = 5;
                dados = 1;
                alcance = 1;
                exito = 4;
                break;
            case 3:
                nombre = "Pistola";
                potencia = 1;
                dados = 2;
                alcance = 2;
                exito= 3;
                break;
            case 4:
                nombre = "Francotirador";
                potencia = 4;
                dados = 1;
                alcance = 3;
                exito = 2;
                break;
            default:
            System.out.println("Tipo de arma no válida");
            break;
        }
    }

    public int generarTipoArma(){
        Random random = new Random();
        // Generar un número aleatorio entre 1, 2, 3 y 4
        return random.nextInt(4) + 1;
    }

    @Override
    public String getNombre(){
        return nombre;
    }
    public int getPotencia(){
        return potencia;
    }
    public int getDados(){
        return dados;
    }
    public  int getAlcance(){
        return alcance;
    }
    public int getExito(){
        return exito;
    }
    @Override
    public String toString(){
        return ("Nombre: "+ nombre + ", Potencia:" + potencia + ", Dados: "
        +dados+ ", Alcance: " +alcance+ ", Exito: "+exito);
    }
}