import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**Copyright (C) 2016  Thomas Gregory

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.

 * Created by Thomas on 12/31/2015.
 * Main for database updating
 */
@SuppressWarnings("Duplicates")
public class ArthroUpdate{
    static final String JDBC_DRIVER_HSQL = "org.hsqldb.jdbc.JDBCDriver";
    static final String DB_SERVER_URL ="jdbc:hsqldb:hsql://localhost/primerdb;ifexists=true";
    public static Connection conn;
    private static final String USER = "SA";
    private static final String PASS = "";

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class.forName(JDBC_DRIVER_HSQL).newInstance();
        conn = DriverManager.getConnection(DB_SERVER_URL,USER,PASS);
        if(args[1].equals("-meta")){
            HSqlManager.getClusterSizes(conn);
        }
        else if(args[1].equals("-clear")){
            HSqlManager.clearDatabase(conn);
        }
//        else if(args[1].equals("-build")){
//            HSqlManager.main(args);
//        }
        else if(args[1].equals("-new")){
            if(args[3]!=null){
                for (int i =Integer.valueOf(args[2]);
                     i<=Integer.valueOf(args[3]);i++){
                    HSqlManager.runNewArthroBP(conn, i);
                    System.gc();
                }
            }else {
                HSqlManager.runNewBP(conn, Integer.valueOf(args[2]));
            }
        }
        else if(args[1].equals("-pick")){
            HSqlPrimerDesign.locations(conn,"-arthro");
        }
//        else if(args[1].equals("-match")){
//            HSqlPrimerDesign.checker(conn,Integer.valueOf(args[2]));
//        }
        else if(args[1].equals("-check")){
//            FixPhagelist.main(new String[0]);
            HSqlManager.runChecks(conn);
        }else if(args[1].equals("-test")){
//            Test.test(conn, 18);
        }
    }
}