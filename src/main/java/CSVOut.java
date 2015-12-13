import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

/**
 * Created by Thomas on 12/8/2015.
 */
public class CSVOut {
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
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
}
