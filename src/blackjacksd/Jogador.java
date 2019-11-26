package blackjacksd;

import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author rodrigo
 */
public class Jogador implements Serializable{
    private int cartas;
    private String nome;
    private int fichas;
    private int aposta;
    private boolean parou;
    
    public Jogador(String nome) {
        this.nome = nome;
        this.cartas = 0;
        this.fichas = 0;
        this.parou = false;
    }

    public boolean isParou() {
        return parou;
    }

    public void setParou(boolean parou) {
        this.parou = parou;
    }
    
    public void darCarta(int carta) {
        this.cartas += carta;
    }
    
    public void darFichas(int fichas) {
        this.fichas += fichas;
    }
    
    public void tirarFichas(int fichas) {
        this.fichas -= fichas;
    }
    
    public void apostar(int valor) {
        this.aposta = valor;
    }
    
    public int getCartas() {
        return cartas;
    }
    
    public String getNome() {
        return nome;
    }
}
