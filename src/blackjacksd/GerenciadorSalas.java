/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blackjacksd;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author rodrigo
 */
public class GerenciadorSalas {
    Integer ids;
    HashMap<Integer, Sala> salas;
    
    public GerenciadorSalas() {
        ids = 0;
        salas = new HashMap();
    }
    
    public synchronized Sala criarSala(String nome, Jogador j1) {
        Sala novaSala = new Sala(nome, ids, j1);
        salas.put(ids++, novaSala);
        return novaSala;
    }
    
    public synchronized Sala removerSala(int id) {
        return salas.remove(id);
    }
    
    public Sala procurarSala(int id) {
        return salas.get(id);
    }
    
    public ArrayList<Sala> getSalas() {
        ArrayList<Sala> s = new ArrayList<Sala>(salas.values());
        return s;
    }
}
