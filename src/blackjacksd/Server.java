package blackjacksd;

import protocolo.Operacoes;
import protocolo.Mensagem;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocolo.Tipo;

/**
 *
 * @author rodrigo
 */
public class Server {
    
    public static void main(String[] args) {
        try {
            GerenciadorSalas gSalas = new GerenciadorSalas();
            ServerSocket listenSocket = new ServerSocket(5555);
            HashMap<Jogador, Socket> jogadorSocket = new HashMap<>();
            HashMap<Jogador, JogadorStream> streams = new HashMap<>();

            System.out.println("Aguardando conexões...");
            while(true) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("Cliente conectado. IP: " + clientSocket.getInetAddress());
                ClientThread clientThread = new ClientThread(clientSocket, gSalas, jogadorSocket, streams);
                clientThread.start();
            }
                      
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

class ClientThread extends Thread {

    Socket clientSocket;
    GerenciadorSalas gSalas;
    ObjectOutputStream objOutputStream;
    ObjectInputStream objInputStream;
    HashMap<Jogador, Sala> jogadorSala;
    HashMap<Jogador, Socket> jogadorSocket;
    HashMap<Jogador, JogadorStream> streams;
    
    public ClientThread (Socket clientSocket, GerenciadorSalas gSalas, HashMap jogadorSocket, HashMap streams) {
        try {
            this.clientSocket = clientSocket;
            this.gSalas = gSalas;
            this.objInputStream = new ObjectInputStream(clientSocket.getInputStream());
            this.objOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            this.jogadorSocket = jogadorSocket;
            this.jogadorSala = new HashMap<>();
            this.streams = streams;
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        try {
            Mensagem login = (Mensagem) objInputStream.readObject();
            Jogador jogador = new Jogador((String) login.getDados());
            objOutputStream.writeObject(jogador);
            jogadorSocket.put(jogador, clientSocket);
            System.out.println(login.getDados() + " entrou.");
            
            streams.put(jogador, new JogadorStream(jogador, objOutputStream, objInputStream));
            Random random = new Random();
            boolean sair = true;
            while (sair) {    
                Mensagem msg = (Mensagem) objInputStream.readObject();
                Mensagem resposta = new Mensagem(Operacoes.RESPOSTA);
                switch(msg.getOp()) {
                    case CRIAR_SALA: {
                        Sala sala = gSalas.criarSala((String) msg.getDados(), jogador);
                        jogadorSala.put(jogador, sala);
                        resposta.setTipo(Tipo.SUCESSO);
                        resposta.setDados(sala);
                        objOutputStream.writeObject(resposta);
                        
                        break;
                    }
                    case VER_SALAS: {
                        resposta.setDados(gSalas.getSalas());
                        resposta.setTipo(Tipo.SUCESSO);
                        objOutputStream.writeObject(resposta);
                        break;
                    }
                    case ENTRAR_SALA: {
                        int idsala = (int) msg.getDados();
                        Sala sala = gSalas.procurarSala(idsala);
                        if (jogador.getModo() == 0) {
                            sala.adicionarJogador(jogador);
                        } else {
                            sala.adicionarEspectador(jogador);
                        }
                        jogadorSala.put(jogador, sala);
                        Mensagem resp = new Mensagem(Operacoes.RESPOSTA);
                        resp.setTipo(Tipo.SUCESSO);
                        resp.setDados(sala);
                        
                        objOutputStream.writeObject(resp);
                        break;
                    }
                    case ATUALIZAR: {
                        int idsala = (int) msg.getDados();
                        Sala sala = gSalas.procurarSala(idsala);
                        resposta.setDados(sala);
                        resposta.setOp(Operacoes.ATUALIZAR);
                        objOutputStream.flush();
                        objOutputStream.reset();
                        objOutputStream.writeObject(resposta);
                        break;
                    }
                    case APOSTA: {
                        int valor = (int) msg.getDados();
                        jogador.apostar(valor);
                        
                        break;
                    }
                    case PEDIR_CARTA: {
                        jogador.darCarta(random.nextInt(10)+1);
                        Sala s = jogadorSala.get(jogador);
                        if(s.getJogador1() != jogador) {
                            ObjectOutputStream out = streams.get(s.getJogador1()).getOutput();
                            out.reset();
                            out.writeObject(new Mensagem(Operacoes.PEDIR_CARTA, s));
                            out.reset();
                            out.flush();
                        } else {
                            ObjectOutputStream out = streams.get(s.getJogador2()).getOutput();
                            out.reset();
                            out.writeObject(new Mensagem(Operacoes.PEDIR_CARTA, s)); 
                            out.reset();
                            out.flush();
                        }
                        break;
                    }
                                        
                    case PARAR: {
                        jogador.setParou(true);
                        Sala s = jogadorSala.get(jogador);
                       
                        if(s.getJogador1() != jogador) {
                            ObjectOutputStream out = streams.get(s.getJogador1()).getOutput();
                            out.flush();
                            
                            out.reset();
                            out.writeObject(new Mensagem(Operacoes.PARAR));
                            out.flush();
                        } else {
                            ObjectOutputStream out = streams.get(s.getJogador2()).getOutput();
                            out.flush();
                            out.reset();
                            out.writeObject(new Mensagem(Operacoes.PARAR));  
                            out.flush();
                        }

                        break;
                    }
                    case SAIR: {
                        Sala s = jogadorSala.get(jogador);
                        s.removerJogador(jogador);
                        jogadorSala.remove(jogador);
                        if (s.getJogador1() == null & s.getJogador2() == null) {
                            gSalas.removerSala(s.getId());
                        }
                        break;
                    }
                    case NOVA_PARTIDA: {
                        jogador.setParou(false);
                        jogador.setCartas(0);
                        jogador.resetAposta();
                        break;
                    }
                    case SAIR_JOGO: {
                        sair = false;
                        System.out.println(jogador.getNome() + " saiu.");
                        break;
                    }
                    case MODO: {
                        int modo = (int) msg.getDados();
                        jogador.setModo(modo);
                        break;
                    }
                    case GANHOU: {
                        jogador.darFichas(jogador.getAposta());
                        break;
                    }
                    case PERDEU: {
                        jogador.tirarFichas(jogador.getAposta());
                        break;
                    }
                }
            }
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
            

    }

}
