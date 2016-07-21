
import com.questdb.ex.JournalException;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;

import java.io.File;
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
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException,
            InstantiationException, SQLException, IOException, CompoundNotFoundException, JournalException {

        if(args[1].equals("-clear")){
            //Incomplete
        }
        else if(args[1].equals("-new")){
            NFSDBManager db=new NFSDBManager();
            db.makePrimerTable();
            if(args[3]!=null){
                for (int i =Integer.valueOf(args[2]);
                     i<=Integer.valueOf(args[3]);i++){
                    UniquePrimers.primerAnalysis(i,db);
                    System.gc();
                }
            }else {
                UniquePrimers.primerAnalysis(Integer.valueOf(args[2]),db);
            }
        }
        else if(args[1].equals("-test")){
            TestNFSDB.main(new String[0]);
        }
        else if(args[1].equals("-pick")){
            PrimerMatching.matchPrimersNFSDB();
        }
    }
}
