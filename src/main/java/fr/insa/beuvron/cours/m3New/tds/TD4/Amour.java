/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.beuvron.cours.m3New.tds.TD4;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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

    public static void creeSchema(Connection con)
            throws SQLException {
        // je veux que le schema soit entierement créé ou pas du tout
        // je vais donc gérer explicitement une transaction
        con.setAutoCommit(false);
        try ( Statement st = con.createStatement()) {
            // creation des tables
            st.executeUpdate(
                    """
                    create table utilisateur (
                        id integer not null primary key
                        generated always as identity,
                        nom varchar(30) not null,
                        pass varchar(30) not null
                    )
                    """);
            st.executeUpdate(
                    """
                    create table aime (
                        u1 integer not null,
                        u2 integer not null
                    )
                    """);
            // je defini les liens entre les clés externes et les clés primaires
            // correspondantes
            st.executeUpdate(
                    """
                    alter table aime
                        add constraint fk_aime_u1
                        foreign key (u1) references utilisateur(id)
                    """);
            st.executeUpdate(
                    """
                    alter table aime
                        add constraint fk_aime_u2
                        foreign key (u2) references utilisateur(id)
                    """);
            // si j'arrive jusqu'ici, c'est que tout s'est bien passé
            // je confirme (commit) la transaction
            con.commit();
            // je retourne dans le mode par défaut de gestion des transaction :
            // chaque ordre au SGBD sera considéré comme une transaction indépendante
            con.setAutoCommit(true);
        } catch (SQLException ex) {
            // quelque chose s'est mal passé
            // j'annule la transaction
            con.rollback();
            // puis je renvoie l'exeption pour qu'elle puisse éventuellement
            // être gérée (message à l'utilisateur...)
            throw ex;
        }
    }

    // vous serez bien contents, en phase de développement de pouvoir
    // "repartir de zero" : il est parfois plus facile de tout supprimer
    // et de tout recréer que d'essayer de modifier le schema et les données
    public static void deleteSchema(Connection con) throws SQLException {
        // comme pour la création, je veux que le schema soit entierement supprimé 
        // ou pas du tout
        // je vais donc gérer explicitement une transaction
        con.setAutoCommit(false);
        try ( Statement st = con.createStatement()) {
            // pour être sûr de pouvoir supprimer, il faut d'abord supprimer les liens
            // puis les tables
            // suppression des liens
            st.executeUpdate(
                    """
                    alter table aime
                        drop constraint fk_aime_u1
                             """);
            st.executeUpdate(
                    """
                    alter table aime
                        drop constraint fk_aime_u2
                    """);
            // je peux maintenant supprimer les tables
            st.executeUpdate(
                    """
                    drop table aime
                    """);
            // je defini les liens entre les clés externes et les clés primaires
            // correspondantes
            st.executeUpdate(
                    """
                    drop table utilisateur
                    """);
            con.commit();
            con.setAutoCommit(true);
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        }

    }

    // pas de probleme particulier pour créer une nouvelle relation aime
    // en supposant que l'on connait les identificateurs des utilisateurs
    // on ne verra que plus tard la création d'un utilisateur, car on
    // veut pouvoir "récupérer" l'identificateur créé automatiquement
    // pour cela, il nous faut voir le principe de la recherche dans la
    // base de donnée
    public static void createAime(Connection con, int idU1, int idU2)
            throws SQLException {
        try ( PreparedStatement pst = con.prepareStatement(
                """
                insert into aime (u1,u2) values (?,?)
                """)) {
            pst.setInt(1, idU1);
            pst.setInt(2, idU2);
            pst.executeUpdate();
        }
    }

    // exemple de requete à la base de donnée
    public static void afficheTousLesUtilisateur(Connection con) throws SQLException {
        try ( Statement st = con.createStatement()) {
            // pour effectuer une recherche, il faut utiliser un "executeQuery"
            // et non un "executeUpdate".
            // un executeQuery retourne un ResultSet qui contient le résultat
            // de la recherche (donc une table avec quelques information supplémentaire)
            try ( ResultSet tlu = st.executeQuery("select * from utilisateur")) {
                // un ResultSet se manipule un peu comme un fichier :
                // - il faut le fermer quand on ne l'utilise plus
                //   d'où l'utilisation du try(...) ci-dessus
                // - il faut utiliser la méthode next du ResultSet pour passer
                //   d'une ligne à la suivante.
                //   . s'il y avait effectivement une ligne suivante, next renvoie true
                //   . si l'on était sur la dernière ligne, next renvoie false
                //   . au début, on est "avant la première ligne", il faut donc
                //     faire un premier next pour accéder à la première ligne
                //     Note : ce premier next peut renvoyer false si le résultat
                //            du select était vide
                // on va donc très souvent avoir un next
                //   . dans un if si l'on veut tester qu'il y a bien un résultat
                //   . dans un while si l'on veut traiter l'ensemble des lignes
                //     de la table résultat

                System.out.println("liste des utilisateurs :");
                System.out.println("------------------------");
                // ici, on veut lister toutes les lignes, d'où le while
                while (tlu.next()) {
                    // Ensuite, pour accéder à chaque colonne de la ligne courante,
                    // on a les méthode getInt, getString... en fonction du type
                    // de la colonne.

                    // on peut accéder à une colonne par son nom :
                    int id = tlu.getInt("id");
                    // ou par son numéro (la première colonne a le numéro 1)
                    String nom = tlu.getString(2);
                    String pass = tlu.getString(3);
                    System.out.println(id + " : " + nom + " (" + pass + ")");
                }
            }
        }

    }

    // exemple de requete à la base de donnée
    public static void afficheAmours(Connection con) throws SQLException {
        try ( Statement st = con.createStatement()) {
             try ( ResultSet tla = st.executeQuery(
                     """
                     select U1.nom,U2.nom
                        from aime 
                            join Utilisateur as U1 on aime.u1 = U1.id
                            join Utilisateur as U2 on aime.u2 = U2.id
                     """)) {
                System.out.println("liste des amours :");
                System.out.println("------------------");
                while (tla.next()) {
                   String nom1 = tla.getString(1);
                   String nom2 = tla.getString(2);
                    System.out.println(nom1 + " aime " + nom2);
                }
            }
        }

    }

    // lors de la création d'un utilisateur, l'identificateur est automatiquement
    // créé par le SGBD.
    // on va souvent avoir besoin de cet identificateur dans le programme,
    // par exemple pour gérer des liens "aime" entre utilisateur
    // vous trouverez ci-dessous la façon de récupérer les identificateurs
    // créés : ils se présentent comme un ResultSet particulier.
    public static int createUtilisateur(Connection con, String nom, String pass)
            throws SQLException {
        // lors de la creation du PreparedStatement, il faut que je précise
        // que je veux qu'il conserve les clés générées
        try ( PreparedStatement pst = con.prepareStatement(
                """
                insert into utilisateur (nom,pass) values (?,?)
                """, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, nom);
            pst.setString(2, pass);
            pst.executeUpdate();

            // je peux alors récupérer les clés créées comme un result set :
            try ( ResultSet rid = pst.getGeneratedKeys()) {
                // et comme ici je suis sur qu'il y a une et une seule clé, je
                // fait un simple next 
                rid.next();
                // puis je récupère la valeur de la clé créé qui est dans la
                // première colonne du ResultSet
                int id = rid.getInt(1);
                return id;
            }
        }
    }

    public static void recreeToutEtAffiche(Connection con) {
        // j'essaye d'abord de tout supprimer
        try {
            deleteSchema(con);
            System.out.println("ancien schéma supprimé");
        } catch (SQLException ex) {
            System.out.println("pas de suppression d'un ancien schéma");
        }
        try {
            creeSchema(con);
            List<Integer> ids = new ArrayList<>();
            ids.add(createUtilisateur(con, "toto", "p1"));
            ids.add(createUtilisateur(con, "bob", "p2"));
            ids.add(createUtilisateur(con, "bill", "p3"));
            // toto aime bob et bill
            createAime(con,ids.get(0), ids.get(1));
            createAime(con,ids.get(0), ids.get(2));
            // bob aime bill
            createAime(con,ids.get(1), ids.get(2));
            // bill aime toto
            createAime(con,ids.get(2), ids.get(1));
            afficheTousLesUtilisateur(con);
            System.out.println("");
            afficheAmours(con);
        } catch (SQLException ex) {
            throw new Error(ex);
        }
    }

    public static void main(String[] args) {
        try ( Connection con = connect()) {
            System.out.println("connecté !!!");
            recreeToutEtAffiche(con);
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

}
