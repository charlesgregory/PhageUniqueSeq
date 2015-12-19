import java.io.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Charles Gregory on 12/8/2015.
 */
public class CSV {
    private static final String COMMA_DELIMITER = ",";
    public static void writeCommonCSV(String s,Set<CharSequence> data) throws IOException {
        String base = new File("").getAbsolutePath();
        FileWriter fileWriter = new FileWriter(base+"\\src\\main\\java\\Common\\"+s+".csv");
        data.forEach(x ->{
            try {
                fileWriter.append(x);
                fileWriter.append(COMMA_DELIMITER);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        fileWriter.flush();
        fileWriter.close();
    }
    public static void writeDataCSV(String s,Set<CharSequence> data) throws IOException {
        String base = new File("").getAbsolutePath();
        FileWriter fileWriter = new FileWriter(base+"\\src\\main\\java\\PhageData\\"+s+".csv");
        data.forEach(x ->{
            try {
                fileWriter.append(x);
                fileWriter.append(COMMA_DELIMITER);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        fileWriter.flush();
        fileWriter.close();
    }
    public static void writeUniqueCSV(String s,Set<CharSequence> data) throws IOException {
        String base = new File("").getAbsolutePath();
        FileWriter fileWriter = new FileWriter(base+"\\src\\main\\java\\Unique\\"+s+".csv");
        data.forEach(x ->{
            try {
                fileWriter.append(x);
                fileWriter.append(COMMA_DELIMITER);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        fileWriter.flush();
        fileWriter.close();
    }
    public static Set<CharSequence> readCSV(String path1){
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
