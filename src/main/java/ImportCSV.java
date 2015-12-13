import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by musta_000 on 11/5/2015.
 */
public class ImportCSV {
    public static Set<CharSequence> readFile(String path1){
        BufferedReader br = null;
        String line;
        String cvsSplitBy = ",";
        CharSequence[] prim =null;
        try {
            br = new BufferedReader( new FileReader(path1));
            while ((line = br.readLine()) != null) {

                prim = line.split(cvsSplitBy);
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
        Set<CharSequence> s = new HashSet<>();
        Collections.addAll(s, prim);
        return s;
    }
}
