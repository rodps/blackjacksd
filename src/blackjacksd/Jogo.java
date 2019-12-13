
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
            novaPartida();
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
            
            if (jogador.getNome().equals(sala.getJogador1().getNome())) {
                jogador.setFichas(sala.getJogador1().getFichas());
            } else {
                jogador.setFichas(sala.getJogador2().getFichas());
            }

            if (jogador.getModo() == 0) {
                String aposta = JOptionPane.showInputDialog("Suas fichas: "+jogador.getFichas()
                    +"\nFaça sua aposta:", 1); 
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
            } else {
                System.out.println("Aguardando apostas...");
                while(sala.getJogador1().getAposta()  == 0 |
                        sala.getJogador2().getAposta() == 0) {

                    atualizaSala(sala.getId());
                }
                System.out.println(sala.getJogador1().getNome() + " apostou " + sala.getJogador1().getAposta());
                System.out.println(sala.getJogador2().getNome() + " apostou " + sala.getJogador2().getAposta());                
            }
            



            System.out.println("\n");
            System.out.println("Distribuindo cartas...");
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            pedirCarta();
            try {
                Mensagem msg = (Mensagem) in.readObject();
                sala = (Sala) msg.getDados();
            } catch (IOException ex) {
                Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
            }
            
//            atualizaSala(sala.getId());
            if (jogador.getModo() == 0) {
                while (true) { 
                    if (sala.getJogador1().getNome().equals(jogador.getNome())) {
                        if(sala.getJogador1().getCartas() > 21){
                            System.out.println("\n");
                            System.out.println("Você estourou!");
                            parar();
                            break;
                        }
                        if(sala.getJogador1().getCartas() == 21){
                            JOptionPane.showConfirmDialog(null, "Vinte e um!", "Vinte e um!", JOptionPane.OK_OPTION);
                            parar();
                            break;
                        }
                        System.out.println("\n");
                        System.out.println("É sua vez!");
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
                        try {
                            System.out.println("\n");
                            System.out.println("É a vez de " + sala.getVez().getNome());
                            System.out.println("Suas cartas: " + sala.getJogador2().getCartas());
                            System.out.println("Cartas do adversário: " + sala.getJogador1().getCartas());

                            Mensagem msg = (Mensagem) in.readObject();
                            if(msg.getOp() == Operacoes.PEDIR_CARTA) {
                                sala = (Sala) msg.getDados();
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
                            try {
                                sleep(1000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            parar();
                            break;
                        }
                        if(sala.getJogador2().getCartas() == 21){
                            JOptionPane.showConfirmDialog(null, "Vinte e um!", "Vinte e um!", JOptionPane.OK_OPTION);
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
                        System.out.println("É sua vez!");
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
                                sala = (Sala) msg.getDados();
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
                    }
                }
                
                String msgFinal;
                int ganhou = 0;
                if(sala.getJogador1().getCartas() > sala.getJogador2().getCartas()){
                    if(sala.getJogador1().getNome().equals(jogador.getNome())) {
                        if(sala.getJogador1().getCartas() <= 21){
                            msgFinal = "Você venceu! :)";
                            ganhou = 1;
                        } else {
                            msgFinal = "Você perdeu! :[";
                        }                  
                    } else {
                        if(sala.getJogador1().getCartas() > 21){
                            msgFinal = "Você venceu! :)";
                            ganhou = 1;
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
                            ganhou = 1;
                        } else {
                            msgFinal = "Você perdeu! :[";
                        }                  
                    } else {
                        if(sala.getJogador2().getCartas() > 21){
                            msgFinal = "Você venceu! :)";
                            ganhou = 1;
                        } else {
                            msgFinal = "Você perdeu! :[";
                        }
                    }
                }
                
                if (ganhou == 0) {
                    try {
                        out.writeObject(new Mensagem(Operacoes.PERDEU));
                    } catch (IOException ex) {
                        Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (ganhou == 1) {
                    try {
                        out.writeObject(new Mensagem(Operacoes.GANHOU));
                    } catch (IOException ex) {
                        Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                
    //            parar();
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
                    sleep(3000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                while (true) {                    
                    try {
                        System.out.println("\n");
                        System.out.println("É a vez de " + sala.getVez().getNome());
                        System.out.println("Cartas de " + sala.getJogador1().getNome() + ": "+ sala.getJogador1().getCartas());
                        System.out.println("Cartas de " + sala.getJogador2().getNome() + ": "+ sala.getJogador2().getCartas());

                        sleep(5000);
                        atualizaSala(sala.getId());
                        if (sala.getJogador1().isParou() & sala.getJogador2().isParou()) {
                            break;
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                sair();
                break;
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
   

