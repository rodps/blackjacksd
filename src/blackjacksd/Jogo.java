
package blackjacksd;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocolo.Mensagem;
import protocolo.Operacoes;

/**
 *
 * @author rodrigo
 */
public class Jogo {
    private Jogador jogador;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    
    public Jogo(Jogador jogador, Socket socket) {
        this.jogador = jogador;
        this.socket = socket;
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void iniciar() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Aguardando outro jogador...");
        try {
            while(true) {
                out.writeObject(new Mensagem(Operacoes.ATUALIZAR));
                Mensagem resp = (Mensagem) in.readObject();
                Sala sala = (Sala) resp.getDados();
                if(sala.apostas) {
                    System.out.println("Inicio de jogo. Façam suas apostas.");
                    int c=0;
                    while(c<2) {
                        resp = (Mensagem) in.readObject();
                        sala = (Sala) resp.getDados();
                        if(sala.proximoAJogar().getNome().equals(jogador.getNome())){
                            System.out.println("É sua vez.");
                            System.out.println("Quanto deseja apostar?");
                            
                        } else {
                            System.out.println("É a vez de " + sala.proximoAJogar().getNome());
                        }
                        c++;
                    }
                    sala.apostas = false; 
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
