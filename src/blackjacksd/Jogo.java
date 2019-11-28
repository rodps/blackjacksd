
package blackjacksd;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
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
        
        while (true) {
            atualizaSala(sala.getId());
            System.out.println("\n");
            System.out.println("Aguardando outro jogador...");
            while(sala.getJogador2() == null || sala.getJogador1() == null ||
                    sala.getJogador1().isParou() || sala.getJogador2().isParou()) {
                try {
                    sleep(1000);
                    atualizaSala(sala.getId());
                } catch (InterruptedException ex) {
                    Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String aposta = JOptionPane.showInputDialog("Faça sua aposta:"); 
            System.out.println("Você apostou " + aposta);
            apostar(Integer.parseInt(aposta));
            
            atualizaSala(sala.getId());
            
            System.out.println("Aguardando outro jogador...");
            while(sala.getJogador1().getAposta()  == 0 |
                    sala.getJogador2().getAposta() == 0) {

                atualizaSala(sala.getId());
            }
            if (jogador.getNome().equals(sala.getJogador1().getNome())) {
                System.out.println(sala.getJogador2().getNome() + " apostou " + sala.getJogador2().getAposta());
            } else {
                System.out.println(sala.getJogador1().getNome() + " apostou " + sala.getJogador1().getAposta());
            }
            

//            pedirCarta();
//            try {
//                in.readObject();
//            } catch (IOException ex) {
//                Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (ClassNotFoundException ex) {
//                Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
//            }

            System.out.println("\n");
            System.out.println("Distribuindo cartas...");
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            atualizaSala(sala.getId());
            while (true) {    
                if (sala.getJogador1().getNome().equals(jogador.getNome())) {
                    if(sala.getJogador1().getCartas() > 21){
                        System.out.println("\n");
                        System.out.println("Você estourou!");
                        parar();
                        break;
                    }
                    System.out.println("\nentro");
                    System.out.println("Suas cartas: " + sala.getJogador1().getCartas());
                    System.out.println("Cartas do adversário: " + sala.getJogador2().getCartas());

                    String[] options = {"Pedir carta", "Parar"};
                    int option = JOptionPane.showOptionDialog(
                                   null
                                 , "O que deseja?"        
                                 , "Blackjack 21"               
                                 , JOptionPane.YES_NO_OPTION  
                                 , JOptionPane.PLAIN_MESSAGE                               
                                 , null
                                 , options
                                 , null
                               );
                    if(option == 0) {
                        pedirCarta();
                        atualizaSala(sala.getId());
                    }
                    if(option == 1) {
                        parar();
                        break;
                    }
                } else {
                    int ultimacarta = sala.getVez().getCartas();
                    try {
                        System.out.println("\n");
                        System.out.println("É a vez de " + sala.getVez().getNome());
                        System.out.println("Suas cartas: " + sala.getJogador2().getCartas());
                        System.out.println("Cartas do adversário: " + sala.getJogador1().getCartas());
                        if(sala.getJogador1().isParou()){
                            System.out.println(sala.getJogador1().getNome() + " parou.");
                            break;
                        }
                        Mensagem msg = (Mensagem) in.readObject();
                        if(msg.getOp() == Operacoes.PEDIR_CARTA) {
                            atualizaSala(sala.getId());
                        }
                        if(msg.getOp() == Operacoes.PARAR) {
                            break;
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
            while (true) { 
                if(sala.getJogador1().getCartas() > 21){
                        break;
                }
                if (sala.getJogador2().getNome().equals(jogador.getNome())) {
                    if(sala.getJogador2().getCartas() > 21){
                        System.out.println("\n");
                        System.out.println("Você estourou!");
                        parar();
                        break;
                    }
                    if(sala.getJogador1().getCartas() > 21){
                        System.out.println("\n");
                        System.out.println(sala.getJogador1().getNome() + " estourou. Você venceu!");
                        parar();
                        break;
                    }
                    System.out.println("\n");
                    System.out.println("Suas cartas: " + sala.getJogador2().getCartas());
                    System.out.println("Cartas do adversário: " + sala.getJogador1().getCartas());

                    String[] options = {"Pedir carta", "Parar"};
                    int option = JOptionPane.showOptionDialog(
                                   null
                                 , "O que deseja?"        
                                 , "Blackjack 21"               
                                 , JOptionPane.YES_NO_OPTION  
                                 , JOptionPane.PLAIN_MESSAGE                               
                                 , null
                                 , options
                                 , null
                               );
                    if(option == 0) {
                        pedirCarta();
                        atualizaSala(sala.getId());
                    }
                    if(option == 1) {
                        parar();
                        break;
                    }
                } else {
                    try {
                        System.out.println("\n");
                        System.out.println("É a vez de " + sala.getVez().getNome());
                        System.out.println("Suas cartas: " + sala.getJogador1().getCartas());
                        System.out.println("Cartas do adversário: " + sala.getJogador2().getCartas());
                        if(sala.getJogador2().isParou()){
                            System.out.println(sala.getJogador1().getNome() + " parou.");
                            break;
                        }
                        Mensagem msg = (Mensagem) in.readObject();
                        if(msg.getOp() == Operacoes.PEDIR_CARTA) {
                            atualizaSala(sala.getId());
                        }
                        if(msg.getOp() == Operacoes.PARAR) {
                            break;
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            if(sala.getJogador1().getNome().equals(jogador.getNome())) {
                if(sala.getJogador2().getCartas() > 21){
                    System.out.println("\n");
                    System.out.println(sala.getJogador2().getNome() + "estourou.");
                    break;
                }
            }

            atualizaSala(sala.getId());
            String msgFinal;
            if(sala.getJogador1().getCartas() > sala.getJogador2().getCartas()){
                if(sala.getJogador1().getNome().equals(jogador.getNome())) {
                    if(sala.getJogador1().getCartas() <= 21){
                        msgFinal = "Você venceu! :)";
                    } else {
                        msgFinal = "Você perdeu! :[";
                    }                  
                } else {
                    if(sala.getJogador1().getCartas() > 21){
                        msgFinal = "Você venceu! :)";
                    } else {
                        msgFinal = "Você perdeu! :[";
                    }
                }
            } else if(sala.getJogador1().getCartas() == sala.getJogador2().getCartas()) {
                msgFinal = "Empate! :|";
            } else {
                if(sala.getJogador2().getNome().equals(jogador.getNome())) {
                    if(sala.getJogador2().getCartas() <= 21){
                        msgFinal = "Você venceu! :)";
                    } else {
                        msgFinal = "Você perdeu! :[";
                    }                  
                } else {
                    if(sala.getJogador2().getCartas() > 21){
                        msgFinal = "Você venceu! :)";
                    } else {
                        msgFinal = "Você perdeu! :[";
                    }
                }
            }
            
            int res = JOptionPane.showConfirmDialog(null,
                    msgFinal + "\nNova partida?",
                    "Fim de jogo",
                    JOptionPane.YES_NO_OPTION);
            if(res == 1){
                sair();
                return;
            }
            System.out.println("\n");
            System.out.println("Iniciando nova partida em 5 segundos");
            
            try {
                sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void atualizaSala(int idsala) {
        try {
            this.out.writeObject(new Mensagem(Operacoes.ATUALIZAR, idsala));
            Mensagem msg = (Mensagem) in.readObject();
            sala = (Sala) msg.getDados();
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
    
    public void pedirCarta() {
        try {
            out.writeObject(new Mensagem(Operacoes.PEDIR_CARTA));
        } catch (IOException ex) {
            Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void parar() {
        try {
            out.writeObject(new Mensagem(Operacoes.PARAR));
        } catch (IOException ex) {
            Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sair() {
        try {
            out.writeObject(new Mensagem(Operacoes.SAIR));
        } catch (IOException ex) {
            Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void entrar(int idsala) {
        try {
            out.writeObject(new Mensagem(Operacoes.ENTRAR_SALA, idsala));
        } catch (IOException ex) {
            Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void novaPartida() {
        try {
            out.writeObject(new Mensagem(Operacoes.NOVA_PARTIDA));
        } catch (IOException ex) {
            Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
   

