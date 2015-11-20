import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by musta_000 on 11/5/2015.
 */
public class ImportCSV {
    String[] prims;
    ImportCSV(String path){
        prims = readFile(path);
    }
    public String[] readFile(String path1){
        BufferedReader br = null;
        String line;
        String cvsSplitBy = ",";
        String[] prim =null;
        try {
            br = new BufferedReader( new FileReader(path1));
            while ((line = br.readLine()) != null) {

                prim = line.split(cvsSplitBy);
                return prim;
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return prim;
    }
}
