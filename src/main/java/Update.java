
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Copyright (C) 2016  Thomas Gregory

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
public class Update{
//    static final String JDBC_DRIVER_HSQL = "org.hsqldb.jdbc.JDBCDriver";
    static final String JDBC_DRIVER_H2 = "org.h2.Driver";
//    static final String DB_SERVER_URL ="jdbc:hsqldb:hsql://localhost/primerdb;ifexists=true";
    static final String DB_SERVER_URL_H2 ="jdbc:h2:primerdb;LOG=0;LOCK_MODE=0;CACHE_SIZE=65536;" +
        "UNDO_LOG=0;WRITE_DELAY=10";
    static final String DB_URL_HSQL_C = "jdbc:hsqldb:file:database/primerdb;ifexists=true";
    public static Connection conn;
    private static final String USER = "SA";
    private static final String PASS = "";

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException, CompoundNotFoundException {
        Class.forName(JDBC_DRIVER_H2).newInstance();
        conn = DriverManager.getConnection(DB_SERVER_URL_H2,USER,PASS);
        if(args[1].equals("-meta")){
//            HSqlManager.getClusterSizes(conn);
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
                    HSqlManager.primerAnalysis(conn, i);
                    System.gc();
                }
            }else {
                HSqlManager.primerAnalysis(conn, Integer.valueOf(args[2]));
            }
        }
        else if(args[1].equals("-pick")){
            HSqlPrimerDesign.locations(conn);
        }
        else if(args[1].equals("-test")){
            PrimerMatching.matchPrimers(conn);
        }
    }
}
