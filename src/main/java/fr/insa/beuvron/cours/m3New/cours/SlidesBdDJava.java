/*
Copyright 2000- Francois de Bertrand de Beuvron

This file is part of CoursBeuvron.

CoursBeuvron is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CoursBeuvron is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CoursBeuvron.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.insa.beuvron.cours.m3New.cours;

import fr.insa.beuvron.utils.ConsoleFdB;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Month;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author francois
 */
public class SlidesBdDJava {

    public static Connection connectPostgresql(String host, int port,
            String database, String user, String pass)
            throws ClassNotFoundException, SQLException {
        // teste la présence du driver postgresql
        Class.forName("org.postgresql.Driver");
        Connection con = DriverManager.getConnection(
                "jdbc:postgresql://" + host + ":" + port + "/" + database, user, pass);
        // fixe le plus haut degré d'isolation entre transactions
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        return con;
    }
//...

    public static void main1(String[] args) {
        try ( Connection con = connectPostgresql("localhost", 5432,
                "postgres", "postgres", "mypass")) {
            // testConnection(con);  // ici le programme
        } catch (ClassNotFoundException | SQLException ex) {
            throw new Error(ex);
        }
    }

    public static void createTablePerson(Connection con) throws SQLException {
        try ( Statement st = con.createStatement()) {
            st.executeUpdate(
                    """
               create table Person(
                 id integer primary key generated always as identity,
                 nom varchar(50) not null,
                 dateNaissance date
               )
               """);
        }
    }

    public static void createSchema(Connection con) throws SQLException {
        try ( Statement st = con.createStatement()) {
            // on veut que le schema soit entierement créé ou pas du tout
            // il nous faut plusieurs ordres pour créer le schema
            // on va donc explicitement gérer les connections
            con.setAutoCommit(false);
            st.executeUpdate(
                    """
               create table Person(
                 id integer primary key generated always as identity,
                 nom varchar(50) not null,
                 dateNaissance date
               )
               """);
            st.executeUpdate(
                    """
               create table Surnom(
                 id integer primary key generated always as identity,
                 surnom varchar(50) not null
               )
               """);
            st.executeUpdate(
                    """
               create table PersonSurnoms(
                 idPerson integer,
                 idSurnom integer
               )
               """);
            st.executeUpdate(
                    """
               alter table PersonSurnoms 
                 add constraint fk_Person_Surnoms_idPerson
                 foreign key(idPerson)
                 references Person(id)
                   on delete restrict
                   on update restrict
               """);
            st.executeUpdate(
                    """
               alter table PersonSurnoms 
                 add constraint fk_Person_Surnoms_idSurnom
                 foreign key(idSurnom)
                 references Surnom(id)
                   on delete restrict
                   on update restrict
               """);
            // si j'arrive ici, c'est que tout s'est bien passé
            // je valide la transaction
            con.commit();
        } catch (SQLException ex) {
            // si quelque chose se passe mal, j'annule la transaction
            // avant de resignaler l'exception
            con.rollback();
            throw ex;
        } finally {
            // pour s'assurer que le autoCommit(true) reste le comportement
            // par défaut (utile dans la plupart des "select"
            con.setAutoCommit(true);
        }
    }

    public static void deleteSchema(Connection con) throws SQLException {
        try ( Statement st = con.createStatement()) {
            con.setAutoCommit(false);
            // pour être sûr de pouvoir supprimer les tables,
            // le plus simple est de supprimer d'abord toutes
            // les contraintes
            st.executeUpdate(
                    """
               alter table PersonSurnoms 
                 drop constraint fk_Person_Surnoms_idPerson
               """);
            st.executeUpdate(
                    """
               alter table PersonSurnoms 
                 drop constraint fk_Person_Surnoms_idSurnom
               """);
            st.executeUpdate("drop table Person");
            st.executeUpdate("drop table Surnom");
            st.executeUpdate("drop table PersonSurnoms");
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.setAutoCommit(true);
        }
    }

    public static int trouvePerson(Connection con, String nom)
            throws SQLException {
        try ( PreparedStatement pst = con.prepareStatement(
                "select id from person where nom = ?")) {
            pst.setString(1, nom);
            ResultSet findP = pst.executeQuery();
            if (!findP.next()) {
                return -1;
            }
            return findP.getInt(1);
        }
    }

    public static class PersonNotFoundException extends Exception {

        public PersonNotFoundException(String nom) {
            super("La Person de nom \"" + nom + "\" n'existe pas");
        }
    }

    public static class PersonAlreadyExistsException extends Exception {

        public PersonAlreadyExistsException(String nom) {
            super("La Person de nom \"" + nom + "\" existe déjà");
        }
    }

    public static int createPerson(Connection con,
            String nom, java.sql.Date dateNaiss)
            throws SQLException, PersonAlreadyExistsException {
        if (trouvePerson(con, nom) != -1) {
            throw new PersonAlreadyExistsException(nom);
        }
        try ( PreparedStatement pst = con.prepareStatement(
                """
        insert into Person (nom,dateNaissance)
          values (?,?)
        """,PreparedStatement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, nom);
            pst.setDate(2, dateNaiss);
            pst.executeUpdate();
            // ci dessous : retrouver l'identificateur qui vient d'être crée
            ResultSet nouvellesCles = pst.getGeneratedKeys();
            // ici, il n'y a qu'une nouvelle clé.
            // s'il y avait plusieurs objets créés, on pourrait retrouver tous
            // les id correspondants en incluant le "next" dans un while
            nouvellesCles.next();
            return nouvellesCles.getInt(1);
        }
    }

    public static void ajouteSurnom(Connection con, String nom, String surnom)
            throws SQLException, PersonNotFoundException {
        int idPerson = trouvePerson(con, nom);
        if (idPerson == -1) {
            throw new PersonNotFoundException(nom);
        }
        try ( PreparedStatement pst = con.prepareStatement(
                "select id from Surnom where surnom = ?")) {
            con.setAutoCommit(false);
            pst.setString(1, surnom);
            ResultSet rsSurnom = pst.executeQuery();
            int idSurnom;
            if (rsSurnom.next()) {
                idSurnom = rsSurnom.getInt(1);
            } else {
                try ( PreparedStatement pst2 = con.prepareStatement(
                        "insert into Surnom (surnom) values (?)",
                        PreparedStatement.RETURN_GENERATED_KEYS)) {
                    pst2.setString(1, surnom);
                    pst2.executeUpdate();
                    // ci dessous : retrouver l'identificateur qui vient d'être crée
                    ResultSet nouvellesCles = pst2.getGeneratedKeys();
                    // ici, il n'y a qu'une nouvelle clé.
                    // s'il y avait plusieurs objets créés, on pourrait retrouver tous
                    // les id correspondants en incluant le "next" dans un while
                    nouvellesCles.next();
                    idSurnom = nouvellesCles.getInt(1);
                }
            }
            try ( PreparedStatement pst3 = con.prepareStatement(
                    "insert into PersonSurnoms (idPerson,idSurnom) values (?,?)")) {
                pst3.setInt(1, idPerson);
                pst3.setInt(2, idSurnom);
                pst3.executeUpdate();
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.setAutoCommit(true);
        }
    }

    public static void creeDonneesTest(Connection con) throws SQLException {
        String[][] donnees = new String[][]{
            // forme : {nom,dateNaissance,surnom1,surnom2...}
            {"toto", "1980-05-23", "titi"},
            {"Marley", "1945-02-06", "bob", "titi"},
            {"SansSurnom", "1930-03-07"},};
        for (String[] p : donnees) {
            java.sql.Date d = java.sql.Date.valueOf(p[1]);
            try {
                createPerson(con, p[0], d);
                for (int i = 2; i < p.length; i++) {
                    ajouteSurnom(con, p[0], p[i]);
                }
            } catch (PersonAlreadyExistsException | PersonNotFoundException ex) {
                throw new Error(ex);
            }
        }
    }

    public static void recreeTout(Connection con) throws SQLException {
        try {
            deleteSchema(con);
        } catch (SQLException ex) {
            System.out.println("Schema non supprimé : première création ?");
        }
        createSchema(con);
        creeDonneesTest(con);
    }

    public static void afficheToutesPersonnes(Connection con)
            throws SQLException {
        try ( Statement st = con.createStatement()) {
            ResultSet res = st.executeQuery(
                    "select * from person");
            while (res.next()) {
                // on peut accéder à une colonne par son nom
                int id = res.getInt("id");
                String nom = res.getString("nom");
                // on peut aussi y accéder par son numéro
                // !! numéro 1 pour la première
                java.sql.Date dn = res.getDate(3);
                System.out.println(id + " : " + nom + " né le " + dn);
            }
        }
    }

    public static void afficheCorrespondances(Connection con) throws SQLException {
        try ( Statement st = con.createStatement()) {
            ResultSet res = st.executeQuery(
                    """
                    select nom,surnom from Person
                      join PersonSurnoms on Person.id = idPerson
                      join Surnom on Surnom.id = idSurnom
                    order by nom,surnom
                    """);
            while (res.next()) {
                String nom = res.getString(1);
                String surnom = res.getString(2);
                System.out.println(nom + " : " + surnom);
            }
        }

    }

    public static void menuPrincipal(Connection con) throws SQLException {
        int rep = -1;
        while (rep != 0) {
            System.out.println("Menu Principal");
            System.out.println("--------------");
            System.out.println("1) (re)créer toute la BdD");
            System.out.println("2) ajouter une personne");
            System.out.println("3) ajouter un surnom à une personne");
            System.out.println("4) afficher toutes les personnes");
            System.out.println("5) afficher correspondances nom-surnom");
            rep = ConsoleFdB.entreeInt("Votre choix : ");

            if (rep == 1) {
                recreeTout(con);
            } else if (rep == 2) {
                String nom = ConsoleFdB.entreeString("nom : ");
                String dateNa = ConsoleFdB.entreeString("date de naissance (aaaa-mm-jj) :");
                java.sql.Date d = java.sql.Date.valueOf(dateNa);
                try {
                    createPerson(con, nom, d);
                } catch (PersonAlreadyExistsException ex) {
                    System.out.println("Impossible : le nom existe déjà");
                }
            } else if (rep == 3) {
                String nom = ConsoleFdB.entreeString("nom : ");
                String surnom = ConsoleFdB.entreeString("surnom : ");
                try {
                    ajouteSurnom(con, nom, surnom);
                } catch (PersonNotFoundException ex) {
                    System.out.println("Impossible : le nom n'existe pas");
                }
            } else if (rep == 4) {
                afficheToutesPersonnes(con);
            } else if (rep == 5) {
                afficheCorrespondances(con);
            }
        }
    }

    public static void main(String[] args) {
        try ( Connection con = connectPostgresql(
                "localhost", 5439,
                "postgres", "postgres", "pass")) {
            menuPrincipal(con);
        } catch (Exception ex) {
            throw new Error("Probleme SQL : " + ex.getMessage(), ex);
        }
    }
}
