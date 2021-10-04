/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.beuvron.cours.m3New.pAp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author francois
 */
public abstract class SGBD {

    private String host;
    private int port;
    private String user;
    private String pass;
    private String database;

    public SGBD(String host, int port, String user, String pass, String database) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.database = database;
    }

    public abstract String nomDriver();

    public abstract String sousProtocole();

    public String getUrl() {
        return "jdbc:" + this.sousProtocole()
                + "://" + this.host + ":"
                + this.port + "/" + this.database;
    }

    public Connection connect() throws ClassNotFoundException,
            SQLException {
        Class.forName(this.nomDriver());

        Connection con = DriverManager.getConnection(this.getUrl(), user, pass);
        
        // fixe le plus haut degré d'isolation entre transactions
        // cela peut éventuellement ralentir le serveur s'il y a beaucoup de clients
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        
        // avec con.setAutoCommit(true) (defaut), chaque ordre au sgbd au travers
        // d'un executeUpdate ou executeQuery est dans sa propre transaction
        // isolée. Cela est un problème si l'on veut faire des modifications
        // avec plusieurs ordres executeUpdate.
        // dans ce cas, il vaut mieux faire un con.setAutoCommit(false), et valider
        // explicitement les modifications avec un con.commit() (ou un con.rollback()
        // si l'on veut annuler les modifications)
        // néanmoins, le défaut est correct pour un
        // executeQuery isolé, ce qui sera le plus souvent le cas
        // aussi, je laisse le defaut.
        // con.setAutoCommit(false);
        
        return con;
    }

    public static class Postgresql extends SGBD {

        public Postgresql(String host, int port, String user, String pass, String database) {
            super(host, port, user, pass, database);
        }

        @Override
        public String nomDriver() {
            return "org.postgresql.Driver";
        }

        @Override
        public String sousProtocole() {
            return "postgres";
        }

    }

}
