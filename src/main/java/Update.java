
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Thomas on 12/31/2015.
 * Main for database updating
 */
public class Update{
    static final String JDBC_DRIVER_HSQL = "org.hsqldb.jdbc.JDBCDriver";
    static final String DB_URL_HSQL_C = "jdbc:hsqldb:file:database/primerdb;ifexists=true";
    public static Connection conn;
    private static final String USER = "SA";
    private static final String PASS = "";

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class.forName(JDBC_DRIVER_HSQL).newInstance();
        conn = DriverManager.getConnection(DB_URL_HSQL_C,USER,PASS);
        if(args[2].equals("-newCommon")){
            HSqlManager.runNewBPCommon(conn,Integer.valueOf(args[1]));
        }
        else if(args[2].equals("-newUnique")){
            HSqlManager.runNewBPUnique(conn,Integer.valueOf(args[1]));
        }
        else if(args[2].equals("-new")){
            HSqlManager.runNewBP(conn,Integer.valueOf(args[1]));
        }
        else {
            FixPhagelist.main(new String[0]);
            HSqlManager.runChecks(conn, Integer.valueOf(args[1]));
        }
    }
}
