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
    
    private ArrayList<Jogador> jogadores = new ArrayList(2);
    private ArrayList<Jogador> espectadores = new ArrayList();
    private String nome;
    private int id;
    private int vez;
    public boolean apostas;
    
    public Sala(String nome, int id) {
        this.nome = nome;
        this.id = id;
        this.vez = 0;
        apostas = true;
    }
    
    public String getNome() {
        return this.nome;
    }
    
    public int getId() {
        return this.id;
    }
   
    public ArrayList<Jogador> getJogadores() {
        return this.jogadores;
    }
    
    public int quantidadeJogadores() {
        return this.jogadores.size();
    }
    
    public boolean adicionarJogador(Jogador jogador) {
        return jogadores.add(jogador);
    }
    
    public boolean removerJogador(Jogador jogador) {
        return jogadores.remove(jogador);
    }
    
    public boolean adicionarEspectador(Jogador espec) {
        return espectadores.add(espec);
    }
    
    public boolean removerEspectador(Jogador espec) {
        return espectadores.remove(espec);
    }
    
    public Jogador proximoAJogar() {
        vez += 1;
        if (vez >= jogadores.size()) {
            vez = 0;
        }
        return jogadores.get(vez);
    }
        
}
