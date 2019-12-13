package blackjacksd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author rodrigo
 */
public class Database {
    
    private String url;
    
    public Database(String url) {
        this.url = url;
    }
    
    private Connection connect() {
        // SQLite connection string
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
    
    public void init() {
        String sql1 = "DROP TABLE IF EXISTS jogadores;";
        String sql2 = "CREATE TABLE IF NOT EXISTS jogadores ("
                + "    nome text NOT NULL PRIMARY KEY,"
                + "    fichas int"
                + ");";
        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement()) {
             stmt.execute(sql1);
             stmt.execute(sql2);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } 
    }
    
    public Jogador getJogador(String nome) {
        String sql = "SELECT * "
                + "FROM jogadores "
                + "WHERE nome = ?;";
        try (Connection conn = this.connect();
             PreparedStatement stmt  = conn.prepareStatement(sql);){
            stmt.setString(1, nome);
            ResultSet rs    = stmt.executeQuery();
            
            // loop through the result set
            if (rs.next()) {
                Jogador jogador = new Jogador(nome);
                jogador.setFichas(rs.getInt("fichas"));
                return jogador;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    
    public Jogador addJogador(String nome) {
        String sql = "INSERT INTO jogadores(nome, fichas) values(?, ?);";
        
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            pstmt.setInt(2, 100);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        Jogador j = new Jogador(nome);
        j.setFichas(100);
        return j;

    }
    
    public void updateJogador(String nome, int fichas) {
        String sql = "UPDATE jogadores SET fichas = ? WHERE nome = ?;";
 
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
 
            // set the corresponding param
            pstmt.setString(2, nome);
            pstmt.setInt(1, fichas);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }    
}
