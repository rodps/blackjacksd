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

            System.out.println("Aguardando conexões...");
            while(true) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("Cliente conectado. IP: " + clientSocket.getInetAddress());
                ClientThread clientThread = new ClientThread(clientSocket, gSalas, jogadorSocket);
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
    
    public ClientThread (Socket clientSocket, GerenciadorSalas gSalas, HashMap jogadorSocket) {
        try {
            this.clientSocket = clientSocket;
            this.gSalas = gSalas;
            this.objInputStream = new ObjectInputStream(clientSocket.getInputStream());
            this.objOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            this.jogadorSocket = jogadorSocket;
            this.jogadorSala = new HashMap<>();
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
                        System.out.println(sala.getJogador1());
                        System.out.println(sala.getJogador2());
                        objOutputStream.reset();
                        objOutputStream.writeObject(resp);
                        break;
                    }
                    case ATUALIZAR: {
                        int idsala = (int) msg.getDados();
                        Sala sala = gSalas.procurarSala(idsala);
                        resposta.setSala(sala);
                        System.out.println(sala.getJogador2());
                        objOutputStream.reset();
                        objOutputStream.writeObject(resposta);
                    }
                    case APOSTA: {
                        int valor = (int) msg.getDados();
                        jogador.apostar(valor);
                        Sala s = jogadorSala.get(jogador);
                        for (Jogador j : jogadores) {
                            if(j != jogador){
                                Socket s = jogadorSocket.get(j);
                                ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                                out.writeInt(valor);
                            }
                        }
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
