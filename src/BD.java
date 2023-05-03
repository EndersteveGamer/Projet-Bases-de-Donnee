import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;

public class BD {
    public static String connecteur = "./mysql-connector-java.jar";
    private static int init = init();
    static HashMap<Integer, Connection> connexions = new HashMap();
    static HashMap<Integer, ResultSet> resultats = new HashMap();
    private static final int TYPE_STRING = 0;
    private static final int TYPE_LONG = 1;
    private static final int TYPE_INT = 2;

    public BD() {
    }

    private static int init() {
        try {
            if (!(new File(connecteur)).exists()) {
                System.err.println("/!\\ Erreur, assurez-vous que le fichier " + connecteur + " existe.");
                System.exit(0);
            }

            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            return 0;
        } catch (Exception var1) {
            var1.printStackTrace();
            return -1;
        }
    }

    public static int ouvrirConnexion(String var0, String var1, String var2, String var3) {
        int var4 = -1;

        try {
            Connection var5 = DriverManager.getConnection("jdbc:mysql://" + var0 + "/" + var1, var2, var3);

            do {
                ++var4;
            } while(connexions.containsKey(var4));

            connexions.put(var4, var5);
        } catch (Exception var6) {
            System.err.println("/!\\ Erreur dans l'ouverture de la connexion : " + var6.getMessage());
            var6.printStackTrace();
        }

        return var4;
    }

    public static void fermerConnexion(int var0) {
        try {
            if (connexions.containsKey(var0)) {
                ((Connection)connexions.get(var0)).close();
                connexions.remove(var0);
            }
        } catch (Exception var2) {
            System.err.println("/!\\ Erreur dans la fermeture de la connexion id=" + var0 + ": " + var2.getMessage());
        }

    }

    public static int executerSelect(int var0, String var1) {
        Connection var2 = (Connection)connexions.get(var0);
        int var3 = -1;
        if (var2 == null) {
            return var3;
        } else {
            try {
                PreparedStatement var4 = var2.prepareStatement(var1);
                ResultSet var5 = var4.executeQuery();

                do {
                    ++var3;
                } while(resultats.containsKey(var3));

                resultats.put(var3, var5);
            } catch (Exception var6) {
                System.err.println("/!\\ Erreur lors de l'exécution de la requête \"" + var1 + "\" : " + var6.getMessage());
            }

            return var3;
        }
    }

    public static int executerUpdate(int var0, String var1) {
        Connection var2 = (Connection)connexions.get(var0);
        int var3 = -1;
        if (var2 == null) {
            return var3;
        } else {
            try {
                PreparedStatement var4 = var2.prepareStatement(var1, 1);
                var3 = var4.executeUpdate();
                ResultSet var5 = var4.getGeneratedKeys();
                if (var5 != null && var5.next()) {
                    var3 = var5.getInt(1);
                    var5.close();
                }
            } catch (Exception var6) {
                System.err.println("/!\\ Erreur lors de l'exécution de la requête \"" + var1 + "\" : " + var6.getMessage());
                var6.printStackTrace();
            }

            return var3;
        }
    }

    public static void fermerResultat(int var0) {
        if (resultats.containsKey(var0)) {
            try {
                ((ResultSet)resultats.get(var0)).close();
            } catch (Exception var2) {
                System.err.println("/!\\ Erreur dans la fermeture du résultat id=" + var0 + " : " + var2.getMessage());
            }

            resultats.remove(var0);
        }

    }

    public static boolean suivant(int var0) {
        ResultSet var1 = (ResultSet)resultats.get(var0);
        if (var1 == null) {
            System.err.println("Le resultat " + var0 + " n'existe pas.");
            return false;
        } else {
            try {
                return var1.next();
            } catch (Exception var3) {
                System.err.println("/!\\ Erreur dans le passage à l'enregistrement suivant :" + var3.getMessage());
                return false;
            }
        }
    }

    public static boolean reinitialiser(int var0) {
        ResultSet var1 = (ResultSet)resultats.get(var0);
        if (var1 == null) {
            System.err.println("Le résultat " + var0 + " n'existe pas.");
            return false;
        } else {
            try {
                var1.beforeFirst();
                return true;
            } catch (Exception var3) {
                System.err.println("/!\\ Erreur dans le passage au premier enregistrement : " + var3.getMessage());
                return false;
            }
        }
    }

    public static String attributString(int var0, String var1) {
        String var2 = (String)attribut(var0, var1, 0);
        return var2 == null ? "" : var2;
    }

    public static int attributInt(int var0, String var1) {
        Integer var2 = (Integer)attribut(var0, var1, 2);
        return var2 == null ? 0 : var2;
    }

    public static long attributLong(int var0, String var1) {
        Long var2 = (Long)attribut(var0, var1, 1);
        return var2 == null ? 0L : var2;
    }

    private static Object attribut(int var0, String var1, int var2) {
        ResultSet var3 = (ResultSet)resultats.get(var0);
        if (var3 == null) {
            System.err.println("Le resultat " + var0 + " n'existe pas.");
            return null;
        } else {
            try {
                switch (var2) {
                    case 0:
                        return var3.getString(var1);
                    case 1:
                        return var3.getLong(var1);
                    case 2:
                        return var3.getInt(var1);
                }
            } catch (Exception var5) {
                System.err.println("/!\\ Erreur dans la récupération de la valeur de l'attribut \"" + var1 + "\" : " + var5.getMessage());
            }

            return null;
        }
    }

    public static long maintenant() {
        return System.currentTimeMillis();
    }

    public static int jour(long var0) {
        return DateParGenre(var0, 5);
    }

    public static int mois(long var0) {
        return DateParGenre(var0, 2);
    }

    public static int annee(long var0) {
        return DateParGenre(var0, 1);
    }

    public static int heures(long var0) {
        return DateParGenre(var0, 11);
    }

    public static int minutes(long var0) {
        return DateParGenre(var0, 12);
    }

    public static int secondes(long var0) {
        return DateParGenre(var0, 13);
    }

    private static int DateParGenre(long var0, int var2) {
        Calendar var3 = Calendar.getInstance();
        var3.setTime(new Date(var0));
        return var3.get(var2);
    }

    public static long date(int var0, int var1, int var2, int var3, int var4, int var5) {
        Calendar var6 = Calendar.getInstance();
        var6.set(var2, var1, var0, var3, var4, var5);
        return var6.getTimeInMillis();
    }

    public static void pause(int var0) {
        try {
            long var1 = (long)var0;
            Thread.sleep(var1);
        } catch (InterruptedException var3) {
        }

    }
}
