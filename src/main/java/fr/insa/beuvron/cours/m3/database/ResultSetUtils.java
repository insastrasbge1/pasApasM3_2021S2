/*
    Copyright 2000-2011 Francois de Bertrand de Beuvron

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
package fr.insa.beuvron.cours.m3.database;

import fr.insa.beuvron.utils.StringUtil;
import fr.insa.beuvron.utils.latex.LatexEscape;
import fr.insa.beuvron.utils.latex.LatexMode;
import fr.insa.beuvron.utils.matrice.MatriceToText;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.text.StringEscapeUtils;

/**
 *
 * @author francois
 */
public class ResultSetUtils {

    /**
     * retourne une représentation d'un 'ResultSet' sous forme d'une table HTML.
     * <p>
     * remarquez que la table affichée est assez basique mais que la méthode est
     * trés générale : elle peut afficher n'importe quel ResultSet, et pas
     * seulement une liste de Personne
     * </p>
     *
     * @param rs le ResultSet
     */
    public static String formatResultSetAsHTMLTable(ResultSet rs) throws Exception {
        StringBuilder res = new StringBuilder();
        // il est également possible d'avoir des information sur la 'structure'
        // du Resultset : nombre de colonnes, nom des colonnes ...
        // toutes ces informations sont contenues dans le 'metadata' associé
        // au ResultSet
        String headerStyle = "STYLE=\"border-top: 3px solid #000000;"
                + " border-bottom: 3px solid #000000;"
                + " border-left: 1px solid #000000;"
                + " border-right: 1px solid #000000\""
                + " ALIGN=LEFT";
        String normalStyle = "STYLE=\"border-bottom: 1px solid #000000;"
                + " border-left: 1px solid #000000;"
                + " border-right: 1px solid #000000\""
                + "ALIGN=LEFT";
        ResultSetMetaData metadata = rs.getMetaData();
        int nombreColonnes = metadata.getColumnCount();
        res.append("<TABLE CELLSPACING=0 COLS=" + nombreColonnes + " BORDER=1>\n");
        res.append("<TBODY>\n");
        res.append("<TR>\n");
        for (int i = 1; i <= nombreColonnes; i++) {
            res.append("  <TD " + headerStyle + ">" + metadata.getColumnName(i) + "</TD>\n");
        }
        res.append("</TR>\n");
        while (rs.next()) {
            res.append("<TR>\n");
            for (int i = 1; i <= nombreColonnes; i++) {
                res.append("  <TD " + normalStyle + ">"
                        + StringEscapeUtils.escapeHtml4("" + rs.getObject(i)) + "</TD>\n");
            }
            res.append("</TR>\n");
        }
        res.append("</TBODY>\n");
        res.append("</TABLE>\n");
        return res.toString();
    }

    /**
     * retourne une représentation d'un 'ResultSet' sous forme d'une table HTML.
     * <p>
     * remarquez que la table affichée est assez basique mais que la méthode est
     * trés générale : elle peut afficher n'importe quel ResultSet, et pas
     * seulement une liste de Personne
     * </p>
     *
     * @param rs le ResultSet
     */
    public static String formatResultSetAsLatexTabular(ResultSet rs) throws Exception {
        StringBuilder res = new StringBuilder();
        res.append("\\begin{tabular}{|");
        ResultSetMetaData metadata = rs.getMetaData();
        int nombreColonnes = metadata.getColumnCount();
        res.append(StringUtil.mult("l|", nombreColonnes));
        res.append("}\n");
        res.append("\\hline\n");
        for (int i = 1; i <= nombreColonnes; i++) {
            res.append(LatexEscape.escapeLatex(metadata.getColumnName(i), LatexMode.TextMode));
            if (i < nombreColonnes) {
                res.append(" & ");
            }
        }
        res.append("\\\\\n");
        res.append("\\hline\n");
        res.append("\\hline\n");
        while (rs.next()) {
            for (int i = 1; i <= nombreColonnes; i++) {
                res.append(LatexEscape.escapeLatex("" + rs.getObject(i), LatexMode.TextMode));
                if (i < nombreColonnes) {
                    res.append(" & ");
                }
            }
            res.append("\\\\\n");
            res.append("\\hline\n");
        }
        res.append("\\end{tabular}\n");
        return res.toString();
    }

    /**
     * retourne une représentation d'un 'ResultSet' au format texte.
     * <p>
     * remarquez que le texte affiché n'est pas beau (pas d'alignement en
     * particulier) mais que la méthode est trés générale : elle peut afficher
     * n'importe quel ResultSet, et pas seulement une liste de Personne
     * </p>
     *
     * @param rs le ResultSet
     */
    public static String formatResultSetOld(ResultSet rs) throws Exception {
        StringBuilder res = new StringBuilder();
        // il est également possible d'avoir des information sur la 'structure'
        // du Resultset : nombre de colonnes, nom des colonnes ...
        // toutes ces informations sont contenues dans le 'metadata' associé
        // au ResultSet
        ResultSetMetaData metadata = rs.getMetaData();
        int nombreColonnes = metadata.getColumnCount();
        res.append("|");
        for (int i = 1; i <= nombreColonnes; i++) {
            res.append(metadata.getColumnName(i));
            res.append("|");
        }
        res.append("\n-------------------------------------------------\n");
        while (rs.next()) {
            res.append("|");
            for (int i = 1; i <= nombreColonnes; i++) {
                res.append(rs.getObject(i));
                res.append("|");
            }
            res.append("\n");
        }
        res.append("-------------------------------------------------\n");
        return res.toString();
    }

    public static String formatResultSet(ResultSet rs) throws SQLException {
        List<List<String>> collect = new ArrayList<>();
        ResultSetMetaData metadata = rs.getMetaData();
        int nombreColonnes = metadata.getColumnCount();
        List<String> one = new ArrayList<>();
        for (int i = 1; i <= nombreColonnes; i++) {
            one.add(metadata.getColumnName(i));
        }
        collect.add(one);
        while (rs.next()) {
            one = new ArrayList<>();
            for (int i = 1; i <= nombreColonnes; i++) {
                Object oneCol = rs.getObject(i);
                if (oneCol != null) {
                    one.add(oneCol.toString());
                } else {
                    one.add("NULL");
                }
            }
            collect.add(one);
        }
        return MatriceToText.formatMat(collect, true);
    }

    public static class ResultSetAsArrays {

        public String[] columnNames;
        public Object[][] values;
    }

    /**
     * retourne le contenu du ResultSet dans deux tableau : . un tableau des
     * noms de colonnes . un tableau 2D d'objet correspondant au contenu du
     * resultset
     *
     * @param rs
     * @return
     */
    public static ResultSetAsArrays fromResultSetToArrays(ResultSet rs) throws SQLException {
        ResultSetAsArrays res = new ResultSetAsArrays();
        ResultSetMetaData metadata = rs.getMetaData();
        int nombreColonnes = metadata.getColumnCount();
        res.columnNames = new String[nombreColonnes];
        for (int i = 0; i < nombreColonnes; i++) {
            res.columnNames[i] = metadata.getColumnName(i + 1);
        }
        res.values = fromResultSetToMatrix(rs);
        return res;

    }

    /**
     * retourne le contenu d'un ResultSet comme un tableau (lignes) de tableaux
     * (colonnes) d'objets
     */
    public static Object[][] fromResultSetToMatrix(ResultSet rs) throws SQLException {
        List<Object[]> resl = new ArrayList<Object[]>();
        ResultSetMetaData metadata = rs.getMetaData();
        int nombreColonnes = metadata.getColumnCount();
        while (rs.next()) {
            Object[] oneLine = new Object[nombreColonnes];
            for (int i = 1; i <= nombreColonnes; i++) {
                oneLine[i - 1] = rs.getObject(i);
            }
            resl.add(oneLine);
        }
        Object[][] res = new Object[resl.size()][];
        for (int i = 0; i < res.length; i++) {
            res[i] = resl.get(i);
        }
        return res;

    }

    /**
     * retourne le contenu d'une colonne comme un tableau d'objets
     */
    public static Object[] fromResultSetColumnToArray(ResultSet rs, int col) throws SQLException {
        List<Object> res = new ArrayList<Object>();
        while (rs.next()) {
            res.add(rs.getObject(col));
        }
        return res.toArray();

    }
}
