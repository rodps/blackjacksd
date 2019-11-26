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
            
            while (true) {    
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
                        sala.adicionarJogador(jogador);
                        jogadorSala.put(jogador, sala);
                        Mensagem resp = new Mensagem(Operacoes.RESPOSTA);
                        resp.setTipo(Tipo.SUCESSO);
                        resp.setDados(sala);
                        objOutputStream.reset();
                        objOutputStream.writeObject(resp);
                        break;
                    }
                    case ATUALIZAR: {
                        int idsala = (int) msg.getDados();
                        Sala sala = gSalas.procurarSala(idsala);
                        resposta.setSala(sala);
                        objOutputStream.reset();
                        objOutputStream.writeObject(resposta);
                        break;
                    }
                    case APOSTA: {
                        int valor = (int) msg.getDados();
                        jogador.apostar(valor);
                        Sala s = jogadorSala.get(jogador);
                        if(s.getJogador1() != jogador) {
                            ObjectOutputStream out = streams.get(s.getJogador1()).getOutput();
                            out.reset();
                            out.writeInt(valor);
                            out.flush();
                        } else {
                            ObjectOutputStream out = streams.get(s.getJogador2()).getOutput();
                            out.reset();
                            out.writeInt(valor);  
                            out.flush();
                        }
                        break;
                    }
                    case PEDIR_CARTA: {
                        jogador.darCarta(random.nextInt(10)+1);
                        break;
                    }
                    case PARAR: {
                        jogador.setParou(true);
                        break;
                    }
                }
            }         
            
        } catch (IOException ioe) {
           ioe.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

}
