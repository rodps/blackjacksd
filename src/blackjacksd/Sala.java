package blackjacksd;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author rodrigo
 */
public class Sala {
    
    private ArrayList<Socket> jogadores = new ArrayList(4);
    private ArrayList<Socket> espectadores = new ArrayList();
    private String nome;
    
    public Sala(String nome) {
        this.nome = nome;
    }
    
    public String getNome() {
        return this.nome;
    }
    
    public boolean adicionarJogador(Socket jogador) {
        return jogadores.add(jogador);
    }
    
    public boolean removerJogador(Socket jogador) {
        return jogadores.remove(jogador);
    }
    
    public boolean adicionarEspectador(Socket espec) {
        return espectadores.add(espec);
    }
    
    public boolean removerEspectador(Socket espec) {
        return espectadores.remove(espec);
    }  
    
}
