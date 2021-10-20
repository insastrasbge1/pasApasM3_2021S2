/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.beuvron.cours.m3New.tds.TD4;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author francois
 */
public class Amour {
    
    public static Connection connect()
            throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5439/postgres",
                "postgres", "pass");
    }
    
    public static void creeTableTruc(Connection con)
            throws SQLException {
        try ( Statement st = con.createStatement()) {
            st.executeUpdate(
                    """
              create table tructd4 (
                id integer not null primary key
                   generated always as identity,
                nom varchar(30) not null
              )
              """);
        }
    }
    
    public static void createUnTruc(Connection con, String nom)
            throws SQLException {
        try ( PreparedStatement pst = con.prepareStatement(
                """
            insert into tructd4 (nom) 
              values (?)
            """)) {
            pst.setString(1, nom);
            pst.executeUpdate();
        }
        
    }
    
    public static void main(String[] args) {
        try ( Connection con = connect()) {
            System.out.println("connecté !!!");
//            creeTableTruc(con);
            createUnTruc(con, "Toto");
        } catch (Exception ex) {
            System.out.println("Problème : " + ex);
        }
    }
    
}
