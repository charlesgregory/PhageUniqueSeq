import com.nfsdb.journal.exceptions.JournalException;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by musta_000 on 7/11/2016.
 */
public class Test {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException, IOException, JournalException, CompoundNotFoundException {

        if(args[1].equals("-meta")){
//            HSqlManager.getClusterSizes();
        }
        else if(args[1].equals("-clear")){
//            HSqlManager.clearDatabase();
        }
//        else if(args[1].equals("-build")){
//            HSqlManager.main(args);
//        }
        else if(args[1].equals("-new")){
            NFSDBManager db=new NFSDBManager();
            db.makeDB();
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
//            PrimerMatching.matchPrimers();
        }
        else if(args[1].equals("-build")){
            UniquePrimers.primerDBsetup();
        }
        else if(args[1].equals("-pick")){
            PrimerMatching.matchPrimersNFSDB();
        }
    }
}
