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
package fr.insa.beuvron.cours.m3New.tds.TD1;

import fr.insa.beuvron.cours.m3.database.ConnectSGBD;
import fr.insa.beuvron.cours.m3.database.ResultSetUtils;
import fr.insa.beuvron.utils.ConsoleFdB;
import java.io.Console;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * creation du schema correspondant au td1 info M3 2021. Syntaxe SQL la plus
 * standard possible, mais sinon pour postgresql. liste des types en postgresql
 * : https://www.postgresql.org/docs/current/datatype.html
 *
 * pour la génération de noms aléatoires, voir :
 * https://www.fakenamegenerator.com
 *
 * @author francois
 */
public class BdDTD1 {

    public static final String[][] tables = new String[][]{
        {"Personne",
            """
            CREATE TABLE Personne (
              id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
              nom VARCHAR(50) NOT NULL,
              prenom VARCHAR(30),
              dateNaissance DATE )
            """},
        {"Module",
            """
            CREATE TABLE Module (
              id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
              intitule VARCHAR(50) NOT NULL,
              description TEXT,
              nbrplace INTEGER,
              responsable INTEGER)
            """},
        {"Semestre",
            """
            CREATE TABLE Semestre (
              id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
              annee INTEGER NOT NULL,
              semestre INTEGER NOT NULL )
            """},
        {"Ouvert",
            """
            CREATE TABLE Ouvert (
              module INTEGER NOT NULL,
              semestre INTEGER NOT NULL )
            """},
        {"Inscription",
            """
            CREATE TABLE Inscription (
              etudiant INTEGER NOT NULL,
              module INTEGER NOT NULL,
              semestre INTEGER NOT NULL )
            """}
    };

    public static final String[][] foreignKeys = new String[][]{
        {"Module", "responsable", "Personne", "id"},
        {"Ouvert", "module", "Module", "id"},
        {"Ouvert", "semestre", "Semestre", "id"},
        {"Inscription", "etudiant", "Personne", "id"},
        {"Inscription", "module", "Module", "id"},
        {"Inscription", "semestre", "Semestre", "id"}
    };

    public static void createSchema(Connection con, boolean echo) {
        try {
            con.setAutoCommit(false);
            Statement st = con.createStatement();

//            st.executeUpdate(tables[0][1]);
            for (String[] table : tables) {
                if (echo) {
                    System.out.println(table[1] + ";\n");
                }
                st.executeUpdate(table[1]);
            }

            for (String[] fkey : foreignKeys) {
                String fk = """
                            ALTER TABLE %tablefk%
                              ADD CONSTRAINT %constraintId%
                                FOREIGN KEY (%fk%)
                                REFERENCES %tablepk% (%pk%)
                                  ON DELETE RESTRICT
                                  ON UPDATE RESTRICT
                            """
                        .replace("%tablefk%", fkey[0])
                        .replace("%constraintId%", "fk_" + fkey[0] + "_" + fkey[1])
                        .replace("%fk%", fkey[1])
                        .replace("%tablepk%", fkey[2])
                        .replace("%pk%", fkey[3]);
                if (echo) {
                    System.out.println(fk + ";\n");
                }
                st.executeUpdate(fk);

            }
            con.commit();
        } catch (SQLException ex) {
            try {
                con.rollback();
            } catch (SQLException ex1) {
            }
            throw new Error(ex);
        }
    }

    public static void deleteSchema(Connection con, boolean echo) {
        try {
            con.setAutoCommit(false);
            Statement st = con.createStatement();

            for (String[] fkey : foreignKeys) {
                String fk = """
                            ALTER TABLE %tablefk%
                              DROP CONSTRAINT %constraintId%
                            """
                        .replace("%tablefk%", fkey[0])
                        .replace("%constraintId%", "fk_" + fkey[0] + "_" + fkey[1]);
                if (echo) {
                    System.out.println(fk + ";\n");
                }
                st.executeUpdate(fk);
            }

            for (String[] table : tables) {
                st.executeUpdate("DROP TABLE " + table[0]);
                if (echo) {
                    System.out.println("DROP TABLE " + table[0] + ";\n");
                }
            }
            con.commit();
        } catch (SQLException ex) {
            try {
                con.rollback();
            } catch (SQLException ex1) {
            }
            throw new Error(ex);
        }

    }

    /**
     * repris de {@link fr.insa.beuvron.utils.probas.TiragesAlea}
     *
     * @param min
     * @param max
     * @param r
     * @return
     */
    public static LocalDate dateAleaBetween(LocalDate min, LocalDate max, Random r) {
        long minDay = min.toEpochDay();
        long maxDay = max.toEpochDay();
        long delta = (long) (r.nextDouble() * (maxDay - minDay + 1));
        return min.plusDays(delta);
    }

    public static void createPersonnesAlea(Connection con, int nbr, LocalDate minNaissance, LocalDate maxNaissance,
            Random r) {
        List<String> noms = ExemplePersonnesAlea.nomsAlea();
        List<String> prenoms = ExemplePersonnesAlea.prenomsAlea();
        try {
            con.setAutoCommit(false);
            PreparedStatement pst = con.prepareStatement(
                    """
               INSERT INTO Personne (nom,prenom,dateNaissance)
                 VALUES (?,?,?)
               """);
            for (int i = 0; i < nbr; i++) {
                pst.setString(1, noms.get(r.nextInt(noms.size())));
                pst.setString(2, prenoms.get(r.nextInt(prenoms.size())));
                LocalDate dalea = dateAleaBetween(minNaissance, maxNaissance, r);
                Date asDate = Date.valueOf(dalea);
                pst.setDate(3, asDate);
                pst.executeUpdate();
            }
            con.commit();
        } catch (SQLException ex) {
            try {
                con.rollback();
            } catch (SQLException ex1) {
            }
            throw new Error(ex);
        }
    }

    public static List<Integer> getAllIds(Connection con, String table) throws SQLException {
        try (Statement st = con.createStatement();
                ResultSet rres = st.executeQuery(
                        "select id from " + table)) {
            List<Integer> res = new ArrayList<>();
            while (rres.next()) {
                res.add(rres.getInt("id"));
            }
            return res;
        }
    }

    public static void createModulesAlea(Connection con, int nbr,
            int minPlace, int maxPlace, double probaAvoirResponsable,
            Random r) {
        try {
            List<Integer> persIds = getAllIds(con, "Personne");
            con.setAutoCommit(false);
            PreparedStatement pst = con.prepareStatement(
                    """
               INSERT INTO Module (intitule,description,nbrPlace,responsable)
                 VALUES (?,?,?,?)
               """);
            for (int i = 0; i < nbr; i++) {
                pst.setString(1, "mod " + String.format("%03d", r.nextInt(1000)));
                pst.setString(2, Integer.toUnsignedString(r.nextInt(), 35));
                pst.setInt(3, minPlace + r.nextInt(maxPlace - minPlace + 1));
                if (r.nextDouble() < probaAvoirResponsable && persIds.size() > 0) {
                    pst.setInt(4, persIds.get(r.nextInt(persIds.size())));
                } else {
                    pst.setNull(4, Types.INTEGER);
                }
                pst.executeUpdate();
            }
            con.commit();
        } catch (SQLException ex) {
            try {
                con.rollback();
            } catch (SQLException ex1) {
            }
            throw new Error(ex);
        }
    }

    public static void createSemestresAlea(Connection con, int minAnnee, int maxAnnee,
            Random r) {
        try {
            con.setAutoCommit(false);
            PreparedStatement pst = con.prepareStatement(
                    """
               INSERT INTO Semestre (annee,semestre)
                 VALUES (?,?)
               """);
            for (int i = minAnnee; i <= maxAnnee; i++) {
                for (int s = 1; s <= 2; s++) {
                    pst.setInt(1, i);
                    pst.setInt(2, s);
                    pst.executeUpdate();
                }
            }
            con.commit();
        } catch (SQLException ex) {
            try {
                con.rollback();
            } catch (SQLException ex1) {
            }
            throw new Error(ex);
        }
    }

    public static boolean ouvertureExists(Connection con, int moduleId, int semestreId) throws SQLException {
        try (Statement st = con.createStatement();
                ResultSet test = st.executeQuery("select * from ouvert where "
                        + "module = " + moduleId
                        + " and semestre = " + semestreId)) {
            return test.next();
        }
    }

    public static void createOuverturesAlea(Connection con, int nbr, Random r) {
        try {
            List<Integer> modIds = getAllIds(con, "Module");
            List<Integer> semIds = getAllIds(con, "Semestre");
            con.setAutoCommit(false);
            PreparedStatement pst = con.prepareStatement(
                    """
               INSERT INTO Ouvert (module,semestre)
                 VALUES (?,?)
               """);
            long maxTry = nbr * 10;
            long i = 0;
            int ok = 0;
            while (ok < nbr && i < maxTry) {
                int mod = modIds.get(r.nextInt(modIds.size()));
                int sem = semIds.get(r.nextInt(semIds.size()));
                if (! ouvertureExists(con, mod, sem)) {
                    pst.setInt(1, mod);
                    pst.setInt(2, sem);
                    pst.executeUpdate();
                    ok ++;
                }
                i ++;
            }
            if (ok < nbr) {
                System.out.println("WARNING : impossible de créer plus de " + ok + " ouvertures en " + maxTry + " essais");
            }
            con.commit();
        } catch (SQLException ex) {
            try {
                con.rollback();
            } catch (SQLException ex1) {
            }
            throw new Error(ex);
        }
    }

    public static boolean inscriptionExists(Connection con,int personneId, int moduleId, int semestreId) throws SQLException {
        try (Statement st = con.createStatement();
                ResultSet test = st.executeQuery("select * from inscription where "
                        + "etudiant = " + personneId
                        + " and module = " + moduleId
                        + " and semestre = " + semestreId)) {
            return test.next();
        }
    }

    public static void createInscriptionsAlea(Connection con, int nbr, Random r) {
        try {
            List<Integer> personIds = getAllIds(con, "Personne");
            List<Integer> modIds = getAllIds(con, "Module");
            List<Integer> semIds = getAllIds(con, "Semestre");
            con.setAutoCommit(false);
            PreparedStatement pst = con.prepareStatement(
                    """
               INSERT INTO Inscription (etudiant,module,semestre)
                 VALUES (?,?,?)
               """);
            long  maxTry = nbr * 10;
            long i = 0;
            int ok = 0;
            while (ok < nbr && i < maxTry) {
                int etud = personIds.get(r.nextInt(personIds.size()));
                int mod = modIds.get(r.nextInt(modIds.size()));
                int sem = semIds.get(r.nextInt(semIds.size()));
                if (! inscriptionExists(con, etud,mod, sem)) {
                    pst.setInt(1, etud);
                    pst.setInt(2, mod);
                    pst.setInt(3, sem);
                    pst.executeUpdate();
                    ok ++;
                }
                i ++;
            }
            if (ok < nbr) {
                System.out.println("WARNING : impossible de créer plus de " + ok + " inscriptions en " + maxTry + " essais");
            }
            con.commit();
        } catch (SQLException ex) {
            try {
                con.rollback();
            } catch (SQLException ex1) {
            }
            throw new Error(ex);
        }
    }
    
    public static void createExempleTD2(Connection con) {
        Random r = new Random(885214156);
        createPersonnesAlea(con, 10, LocalDate.parse("1960-01-01"), LocalDate.parse("2005-01-01"), r);
        createModulesAlea(con, 6, 20, 200, 0.8, r);
        createSemestresAlea(con, 2021, 2021, r);
        createOuverturesAlea(con, 10, r);
        createInscriptionsAlea(con, 30, r);      
    }

    public static void afficheSQLQuery(Connection con, String query) throws SQLException {
        try (Statement st = con.createStatement(); ResultSet res = st.executeQuery(query)) {
            System.out.println(ResultSetUtils.formatResultSet(res));
        }
    }

    public static LocalDate entreeDate(LocalDate defaut) {
        LocalDate res = null;
        while (res == null) {
            String date = ConsoleFdB.entreeString(("entrez une date au format aaaa-mm-jj : (ou rien pour defaut = " + defaut + " : "));
            if (date.trim().length() == 0) {
                res = defaut;
            } else {
                try {
                    res = LocalDate.parse(date);
                } catch (DateTimeParseException ex) {
                    System.out.println("format invalide");
                }
            }
        }
        return res;
    }

    public static void menuAffTables(Connection con) {
        int rep = -1;
        while (rep != 0) {
            try {
                System.out.println("Table à afficher : ");
                System.out.println("1) Personne");
                System.out.println("2) Module");
                System.out.println("3) Semestre");
                System.out.println("4) Ouvert");
                System.out.println("5) Inscription");
                System.out.println("6) Toutes les tables");
                System.out.println("0) Quitter");
                rep = ConsoleFdB.entreeInt("votre choix : ");
                if (rep == 1) {
                    afficheSQLQuery(con, "select * from Personne");
                } else if (rep == 2) {
                    afficheSQLQuery(con, "select * from Module");
                } else if (rep == 3) {
                    afficheSQLQuery(con, "select * from Semestre");
                } else if (rep == 4) {
                    afficheSQLQuery(con, "select * from Ouvert");
                } else if (rep == 5) {
                    afficheSQLQuery(con, "select * from Inscription");
                } else if (rep == 6) {
                    for (String table : new String[] {
                        "Personne",
                        "Module",
                        "Semestre",
                        "Ouvert",
                        "Inscription",
                    }) {
                    afficheSQLQuery(con, "select * from " + table);
                    }
                } else if (rep != 0) {
                    System.out.println("choix invalide : " + rep);
                }
            } catch (SQLException ex) {
                System.out.println("Erreur : " + ex.getLocalizedMessage());

            }
        }

    }

    public static void menuText(Connection con) {
        int rep = -1;
        Random r = new Random();
        while (rep != 0) {
            try {
                System.out.println("Voulez-vous : ");
                System.out.println("1) créer le schema");
                System.out.println("2) supprimer tout");
                System.out.println("3) créer des personnes");
                System.out.println("4) créer des modules");
                System.out.println("5) créer des semestres");
                System.out.println("6) créer des liens ouvert (module,semestre)");
                System.out.println("7) créer des liens inscription (personne,module,semestre)");
                System.out.println("8) recherche (select) sql quelconque");
                System.out.println("9) afficher des tables");
                System.out.println("10) créer les données de la correction du TD2");
                System.out.println("0) Quitter");
                rep = ConsoleFdB.entreeInt("votre choix : ");
                if (rep == 1) {
                    createSchema(con, false);
                } else if (rep == 2) {
                    deleteSchema(con, false);
                } else if (rep == 3) {
                    int nbr = ConsoleFdB.entreeEntier("combien de personnes : ");
                    System.out.println("date de naissance minimale : ");
                    LocalDate dmin = entreeDate(LocalDate.parse("1960-01-01"));
                    System.out.println("date de naissance maximale : ");
                    LocalDate dmax = entreeDate(LocalDate.parse("2005-01-01"));
                    createPersonnesAlea(con, nbr, dmin, dmax, r);
                } else if (rep == 4) {
                    int nbr = ConsoleFdB.entreeEntier("combien de modules : ");
                    int min = ConsoleFdB.entreeEntier("nbr places min : ");
                    int max = ConsoleFdB.entreeEntier("nbr places max : ");
                    double p = ConsoleFdB.entreeDouble("proba qu'un module possède un responsable : ");
                    createModulesAlea(con, nbr, min, max, p, r);
                } else if (rep == 5) {
                    int amin = ConsoleFdB.entreeEntier("année min : ");
                    int amax = ConsoleFdB.entreeEntier("année max : ");
                    createSemestresAlea(con, amin,amax, r);
                } else if (rep == 6) {
                    int nbr = ConsoleFdB.entreeEntier("combien d'ouvertures : ");
                    createOuverturesAlea(con, nbr, r);
                } else if (rep == 7) {
                    int nbr = ConsoleFdB.entreeEntier("combien d'inscriptions : ");
                    createInscriptionsAlea(con, nbr, r);
                } else if (rep == 8) {
                    String sql = ConsoleFdB.entreeString("entrez un select : ");
                    afficheSQLQuery(con, sql );
                } else if (rep == 9) {
                    menuAffTables(con);
                } else if (rep == 10) {
                    createExempleTD2(con);
                } else if (rep != 0) {
                    System.out.println("choix invalide : " + rep);
                }
            } catch (SQLException ex) {
                System.out.println("Erreur : " + ex.getLocalizedMessage());

            }
        }
    }

    public static void main(String[] args) {
        Connection con = ConnectSGBD.connectionLocalPostgresql();
        menuText(con);
    }


}
