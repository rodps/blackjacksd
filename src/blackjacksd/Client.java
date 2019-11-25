package blackjacksd;

import protocolo.Operacoes;
import protocolo.Mensagem;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.Thread.sleep;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import protocolo.Tipo;

/**
 *
 * @author rodrigo
 */
public class Client {
    
    public static void main(String[] args) {
                
        try {
            int serverPort = 5555;
            InetAddress serverAddr = InetAddress.getByName("127.0.0.1");
            Socket socket = new Socket(serverAddr, serverPort);
            
            ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());
                       
            String username = JOptionPane.showInputDialog("Digite seu nome de usuário:");
            objOut.writeObject(new Mensagem(Operacoes.LOGIN, username));
            Jogador jogador = (Jogador) objIn.readObject();
            
            while (true) {                
                String[] options = {"Criar sala", "Ver salas"};
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

                switch(option) {
                    case 0 /* Criar sala */: {
                        String nomeSala = JOptionPane.showInputDialog("Digite o nome da sala:");
                        if(nomeSala != null) {
                            Mensagem msg = new Mensagem(Operacoes.CRIAR_SALA, nomeSala);
                            objOut.writeObject(msg);
                            Mensagem resp = (Mensagem) objIn.readObject();
                            if(resp.getTipo() == Tipo.SUCESSO) {
                                System.out.println("Sala criada com sucesso");
                                Sala sala = (Sala) resp.getDados();
                                Jogo jogo = new Jogo(jogador, objIn, objOut, sala);
                                jogo.iniciar();
                            } else if(resp.getTipo() == Tipo.ERRO) {
                                System.out.println("Erro ao criar sala: " + resp.getMensagem());
                            }
                        }
                        break;
                    }
                    case 1 /* Ver salas */: {
                        Mensagem msg = new Mensagem(Operacoes.VER_SALAS);
                        objOut.writeObject(msg);
                        Mensagem resp = (Mensagem) objIn.readObject();
                        ArrayList<Sala> salas = (ArrayList) resp.getDados();
                        ArrayList salasNomes = new ArrayList();
                        salas.forEach((Sala sala) -> {
                            salasNomes.add(sala.getId() + " - " + sala.getNome());
                        });
                        String idsala = JOptionPane.showInputDialog(
                                "Digite o id da sala que deseja conectar:\n"
                                        + Arrays.toString(salasNomes.toArray()));
                        if(idsala != null) {
                            objOut.writeObject(new Mensagem(Operacoes.ENTRAR_SALA,
                                                            Integer.parseInt(idsala)));
                            Mensagem r = (Mensagem) objIn.readObject();
                            
                            if (r.getTipo() == Tipo.SUCESSO) {
                                System.out.println("Você entrou na sala");
                                Sala sala = (Sala) r.getDados();
                                System.out.println(sala.getJogador2());
                                Jogo jogo = new Jogo(jogador, objIn, objOut, sala);
                                jogo.iniciar();
                            } else if (r.getTipo() == Tipo.ERRO) {
                                System.out.println("Não foi possível entrar na sala: "
                                                    + r.getMensagem());
                                
                            }
                        }
                        break;
                    }
                }
            }

                        
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
