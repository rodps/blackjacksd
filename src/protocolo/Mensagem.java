
package protocolo;

import blackjacksd.Sala;
import java.io.Serializable;

/**
 *
 * @author rodrigo
 */
public class Mensagem implements Serializable {
    private Operacoes op;
    private Tipo tipo;
    private Serializable dados;
    private String mensagem;
    
    public Mensagem(Operacoes op, Serializable dados) {
        this.op = op;
        this.dados = dados;
    }
    
    public Mensagem(Operacoes op) {
        this.op = op;
    }
    
    public Operacoes getOp() {
        return op;
    }
    public void setOp(Operacoes op) {
        this.op = op;
    }
    
    public Serializable getDados() {
        return dados;
    }
    
    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }
    
    public Tipo getTipo() {
        return tipo;
    }
    
    public void setDados(Serializable dados) {
        this.dados = dados;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
    
    
}
