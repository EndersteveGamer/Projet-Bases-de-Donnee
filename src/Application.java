// Théo Pariney
// Noam Faivre

import  java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;

public class Application {
    public static final String adresse = "sql7.freemysqlhosting.net";
    public static final String bd = "sql7615548";
    public static final String login = "sql7615548";
    public static final String password = "pvudb2IQnY";
    public static int connection;

    public static void main(String[] args) {
        connection = BD.ouvrirConnexion(adresse, bd, login, password);

        menuPrincipal();
    }

    // Menu Principal dans lequel l'utilisateur choisit l'action désirée
    public static void menuPrincipal() {
        Scanner sc = new Scanner(System.in);
        boolean continuer = true;

        while (continuer) {
            String reponse;
            int num = -1;
            while (num == -1) {
                System.out.println(
                        """
                                Menu Principal
                                Entrez un numéro:
                                1 - Ajouter un client
                                2 - Commandes d'un client
                                3 - Ajouter une commande
                                4 - Mélanges à produire
                                5 - Factures mensuelles d'un client
                                6 - Quitter
                                """
                );

                reponse = sc.next();

                try {
                    num = Integer.parseInt(reponse);
                } catch (NumberFormatException e) {
                    System.out.println("Cette option n'existe pas");
                }
                if (num > 6) num = -1;

                // Sélectionne l'action choisie
                switch (num) {
                    // Ajoute un client
                    case 1 -> ajouterClient();
                    // Permet de voir les commandes d'un client
                    case 2 -> commandesClient();
                    // Permet d'ajouter une commande
                    case 3 -> ajouterCommande();
                    // Permet de renseigner l'utilisateur sur le nombre de mélanges à faire pour la semaine prochaine
                    case 4 -> productionMelange();
                    // Permet de se renseigner sur le montant mensuel des factures d'un client
                    case 5 -> facturesMensuelles();
                    // Permet de quitter l'application
                    case 6 -> continuer = false;
                }
            }
        }

        // Déconnection de la base de données
        BD.fermerConnexion(connection);
    }

    // La fonction ci-dessous permet au patron d'ajouter un client
    public static void ajouterClient() {
        Scanner sc = new Scanner(System.in);

        // L'utilisateur renseigne le nom, l'adresse ainsi que la ville du client
        System.out.println("Entrez le nom du client");
        String nomClient = sc.nextLine();

        System.out.println("Entrez l'adresse du client");
        String adresseClient = sc.nextLine();

        System.out.println("Entrez la ville du client");
        String villeClient = sc.nextLine();

        // La requête SQL permettant l'ajout du nom, de l'adresse et de la ville d'un client dans la base de données
        String requete = "INSERT INTO CLIENTE" +
                "(NomCli, AdrCli, VilleCli) " +
                "VALUES " +
                "('" + nomClient + "', '" + adresseClient + "', '" + villeClient + "')";

        // Exécution de la requête
        BD.executerUpdate(connection, requete);
    }

    // La fonction ci-dessous permet de voir les commandes d'un client
    public static void commandesClient() {
        Scanner sc = new Scanner(System.in);

        ArrayList<Integer> clients = new ArrayList<>();
        ArrayList<String> noms = new ArrayList<>();

        // Exécution de la requête permettant de sélectionner le nom et l'ID d'un client ayant efectué au moins une commande
        // Cette requête servira à faire en sorte que l'utilisateur ne puisse pas voir les commandes d'un client qui n'aurait effectué aucune commande
        int res = BD.executerSelect(connection, "SELECT DISTINCT LIVRER.IDCli, CLIENTE.NomCli FROM LIVRER " +
                "INNER JOIN CLIENTE ON LIVRER.IDCli = CLIENTE.IDCli");

        while (BD.suivant(res)) {
            clients.add(BD.attributInt(res, "LIVRER.IDCli"));
            noms.add(BD.attributString(res, "CLIENTE.NomCli"));
        }

        String answer;
        int numClient = -1;

        do {
            // Ci-dessous l'utilisateur renseigne l'id d'un client jusqu'à ce que l'id renseignée appartienne à un client
            System.out.println("Entrez le client dont vous voulez voir les commandes");
            System.out.print("Valeurs possibles: ");
            for (int i = 0; i < clients.size(); i++) {
                System.out.print(clients.get(i) + " (" + noms.get(i) + ")");
                if (i != clients.size() - 1) System.out.print(", ");
            }
            System.out.println();

            answer = sc.next();

            try {
                numClient = Integer.parseInt(answer);
            } catch (NumberFormatException e) {
                System.out.println("Ceci n'est pas un numéro de client!");
                continue;
            }

            if (!clients.contains(numClient)) System.out.println("Ce client n'a pas commandé de pain!");
        } while (!clients.contains(numClient));

        // La requête ci-dessous permet de récupérer les commande du client sélectionné ("numClient")
        String requete = "SELECT * FROM LIVRER NATURAL JOIN PAIN " +
                "WHERE LIVRER.IDCli = " + numClient;

        // Exécution de la requête.
        res = BD.executerSelect(connection, requete);

        // Affichage du résultat
        while (BD.suivant(res)) {
            System.out.println("Commande du " + Date.formatSQLDate(BD.attributString(res, "LIVRER.DateLivraison")) + ": " +
                    "Pain " + BD.attributString(res, "PAIN.DescPain") + " x" + BD.attributInt(res, "LIVRER.NombreDePains") + "\n");
        }
    }

    // La fonction ci-dessous permet d'ajouter une commande à un client
    public static void ajouterCommande() {
        Scanner sc = new Scanner(System.in);

        ArrayList<Integer> clients = new ArrayList<>();
        ArrayList<String> noms = new ArrayList<>();

        // Cette requête permet d'obtenir l'id et le nom de chaque client
        int res = BD.executerSelect(connection, "SELECT DISTINCT CLIENTE.IDCli, CLIENTE.NomCli FROM CLIENTE");

        while (BD.suivant(res)) {
            clients.add(BD.attributInt(res, "CLIENTE.IDCli"));
            noms.add(BD.attributString(res, "CLIENTE.NomCli"));
        }

        String answer;
        int numClient = -1;
        // Cette partie permet à l'utilisateur de saisir un id de client
        do {
            System.out.println("Entrez le client ayant effectué une commande");
            System.out.print("Valeurs possibles: ");
            for (int i = 0; i < clients.size(); i++) {
                System.out.print(clients.get(i) + " (" + noms.get(i) + ")");
                if (i != clients.size() - 1) System.out.print(", ");
            }
            System.out.println();
            answer = sc.next();

            try {
                numClient = Integer.parseInt(answer);
            } catch (NumberFormatException e) {
                System.out.println("Ceci n'est pas un numéro de client!");
                continue;
            }

            if (!clients.contains(numClient)) System.out.println("Ce client n'existe pas!");
        } while (!clients.contains(numClient));

        ArrayList<Integer> pains = new ArrayList<>();
        ArrayList<String> descPains = new ArrayList<>();

        // Cette requête permet de récupérer les différents pains ainsi que leur description, afin de faire en sorte que
        // l'utilisateur ne puisse rentrer qu'un pain qui existe
        String requete = "SELECT IDPain, DescPain FROM PAIN";

        res = BD.executerSelect(connection, requete);

        while (BD.suivant(res)) {
            pains.add(BD.attributInt(res, "PAIN.IDPain"));
            descPains.add(BD.attributString(res, "PAIN.DescPain"));
        }

        int pain = -1;
        String reponse;
        // L'utilisateur saisit ici un id de pain
        do {
            for (int i = 0; i < pains.size(); i++) System.out.println("Pain " + pains.get(i) + ": " + descPains.get(i));

            reponse = sc.next();

            try {
                pain = Integer.parseInt(reponse);
            } catch (NumberFormatException e) {
                System.out.println("Ceci n'est pas un numéro de pain!");
                continue;
            }

            if (!pains.contains(pain)) System.out.println("Ce pain n'existe pas");
        } while (pain == -1 || !pains.contains(pain));

        int numPains = 0;

        // Permet de rentrer la quantité de pains désirée
        do {
            System.out.println("Entrez le nombre de pains à commander");
            reponse = sc.next();

            try {
                numPains = Integer.parseInt(reponse);
            } catch (NumberFormatException e) {
                System.out.println("Ceci n'est pas un nombre!");
                continue;
            }

            if (numPains <= 0) System.out.println("Ce nombre de pains n'est pas valide!");
        } while (numPains <= 0);

        String date = "";
        boolean isDateValid = false;

        // Permet de saisir la date de livraison du pain (et vérifie que le format soit respecté)
        while (!isDateValid) {
            try {
                date = Date.entrerDate("Entrez la date de livraison au format jj/mm/aaaa");
                isDateValid = true;
            } catch (ParseException e) {
                System.out.println("La date entrée n'est pas dans un format valide!");
            }
        }

        // Crée et exécute la requête permettant de passer une commande
        // Si l'utilisateur a déjà passé une commande avec le même type de pain à la même date alors le nombre de pains des deux commandes sera additionné.
        // Il ne restera donc plus qu'une seule commande
        // Sinon, la commande est créée
        boolean isAdding = false;
        requete = "SELECT IDCli, IDPain, DateLivraison FROM LIVRER" +
                " WHERE IDCLI = " + numClient + " AND IDPain = " + pain + " AND DateLivraison = '" + date + "'";
        res = BD.executerSelect(connection, requete);
        while (BD.suivant(res)) isAdding  = true;

        if (isAdding) {
            requete = "UPDATE LIVRER SET NombreDePains = NombreDePains + " + numPains + " " +
                    "WHERE IDCli = " + numClient + " AND IDPain = " + pain + " AND DateLivraison = '" + date + "'";
        }
        else {
            requete = "INSERT INTO LIVRER (IDCli, IDPain, DateLivraison, NombreDePains) VALUES " +
                    "(" + numClient + ", " + pain + ", '" + date + "', " + numPains + ")";
        }

        BD.executerUpdate(connection, requete);
        System.out.println("La commande a été ajoutée avec succès!");
    }

    // La fonction ci-dessous permet de renseigner l'utilisateur sur le nombre de mélanges à produire pour la semaine prochaine
    public static void productionMelange() {
        ArrayList<Integer> idMelange = new ArrayList<>();
        ArrayList<Integer> quantiteMelange = new ArrayList<>();
        ArrayList<java.util.Date> dates = new ArrayList<>();
        HashMap<Integer, Integer> melangesAPreparer = new HashMap<>();

        // Récupère toutes les commandes passées
        String requete = "SELECT PAIN.IDMelange, LIVRER.DateLivraison, LIVRER.NombreDePains FROM LIVRER NATURAL JOIN PAIN";

        int res = BD.executerSelect(connection, requete);

        while (BD.suivant(res)) {
            idMelange.add(BD.attributInt(res, "PAIN.IDMelange"));
            quantiteMelange.add(BD.attributInt(res, "LIVRER.NombreDePains"));
            try {
                dates.add(new SimpleDateFormat("yyyy-MM-dd").parse(BD.attributString(res, "LIVRER.DateLivraison")));
            } catch (ParseException e) {
                System.out.println("La date renvoyée par la base de donnée est incorrecte!");
                return;
            }
        }

        // Ne récupère que les mélanges de la semaine prochaine
        for (int i = 0; i < idMelange.size(); i++) {
            if (dates.get(i).before(new java.util.Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000))) {
                if (!melangesAPreparer.containsKey(idMelange.get(i))) {
                    melangesAPreparer.put(idMelange.get(i), quantiteMelange.get(i));
                }
                else {
                    melangesAPreparer.put(idMelange.get(i), melangesAPreparer.get(idMelange.get(i)) + quantiteMelange.get(i));
                }
            }
        }

        HashMap<Integer, String> descriptionMelanges = new HashMap<>();

        // Récupère les descriptions des mélanges
        requete = "SELECT * FROM MELANGE";
        res = BD.executerSelect(connection, requete);

        while (BD.suivant(res)) {
            descriptionMelanges.put(
                BD.attributInt(res, "MELANGE.IDMelange"),
                BD.attributString(res, "MELANGE.DescMelange")
            );
        }

        // Affiche l'id du mélange ainsi que sa description et le nombre de fois dont il faut le préparer
        for (int melange : melangesAPreparer.keySet()) {
            System.out.println("Mélange numéro " + melange + ":");
            System.out.println("Description: " + descriptionMelanges.get(melange));
            System.out.println("À préparer " + melangesAPreparer.get(melange) + " fois\n");
        }
    }

    // La fonction ci-dessous permet de se renseigner sur le montant mensuel des factures d'un client
    public static void facturesMensuelles() {
        Scanner sc = new Scanner(System.in);

        ArrayList<Integer> clients = new ArrayList<>();
        ArrayList<String> noms = new ArrayList<>();

        // Cette requête permet de sélectionner l'id et le nom de tous les clients ayant passé une commande
        int res = BD.executerSelect(connection, "SELECT DISTINCT LIVRER.IDCli, CLIENTE.NomCli FROM LIVRER " +
                "INNER JOIN CLIENTE ON LIVRER.IDCli = CLIENTE.IDCli");

        while (BD.suivant(res)) {
            clients.add(BD.attributInt(res, "LIVRER.IDCli"));
            noms.add(BD.attributString(res, "CLIENTE.NomCli"));
        }

        String answer;
        int numClient = -1;

        do {
            // Permet de saisir un des clients ayant passé une commande
            System.out.println("Entrez le client dont vous voulez voir les commandes");
            System.out.print("Valeurs possibles: ");
            for (int i = 0; i < clients.size(); i++) {
                System.out.print(clients.get(i) + " (" + noms.get(i) + ")");
                if (i != clients.size() - 1) System.out.print(", ");
            }
            System.out.println();

            answer = sc.next();

            try {
                numClient = Integer.parseInt(answer);
            } catch (NumberFormatException e) {
                System.out.println("Ceci n'est pas un numéro de client!");
                continue;
            }

            if (!clients.contains(numClient)) System.out.println("Ce client n'a pas commandé de pain!");
        } while (!clients.contains(numClient));

        double somme = 0;

        // Cette requête récupère le prix ainsi que le nombre de pains commandé par le client
        String requete = "SELECT PAIN.PrixPainHT, LIVRER.NombreDePains FROM LIVRER NATURAL JOIN PAIN WHERE IDCli = " +
                numClient;

        res = BD.executerSelect(connection, requete);

        // Calcule la somme des prix des commandes du client
        while (BD.suivant(res)) {
            somme += Double.parseDouble(BD.attributString(res, "PAIN.PrixPainHT")) * BD.attributInt(res, "LIVRER.NombreDePains");
        }

        // Affiche le montant de la facture HT, la TVA, ainsi que le total
        System.out.println("La facture mensuelle de ce client s'élève à:");
        System.out.println(somme + " HT");
        System.out.println("+" + MathUtils.round(somme * .055, 2) + " de TVA");
        System.out.println("Total: " + MathUtils.round(somme * 1.055, 2));
    }

    // Cette classe permet de renseigner ainsi que de saisir des dates
    public static class Date {
        // Formatte une date donnée au format SQL en date formattée en jour nomdumois année
        public static String formatSQLDate(String date) {
            int jour = Integer.parseInt(date.substring(8));
            int mois = Integer.parseInt(date.substring(5, 7));
            int annee = Integer.parseInt(date.substring(0, 4));

            return jour + " " + formatMois(mois) + " " + annee;
        }

        // Convertit un numéro de mois en nom du mois
        private static String formatMois(int mois) {
            return switch (mois) {
                case 1 -> "janvier";
                case 2 -> "février";
                case 3 -> "mars";
                case 4 -> "avril";
                case 5 -> "mai";
                case 6 -> "juin";
                case 7 -> "juillet";
                case 8 -> "août";
                case 9 -> "septembre";
                case 10 -> "octobre";
                case 11 -> "novembre";
                default -> "décembre";
            };
        }

        // Cette méthode permet de saisir une date
        public static String entrerDate(String prompt) throws ParseException {
            Scanner sc = new Scanner(System.in);

            System.out.println(prompt);
            String date = sc.next();

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
            java.util.Date newDate = formatter.parse(date);
            SimpleDateFormat SQLFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);
            return SQLFormatter.format(newDate);
        }
    }

    // Cette classe permet d'arrondir un nombre à virgule (utilisé pour arrondir la TVA)
    public static class MathUtils {
        public static double round(double number, int places) {
            if (places < 0) throw new IllegalArgumentException();

            BigDecimal bd = new BigDecimal(Double.toString(number));
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            return bd.doubleValue();
        }
    }
}
