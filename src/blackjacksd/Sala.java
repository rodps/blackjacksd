package blackjacksd;

import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author rodrigo
 */
public class Sala implements Serializable {
    
    private Jogador jogador1;
    private Jogador jogador2;
    private ArrayList<Jogador> espectadores;
    private String nome;
    private int id;
    private int vez;
    
    public Sala(String nome, int id, Jogador jogador1) {
        this.nome = nome;
        this.id = id;
        this.vez = -1;
        this.jogador1 = jogador1;
        this.jogador2 = null;
        this.espectadores = new ArrayList();
    }

    public Jogador getJogador1() {
        return jogador1;
    }

    public void setJogador1(Jogador jogador1) {
        this.jogador1 = jogador1;
    }

    public Jogador getJogador2() {
        return jogador2;
    }

    public void setJogador2(Jogador jogador2) {
        this.jogador2 = jogador2;
    }
    
    
    
    public String getNome() {
        return this.nome;
    }
    
    public int getId() {
        return this.id;
    }
    
    public boolean adicionarJogador(Jogador jogador) {
        if(jogador2 == null){
            this.jogador2 = jogador;
            return true;
        }
        return false;
    }
    
    public boolean removerJogador(Jogador jogador) {
        if(jogador1 == jogador) {
            jogador1 = jogador2;
            jogador2 = null;
        }
        else if(jogador2 == jogador)
            jogador2 = null;
        else
            return false;
        return true;
    }
    
    public boolean adicionarEspectador(Jogador espec) {
        return espectadores.add(espec);
    }
    
    public boolean removerEspectador(Jogador espec) {
        return espectadores.remove(espec);
    }
        
}
