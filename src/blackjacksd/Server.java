package blackjacksd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
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
            MulticastSocket msocket = new MulticastSocket(5555);
            msocket.joinGroup(InetAddress.getByName("224.0.0.1"));
            
            
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

class ReceiveThread extends Thread {
    MulticastSocket multicastSocket = null;
	
    public ReceiveThread (MulticastSocket multicastSocket) {
        this.multicastSocket = multicastSocket;
    }

    public void run() {
        try {
            while (true) {
                byte[] buffer = new byte[1000];
                DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
                multicastSocket.receive(messageIn);
//                System.out.println("Recebido:" + new String(messageIn.getData(),0,messageIn.getLength()));
            }
        } catch (Exception e) {
                System.out.println(e);
        }
    }
}