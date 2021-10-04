/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.beuvron.cours.m3New.pAp;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author francois
 */
public class Test1 {

    public static void main(String[] args) {
//        Connection con = null;
//        try {
//            SGBD postgres = new SGBD.Postgresql("localhost",
//                    5439, "postgres", "pass", "postgres");
//            con = postgres.connect();
//            System.out.println("tout va bien");
//        } catch (Exception ex) {
//            throw new Error("Erreur de connection",
//                    ex);
//        } finally {
//            if (con != null) {
//                try {
//                    con.close();
//                } catch (SQLException ex) {
//                }
//            }
//        }
        SGBD postgres = new SGBD.Postgresql("localhost",
                5439, "postgres", "pass", "postgres");
        try ( Connection con = postgres.connect()) {
            System.out.println("tout va bien");
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

}
