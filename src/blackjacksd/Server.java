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

            System.out.println("Aguardando conexões...");
            while(true) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("Cliente conectado. IP: " + clientSocket.getInetAddress());
                ClientThread clientThread = new ClientThread(clientSocket, gSalas);
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
    DataInputStream inputStream;
    DataOutputStream outputStream;
    ObjectOutputStream objOutputStream;
    ObjectInputStream objInputStream;
    HashMap<Jogador, Socket> jogadorSocket;
    HashMap<Jogador, Sala> jogadorSala;
    
    public ClientThread (Socket clientSocket, GerenciadorSalas gSalas) {
        try {
            this.clientSocket = clientSocket;
            this.gSalas = gSalas;
            this.inputStream = new DataInputStream(clientSocket.getInputStream());
            this.outputStream = new DataOutputStream(clientSocket.getOutputStream());
            this.objOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            this.objInputStream = new ObjectInputStream(clientSocket.getInputStream());
            this.jogadorSocket = new HashMap<>();
            this.jogadorSala = new HashMap<>();
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        try {
            String username = inputStream.readUTF();
            Jogador jogador = new Jogador(username);
            jogadorSocket.put(jogador, clientSocket);
            System.out.println(username + " entrou.");
            
            while (true) {                
                Mensagem msg = (Mensagem) objInputStream.readObject();
                Mensagem resposta = new Mensagem(Operacoes.RESPOSTA);
                switch(msg.getOp()) {
                    case CRIAR_SALA: {
                        Sala sala = gSalas.criarSala((String) msg.getDados());
                        sala.adicionarJogador(jogador);
                        jogadorSala.put(jogador, sala);
                        resposta.setTipo(Tipo.SUCESSO);
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
                        resposta.setTipo(Tipo.SUCESSO);
                        objOutputStream.writeObject(resposta);
                        break;
                    }
                }
            }         
            
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void atualizarJogadores(Sala sala) {
        
    }

    public void comecarJogo(Sala sala) {
        ArrayList<Jogador> jogadores = sala.getJogadores();
        for (Jogador jogador : jogadores) {
            Socket s = jogadorSocket.get(jogador);
            try {
                ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                out.writeObject(new Mensagem(Operacoes.INICIAR, sala));
            } catch (IOException ex) {
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}