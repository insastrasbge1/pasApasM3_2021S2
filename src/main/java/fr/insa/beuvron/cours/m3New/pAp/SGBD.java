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

    public abstract String sousProtocale();

    public String getUrl() {
        return "jdbc:" + this.sousProtocale()
                + "://" + this.host + ":"
                + this.port + "/" + this.database;
    }

    public Connection connect() throws ClassNotFoundException,
                        SQLException {
        Class.forName(this.nomDriver());

        Connection con = DriverManager.getConnection(this.getUrl(), user, pass);
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
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
        public String sousProtocale() {
            return "postgres";
        }

    }

}
