package blackjacksd;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author a1724533
 */
public class JogadorStream {
    private Jogador jogador;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    public JogadorStream(Jogador jogador, ObjectOutputStream out, ObjectInputStream in) {
        this.jogador = jogador;
        this.out = out;
        this.in = in;
    }
    
    public ObjectOutputStream getOutput() {
        return this.out;
    }
    
    public ObjectInputStream getInput() {
        return this.in;
    }
}
