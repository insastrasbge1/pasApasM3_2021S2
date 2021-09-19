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

import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

/**
 * Personnes "aléatoires" générées sur https://www.fakenamegenerator.com. pour
 * passer de la sortie csv à un String[][] java, par pattern :  * {@code 
 * ([^"]),  --> $1",  : ajout de " avant virgules (sauf si déjà)
 * ,([^"])  --> ,"$1  : ajout de " après virgules (sauf si déjà)
 *
 * (?m)^(.*)$    --> {"$1"},   : ajout de {" en début et "}, en fin de ligne  (?m) pour forcer le match multi-ligne
 *}
  * @author francois
 */
public class ExemplePersonnesAlea {

    public static final String[][] PERSONNES_ALEA = new String[][]{
        {"1", "Pauline", "Giroux", "PaulineGiroux@armyspy.com", "42 rue de Geneve", "AMIENS", "80080", "5/7/1990"},
        {"2", "Peppin", "David", "PeppinDavid@dayrep.com", "28 Rue Marie De Médicis", "CANNES", "06400", "11/1/1969"},
        {"3", "Aurore", "Benjamin", "AuroreBenjamin@gustr.com", "33 rue Lenotre", "RENNES", "35200", "10/18/1994"},
        {"4", "Dominic", "Rochefort", "DominicRochefort@armyspy.com", "37 rue Michel Ange", "LE HAVRE", "76620", "7/8/1946"},
        {"5", "Gérard", "Parenteau", "GerardParenteau@einrot.com", "53 avenue Jules Ferry", "STAINS", "93240", "6/29/1942"},
        {"6", "Melusina", "Simon", "MelusinaSimon@dayrep.com", "62 rue de Raymond Poincaré", "NEUILLY-SUR-SEINE", "92200", "1/27/1984"},
        {"7", "Calandre", "Béland", "CalandreBeland@rhyta.com", "25 rue du Président Roosevelt", "SCHILTIGHEIM", "67300", "3/20/1955"},
        {"8", "Édith", "Caya", "EdithCaya@einrot.com", "16 avenue Jean Portalis", "TROYES", "10000", "3/25/2002"},
        {"9", "Eugène", "Chrétien", "EugeneChretien@armyspy.com", "90 Rue Frédéric Chopin", "VERSAILLES", "78000", "3/16/1956"},
        {"10", "Searlas", "Guay", "SearlasGuay@cuvox.de", "55 Rue Roussy", "ORLÉANS", "45000", "1/28/1975"},
        {"11", "Pierrette", "Gadbois", "PierretteGadbois@jourrapide.com", "15 rue La Boétie", "PARIS", "75013", "5/4/1984"},
        {"12", "Zurie", "Bonami", "ZurieBonami@superrito.com", "59 rue Ernest Renan", "CHOLET", "49300", "5/23/1939"},
        {"13", "Alphonsine", "Cloutier", "AlphonsineCloutier@dayrep.com", "2 rue des Coudriers", "MOULINS", "03000", "10/3/1994"},
        {"14", "Pierpont", "Dumont", "PierpontDumont@dayrep.com", "48 route de Lyon", "JOUÉ-LÈS-TOURS", "37300", "11/5/2001"},
        {"15", "Yvon", "Primeau", "YvonPrimeau@fleckens.hu", "57 rue du Clair Bocage", "LA SEYNE-SUR-MER", "83500", "9/21/1981"},
        {"16", "Albertine", "Rossignol", "AlbertineRossignol@fleckens.hu", "64 rue de Raymond Poincaré", "NARBONNE", "11100", "10/28/1968"},
        {"17", "Christien", "Laderoute", "ChristienLaderoute@einrot.com", "30 rue Isambard", "FRESNES", "94260", "5/29/1980"},
        {"18", "Lance", "Gosselin", "LanceGosselin@gustr.com", "22 rue Sadi Carnot", "AUBERVILLIERS", "93300", "10/19/1970"},
        {"19", "Somerville", "Brisebois", "SomervilleBrisebois@einrot.com", "18 avenue du Marechal Juin", "SAINT-LAURENT-DU-VAR", "06700", "8/15/1959"},
        {"20", "Auriville", "Beauchemin", "AurivilleBeauchemin@superrito.com", "19 rue des Nations Unies", "SAINT-BENOÎT", "97470", "3/23/1956"},
        {"21", "Hugues", "Houle", "HuguesHoule@cuvox.de", "80 boulevard Amiral Courbet", "ORVAULT", "44700", "10/16/1965"},
        {"22", "Ogier", "Josseaume", "OgierJosseaume@cuvox.de", "26 boulevard d'Alsace", "VAULX-EN-VELIN", "69120", "3/7/1959"},
        {"23", "Armina", "Massé", "ArminaMasse@dayrep.com", "72 rue Charles Corbeau", "ÉVREUX", "27000", "4/18/1948"},
        {"24", "Océane", "Lamare", "OceaneLamare@armyspy.com", "76 boulevard d'Alsace", "VANVES", "92170", "4/15/1951"},
        {"25", "Beltane", "Pariseau", "BeltanePariseau@fleckens.hu", "79 rue Michel Ange", "LE KREMLIN-BICÊTRE", "94270", "1/8/1938"},
        {"26", "Maryse", "Guibord", "MaryseGuibord@gustr.com", "16 rue des six frères Ruellan", "SAINT-SÉBASTIEN-SUR-LOIRE", "44230", "1/21/1993"},
        {"27", "Émilie", "Desnoyers", "EmilieDesnoyers@dayrep.com", "86 rue Petite Fusterie", "BREST", "29200", "7/5/1962"},
        {"28", "Melville", "Duplessis", "MelvilleDuplessis@armyspy.com", "57 rue de la Boétie", "POINTE-À-PITRE", "97110", "6/6/1982"},
        {"29", "Michel", "Baril", "MichelBaril@rhyta.com", "98 Place de la Gare", "COLOMIERS", "31770", "11/14/1989"},
        {"30", "Bertrand", "Turgeon", "BertrandTurgeon@gustr.com", "92 rue du Président Roosevelt", "SAVIGNY-SUR-ORGE", "91600", "3/12/1949"},
        {"31", "Landers", "Lemieux", "LandersLemieux@jourrapide.com", "27 rue des Lacs", "HÉROUVILLE-SAINT-CLAIR", "14200", "3/5/1940"},
        {"32", "La Roux", "Cotuand", "LaRouxCotuand@armyspy.com", "77 rue Banaudon", "LYON", "69005", "1/30/1968"},
        {"33", "Serge", "Givry", "SergeGivry@jourrapide.com", "18 Place de la Madeleine", "PARIS", "75009", "6/6/1955"},
        {"34", "Ignace", "Quiron", "IgnaceQuiron@teleworm.us", "27 boulevard d'Alsace", "VERDUN", "55100", "9/20/1986"},
        {"35", "Aleron", "Fréchette", "AleronFrechette@superrito.com", "38 rue Beauvau", "MARSEILLE", "13002", "10/15/1986"},
        {"36", "Fortun", "Savoie", "FortunSavoie@fleckens.hu", "19 Boulevard de Normandie", "FONTAINE", "38600", "2/18/1953"},
        {"37", "Fusberta", "Dufour", "FusbertaDufour@rhyta.com", "88 boulevard Bryas", "CROIX", "59170", "11/23/1963"},
        {"38", "Merle", "Dupont", "MerleDupont@jourrapide.com", "12 rue Marie de Médicis", "BIARRITZ", "64200", "1/9/1987"},
        {"39", "Ferrau", "Déziel", "FerrauDeziel@superrito.com", "75 rue du Général Ailleret", "LES ABYMES", "97139", "3/24/1961"},
        {"40", "Joseph", "Laforge", "JosephLaforge@fleckens.hu", "85 rue Gustave Eiffel", "RIS-ORANGIS", "91130", "10/28/1993"},
        {"41", "David", "Laderoute", "DavidLaderoute@dayrep.com", "43 rue de Groussay", "ROCHEFORT", "17300", "5/14/1981"},
        {"42", "Eugène", "Busson", "EugeneBusson@einrot.com", "51 rue Pierre Motte", "SAINTE-FOY-LÈS-LYON", "69110", "7/15/1945"},
        {"43", "Joséphine", "Chandonnet", "JosephineChandonnet@armyspy.com", "79 Rue Hubert de Lisle", "LONS-LE-SAUNIER", "39000", "12/10/1950"},
        {"44", "Ambra", "Bourget", "AmbraBourget@einrot.com", "92 avenue de l'Amandier", "BORDEAUX", "33000", "3/19/1991"},
        {"45", "Merle", "Lapointe", "MerleLapointe@superrito.com", "32 Rue de Strasbourg", "CLERMONT-FERRAND", "63100", "8/29/1988"},
        {"46", "Arthur", "Sevier", "ArthurSevier@superrito.com", "68 quai Saint-Nicolas", "TOURNEFEUILLE", "31170", "5/24/1943"},
        {"47", "Quennel", "Verreau", "QuennelVerreau@einrot.com", "8 Rue du Limas", "BASTIA", "20200", "8/18/1989"},
        {"48", "Aubrey", "Gauthier", "AubreyGauthier@einrot.com", "14 place Stanislas", "NANTES", "44100", "2/9/1957"},
        {"49", "Gill", "Vaillancour", "GillVaillancour@superrito.com", "17 rue Cazade", "DREUX", "28100", "9/26/1985"},
        {"50", "Talon", "Rhéaume", "TalonRheaume@gustr.com", "75 Rue Bonnet", "WATTRELOS", "59150", "4/3/1996"},
        {"51", "Latimer", "Morneau", "LatimerMorneau@teleworm.us", "40 Place du Jeu de Paume", "VIGNEUX-SUR-SEINE", "91270", "3/26/1995"},
        {"52", "Chapin", "Sansouci", "ChapinSansouci@superrito.com", "93 rue Bonneterie", "MIRAMAS", "13140", "5/25/1993"},
        {"53", "Cosette", "Chesnay", "CosetteChesnay@fleckens.hu", "82 Rue St Ferréol", "METZ", "57050", "8/18/2002"},
        {"54", "Nicole", "Gregoire", "NicoleGregoire@teleworm.us", "29 rue de la Boétie", "POISSY", "78300", "6/2/1970"},
        {"55", "Curtis", "Larocque", "CurtisLarocque@einrot.com", "89 rue Isambard", "FRÉJUS", "83600", "6/29/1954"},
        {"56", "Philippe", "Audet", "PhilippeAudet@jourrapide.com", "45 rue de l'Epeule", "ROUEN", "76000", "10/7/1943"},
        {"57", "Arnaud", "Rouze", "ArnaudRouze@gustr.com", "62 Faubourg Saint Honoré", "PARIS", "75018", "6/4/1953"},
        {"58", "Sumner", "Bizier", "SumnerBizier@fleckens.hu", "93 place de Miremont", "VILLENEUVE-SAINT-GEORGES", "94190", "5/28/1956"},
        {"59", "Rosamonde", "Laisné", "RosamondeLaisne@fleckens.hu", "6 Square de la Couronne", "PANTIN", "93500", "4/4/1936"},
        {"60", "Rive", "Beauchamps", "RiveBeauchamps@gustr.com", "90 rue de Geneve", "AMIENS", "80090", "5/18/1986"},
        {"61", "Lotye", "Angélil", "LotyeAngelil@gustr.com", "80 rue de la Mare aux Carats", "MONTPELLIER", "34080", "4/16/1983"},
        {"62", "Jesper", "Chartier", "JesperChartier@dayrep.com", "34 rue du Général Ailleret", "LES ABYMES", "97139", "6/22/1979"},
        {"63", "Armina", "Langlais", "ArminaLanglais@cuvox.de", "79 place Maurice-Charretier", "CHAMPS-SUR-MARNE", "77420", "6/17/1998"},
        {"64", "Brigitte", "Pelchat", "BrigittePelchat@rhyta.com", "97 rue des Soeurs", "LA CELLE-SAINT-CLOUD", "78170", "11/6/1997"},
        {"65", "Henry", "Brisebois", "HenryBrisebois@jourrapide.com", "63 rue de Raymond Poincaré", "NARBONNE", "11100", "7/11/1999"},
        {"66", "Matilda", "Sevier", "MatildaSevier@einrot.com", "77 rue de la Mare aux Carats", "MONTREUIL", "93100", "3/7/1979"},
        {"67", "Minette", "Roy", "MinetteRoy@dayrep.com", "40 rue de Penthièvre", "PONTOISE", "95300", "12/11/1975"},
        {"68", "Burkett", "Guertin", "BurkettGuertin@gustr.com", "39 boulevard Albin Durand", "CERGY", "95000", "2/25/1973"},
        {"69", "Halette", "Barteaux", "HaletteBarteaux@jourrapide.com", "93 rue Michel Ange", "LE HAVRE", "76620", "11/18/1954"},
        {"70", "Jewel", "Fournier", "JewelFournier@teleworm.us", "45 rue Beauvau", "MARSEILLE", "13004", "10/26/1988"},
        {"71", "Raina", "Garcia", "RainaGarcia@armyspy.com", "24 rue des six frères Ruellan", "SARCELLES", "95200", "8/27/1986"},
        {"72", "Peppin", "Ménard", "PeppinMenard@teleworm.us", "11 rue Grande Fusterie", "BRIVE-LA-GAILLARDE", "19100", "7/15/1997"},
        {"73", "Fealty", "Allard", "FealtyAllard@teleworm.us", "31 rue de la République", "LYON", "69001", "6/30/1946"},
        {"74", "Percy", "Bériault", "PercyBeriault@jourrapide.com", "11 avenue du Marechal Juin", "SAINT-LEU", "97436", "12/14/1953"},
        {"75", "Merlin", "Massé", "MerlinMasse@rhyta.com", "27 rue Léon Dierx", "LIMOGES", "87280", "10/25/1975"},
        {"76", "Damiane", "Vernadeau", "DamianeVernadeau@jourrapide.com", "17 Rue St Ferréol", "METZ", "57070", "10/20/1937"},
        {"77", "Byron", "LaGrande", "ByronLaGrande@superrito.com", "24 rue Marguerite", "VILLIERS-SUR-MARNE", "94350", "1/25/1958"},
        {"78", "Sibyla", "Roux", "SibylaRoux@dayrep.com", "8 avenue du Marechal Juin", "SAINT-LÔ", "50000", "3/22/1989"},
        {"79", "Joséphine", "Grivois", "JosephineGrivois@dayrep.com", "22 rue Nationale", "PARIS", "75007", "8/23/1991"},
        {"80", "Pascaline", "DuLin", "PascalineDuLin@teleworm.us", "13 avenue Voltaire", "MAISONS-ALFORT", "64700", "10/19/1971"},
        {"81", "Galatee", "Bordeaux", "GalateeBordeaux@dayrep.com", "86 cours Jean Jaures", "BORDEAUX", "33100", "1/22/1959"},
        {"82", "Pomeroy", "Fournier", "PomeroyFournier@armyspy.com", "61 rue du Paillle en queue", "LEVALLOIS-PERRET", "92300", "4/12/2002"},
        {"83", "Artus", "Thibodeau", "ArtusThibodeau@cuvox.de", "95 rue de la Boétie", "POISSY", "78300", "2/25/1949"},
        {"84", "Arianne", "CinqMars", "ArianneCinqMars@fleckens.hu", "60 rue de l'Aigle", "LA ROCHELLE", "17000", "6/6/1936"},
        {"85", "Curtis", "Mainville", "CurtisMainville@armyspy.com", "41 Place Napoléon", "LAMBERSART", "59130", "2/10/1969"},
        {"86", "Viollette", "Bélanger", "ViolletteBelanger@gustr.com", "9 avenue Ferdinand de Lesseps", "GRANDE-SYNTHE", "59760", "6/10/1974"},
        {"87", "Warrane", "Raymond", "WarraneRaymond@dayrep.com", "60 rue Jean Vilar", "BELFORT", "90000", "11/11/1987"},
        {"88", "Rosemarie", "Roux", "RosemarieRoux@fleckens.hu", "65 Avenue De Marlioz", "ANTIBES", "06600", "7/6/1996"},
        {"89", "Bradamate", "Rossignol", "BradamateRossignol@superrito.com", "65 rue Nationale", "PARIS", "75004", "6/25/1974"},
        {"90", "Céline", "Bertrand", "CelineBertrand@superrito.com", "40 Rue de Verdun", "MONTGERON", "91230", "12/5/1988"},
        {"91", "Françoise", "Raymond", "FrancoiseRaymond@armyspy.com", "35 rue Pierre Motte", "SAINT-DIZIER", "52100", "4/17/2000"},
        {"92", "Granville", "Baron", "GranvilleBaron@jourrapide.com", "51 rue Victor Hugo", "COUDEKERQUE-BRANCHE", "59210", "8/6/1965"},
        {"93", "Serge", "Caya", "SergeCaya@dayrep.com", "65 cours Jean Jaures", "BORDEAUX", "33300", "3/1/1957"},
        {"94", "Dixie", "Couet", "DixieCouet@gustr.com", "64 boulevard Aristide Briand", "LE GRAND-QUEVILLY", "76120", "6/21/1956"},
        {"95", "Melusina", "Barrientos", "MelusinaBarrientos@fleckens.hu", "82 Chemin Challet", "LILLE", "59000", "3/8/1986"},
        {"96", "Isaac", "Auclair", "IsaacAuclair@fleckens.hu", "75 rue de Raymond Poincaré", "NANTES", "44200", "6/22/1953"},
        {"97", "Jeannine", "Poulin", "JeanninePoulin@superrito.com", "70 rue de la République", "LUNÉVILLE", "54300", "1/1/1952"},
        {"98", "Maurice", "Pirouet", "MauricePirouet@fleckens.hu", "1 Place Charles de Gaulle", "VILLENEUVE-D'ASCQ", "59491", "10/28/1963"},
        {"99", "Marc", "Meunier", "MarcMeunier@superrito.com", "75 avenue Voltaire", "MÂCON", "71000", "9/3/1947"},
        {"100", "Valérie", "Cressac", "ValerieCressac@einrot.com", "66 Rue Joseph Vernet", "BAGNEUX", "92220", "6/30/1957"},};
    
    
    public static List<String> nomsAlea() {
        return Arrays.stream(PERSONNES_ALEA).map((t) -> {
            return t[2];
        }).unordered().distinct().toList();
    }

    public static List<String> prenomsAlea() {
        return Arrays.stream(PERSONNES_ALEA).map((t) -> {
            return t[1];
        }).unordered().distinct().toList();
    }

}
