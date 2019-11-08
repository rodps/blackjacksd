package blackjacksd;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rodrigo
 */
public class Server {
    
    public static void main(String[] args) {
        try {
            ArrayList<Sala> salas = new ArrayList();
            ServerSocket listenSocket = new ServerSocket(5555);

            while(true) {
                Socket clientSocket = listenSocket.accept();
                ClientThread clientThread = new ClientThread(clientSocket, salas);
                clientThread.start();
            }
                      
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

class ClientThread extends Thread {

    Socket clientSocket;
    List<Sala> salas;
    DataInputStream inputStream;
    DataOutputStream outputStream;
    ObjectOutputStream objOutputStream;
    
    public ClientThread (Socket clientSocket, List<Sala> salas) {
        try {
            this.clientSocket = clientSocket;
            this.salas = salas;
            this.inputStream = new DataInputStream(clientSocket.getInputStream());
            this.outputStream = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        try {
            String username = inputStream.readUTF();
            Jogador jogador = new Jogador(username, clientSocket);
            
            int option = inputStream.readInt();
            switch(option) {
                case 1 : {
                    ArrayList salasNomes = new ArrayList();
                    for (Sala sala : salas) {
                        salasNomes.add(sala.getNome());
                    }
                    objOutputStream.writeObject(salasNomes);
                    break;
                }
            }
            
            while (true) {
               
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}