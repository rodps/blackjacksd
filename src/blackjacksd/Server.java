package blackjacksd;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

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
                ClientThread clientThread = new ClientThread(clientSocket);
                clientThread.start();
            }
                      
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

class ClientThread extends Thread {

    Socket clientSocket;
    DataInputStream inputStream;
    DataOutputStream outputStream;
    
    public ClientThread (Socket clientSocket) {
        try {
            this.clientSocket = clientSocket;
            this.inputStream = new DataInputStream(clientSocket.getInputStream());
            this.outputStream = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        try {
            while (true) {
               
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}