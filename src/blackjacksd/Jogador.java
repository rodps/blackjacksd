package blackjacksd;

import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author rodrigo
 */
public class Jogador {
    private int cartas;
    private String nome;
    private Socket socket;
    private int fichas;
    
    public Jogador(String nome, Socket socket) {
        this.nome = nome;
        this.socket = socket;
        this.cartas = 0;
        this.fichas = 0;
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
}
