
package blackjacksd;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.Thread.sleep;
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
    private Sala sala;
    private Jogador jogador;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    
    public Jogo(Jogador jogador, ObjectInputStream in, ObjectOutputStream out, Sala sala) {
        this.jogador = jogador;
        this.sala = sala;
        this.in = in;
        this.out = out;
    }
    
    public void iniciar() {
        Scanner scanner = new Scanner(System.in);
        while (true) {            
            if (sala.getJogador2() == null) {
                try {
                    System.out.println("Aguardando outro jogador...");
                    sleep(1000);
                    atualizaSala(sala.getId());
                } catch (InterruptedException ex) {
                    Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                System.out.println("Começou a partida. Façam suas apostas.");
                
                if(sala.getJogador1().getNome().equals(jogador.getNome())) {
                    System.out.println("É sua vez. Digite a sua aposta: ");
                    int valor = scanner.nextInt();
                    apostar(valor);
//                    atualizaSala(sala.getId());
                } else {
                    try {
                        System.out.println("É a vez de " + sala.getJogador1().getNome());
                        System.out.println(sala.getJogador1().getNome() + " apostou " + in.readInt());
                    } catch (IOException ex) {
                        Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                if(sala.getJogador2().getNome().equals(jogador.getNome())) {
                    System.out.println("É sua vez. Digite a sua aposta: ");
                    int valor = scanner.nextInt();
                    apostar(valor);
//                    atualizaSala(sala.getId());
                } else {
                    try {
                        System.out.println("É a vez de " + sala.getJogador2().getNome());
                        System.out.println(sala.getJogador2().getNome() + " apostou " + in.readInt());
                    } catch (IOException ex) {
                        Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
//                while(true) {
//                    atualizaSala();
//                    Jogador proximo = sala.proximoAJogar();
//                    if(proximo.getNome().equals(jogador.getNome())) {
//                        System.out.println("É sua vez.");
//                        System.out.println("1 - Pedir carta.\n2 - Parar");
//                        int opt = scanner.nextInt();
//                        if (opt == 1) {
//                            
//                        }
//                    } else {
//                        
//                    }
//                }
                
            }
        
    }
    
    public void atualizaSala(int idsala) {
        try {
            this.out.writeObject(new Mensagem(Operacoes.ATUALIZAR, idsala));
            Mensagem resp = (Mensagem) in.readObject();
            this.sala = resp.getSala();
        } catch (IOException ex) {
            Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void apostar(int valor) {
        try {
            this.out.writeObject(new Mensagem(Operacoes.APOSTA, valor));
        } catch (IOException ex) {
            Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
//        try {
//            Scanner scanner = new Scanner(System.in);
//            System.out.println("Aguardando outro jogador...");
//
//            while(true) {
//                out.writeObject(new Mensagem(Operacoes.ATUALIZAR));
//                Mensagem resp = (Mensagem) in.readObject();
//                Sala sala = (Sala) resp.getDados();
//                System.out.println("Inicio de jogo. Façam suas apostas.");
//                int c=0;
//                while(c<2) {
//                    out.writeObject(new Mensagem(Operacoes.ATUALIZAR));
//                    resp = (Mensagem) in.readObject();
//                    sala = (Sala) resp.getDados();
//                    Jogador proximo = sala.proximoAJogar();
//                    if(proximo.getNome().equals(jogador.getNome())){
//                        System.out.println("É sua vez.");
//                        System.out.println("Quanto deseja apostar?");
//                        out.writeObject(new Mensagem(Operacoes.APOSTA, scanner.nextInt()));
//                    } else {
//                        System.out.println("É a vez de " + proximo.getNome());
//                        resp = (Mensagem) in.readObject();
//                        int aposta = (int) resp.getDados();
//                        System.out.println(proximo.getNome() + " apostou " + aposta);
//                    }
//                    c++;
//                }
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//    }
    
