
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
            atualizaSala(sala.getId());
            System.out.println("Aguardando outro jogador...");
            while(sala.getJogador2() == null) {
                try {
                    sleep(1000);
                    atualizaSala(sala.getId());
                } catch (InterruptedException ex) {
                    Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
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

                pedirCarta();
                System.out.println("Distribuindo cartas...");
                try {
                    sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
                }
//                    atualizaSala(sala.getId());

//                    if(sala.getJogador1().getNome().equals(jogador.getNome())) {
//                        System.out.println("Suas cartas: " + sala.getJogador1().getCartas());
//                        System.out.println("Cartas do adversário: " + sala.getJogador2().getCartas());
//                    } else {
//                        System.out.println("Suas cartas: " + sala.getJogador2().getCartas());
//                        System.out.println("Cartas do adversário: " + sala.getJogador1().getCartas());
//                    }

                while(true) {
                    atualizaSala(sala.getId());
                    if(sala.getJogador1().getNome().equals(jogador.getNome())) {
                        if(sala.getJogador1().getCartas() > 21){
                            System.out.println("Você estourou!");
                            parar();
                            break;
                        }
                        System.out.println("É sua vez.");
                        System.out.println("Suas cartas: " + sala.getJogador1().getCartas());
                        System.out.println("Cartas do adversário: " + sala.getJogador2().getCartas());
                        System.out.println("1 - Pedir carta");
                        System.out.println("2 - Parar");
                        int op = scanner.nextInt();
                        if(op == 1) {
                            pedirCarta();
                        }
                        if(op == 2) {
                            parar();
                            break;
                        }
                    } else {
                        System.out.println("É a vez de " + sala.getJogador1().getNome());
                        System.out.println("Suas cartas: " + sala.getJogador2().getCartas());
                        System.out.println("Cartas do adversário: " + sala.getJogador1().getCartas());
                        while (!sala.getJogador1().isParou()) {                                
                            try {
                                sleep(1000);
                                atualizaSala(sala.getId());
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        break;
                    }
                }
                while(true) {
                    atualizaSala(sala.getId());
                    if(sala.getJogador2().getNome().equals(jogador.getNome())) {
                        if (sala.getJogador1().getCartas() > 21) {
                            System.out.println(sala.getJogador1().getNome() + "estourou.");
                            System.out.println("Você venceu!");
                            break;
                        }
                        if (sala.getJogador2().getCartas() > 21) {
                            System.out.println("Você estourou!");
                            parar();
                            break;
                        }
                        System.out.println("É sua vez.");
                        System.out.println("Suas cartas: " + sala.getJogador2().getCartas());
                        System.out.println("Cartas do adversário: " + sala.getJogador1().getCartas());
                        System.out.println("1 - Pedir carta");
                        System.out.println("2 - Parar");
                        int op = scanner.nextInt();
                        if(op == 1) {
                            pedirCarta();
                        }
                        if(op == 2) {
                            parar();
                            break;
                        }
                    } else {
                        if (sala.getJogador1().getCartas() > 21) {
                            break;
                        }
                        System.out.println("É a vez de " + sala.getJogador2().getNome());
                        System.out.println("Suas cartas: " + sala.getJogador1().getCartas());
                        System.out.println("Cartas do adversário: " + sala.getJogador2().getCartas());
                        while (!sala.getJogador2().isParou()) {                                
                            try {
                                sleep(1000);
                                atualizaSala(sala.getId());
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        break;
                    }
                }
                if(sala.getJogador1().getNome().equals(jogador.getNome())) {
                    if(sala.getJogador2().getCartas() > 21){
                        System.out.println(sala.getJogador2().getNome() + "estourou.");
                        System.out.println("Você venceu!");
                        break;
                    }
                }

            sair();
            System.out.println("Novo partida? y/n");
            String res = scanner.next();
            if(res.equals('n')){
                return;
            }
            entrar(sala.getId());
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
}
    
