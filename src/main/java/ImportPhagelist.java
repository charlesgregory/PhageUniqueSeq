import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.io.InputStreamReader;

/**
 * Created by musta_000 on 11/10/2015.
 * Controls the import of the mycobacterium smegmatis phage list
 * from phagesdb.org
 */
public class ImportPhagelist {
    List<String[]> full;
    ImportPhagelist() throws IOException {
        String path =Download();
        this.full = readFile(path);

    }
    //parses phagelist tsv file and preselects only mycobacterium smegmatis phages
    public List<String[]> readFile(String path1) throws IOException {
        String cvsSplitBy = "\\t";
        List<String[]> lines = null;
        try (FileInputStream fis = new FileInputStream(path1);
            BufferedReader br = new BufferedReader( new InputStreamReader(fis))) {
            lines = br.lines().map((l) -> l.split(cvsSplitBy)).collect(Collectors.toList());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        List<String[]> collect = lines.stream().filter(x -> x[1].equals("Mycobacterium smegmatis mc²155"))
                .map(x -> {
                    if(x[2].equals("Singleton")){
                        String[] r = new String[2];
                        r[0] = x[0];
                        r[1] = x[0];
                        return r;
                    }
                    else{
                                String[] r = new String[2];
                                r[0] = x[2];
                                r[1] = x[0];
                                return r;
                            }
                }
                ).collect(Collectors.toList());
        return collect;
    }
    //Downloads phagelist file from phagesdb.org
    public static String Download() throws IOException {
        String path = "http://phagesdb.org/data/?set=seq&type=full";
        String base = new File("").getAbsolutePath();
        String name = base + "\\src\\main\\java\\Fastas\\PhagesDB_Data.txt";
        File file = new File(name);
        URL netPath = new URL(path);
        FileUtils.copyURLToFile(netPath, file);
        return file.toString();
    }
}
