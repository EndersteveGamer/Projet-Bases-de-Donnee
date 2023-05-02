// Sujet 2
// Univ_fcomte.theop/noamf

//Server: sql7.freemysqlhosting.net
//Name: sql7615548
//Username: sql7615548
//Password: pvudb2IQnY
//Port number: 3306

public class Application {
    public static final String adresse = "sql7.freemysqlhosting.net";
    public static final String bd = "sql7615548";
    public static final String login = "sql7615548";
    public static final String password = "pvudb2IQnY";

    public static void main(String[] args) {
        int connection = BD.ouvrirConnection(adresse, bd, login, password);


    }
}
