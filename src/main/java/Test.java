//import com.nfsdb.journal.exceptions.JournalException;
import com.questdb.ex.JournalException;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by musta_000 on 7/11/2016.
 */
public class Test {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException,
            InstantiationException, SQLException, IOException, CompoundNotFoundException, JournalException {

        if(args[1].equals("-meta")){
        }
        else if(args[1].equals("-clear")){
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
//            NFSDBManager db=new NFSDBManager();
//            db.test();
            TestNFSDB.main(new String[0]);
        }
        else if(args[1].equals("-build")){
            UniquePrimers.primerDBsetup();
        }
        else if(args[1].equals("-pick")){
            PrimerMatching.matchPrimersNFSDB();
        }
        else if(args[1].equals("-full")){
            Test.main(new String[]{"-test","-build"});
            Test.main(new String[]{"-test","-new","18","25"});
            Test.main(new String[]{"-test","-pick"});
        }
    }
}
