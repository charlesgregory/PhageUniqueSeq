
import org.apache.commons.io.FileUtils;
import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.io.InputStreamReader;

/**
 * Created by Charles Gregory on 11/10/2015.
 * Controls the import of the mycobacterium smegmatis phage list
 * from phagesdb.org
 */
public class ImportPhagelist {
    List<String[]> full;
    //Singleton pattern
    private static ImportPhagelist instance;

    private ImportPhagelist() throws IOException {
        String base = new File("").getAbsolutePath();
        File file = new File(base+"\\Fastas");
        System.out.println(file.getAbsolutePath());
        CSV.makeDirectory(file);
        String path =Download();
        System.out.println(path);
        this.full = readFile(path);
    }

    public static ImportPhagelist getInstance() throws IOException {
        if (instance == null) {
            instance = new ImportPhagelist();
        }
        return instance;
    }

    //parses phagelist tsv file and preselects only mycobacterium smegmatis phages
    private List<String[]> readFile(String path1) throws IOException {
        String cvsSplitBy = "\\t";
        List<String[]> lines = null;
        try (FileInputStream fis = new FileInputStream(path1);
            BufferedReader br = new BufferedReader( new InputStreamReader(fis))) {
            lines = br.lines().map((l) -> l.split(cvsSplitBy)).collect(Collectors.toList());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(lines.isEmpty());
        System.out.println(Arrays.toString(lines.get(1)));
        List<String[]> collect = lines.stream().filter(x -> x[1].contains("Mycobacterium smegmatis"))
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
        System.out.println(collect.isEmpty());
        return collect;
    }
    //Downloads phagelist file from phagesdb.org
    private static String Download() throws IOException {
        String path = "http://phagesdb.org/data/?set=seq&type=full";
        String base = new File("").getAbsolutePath();
        String name = base + "\\Fastas\\PhagesDB_Data.txt";
        File file = new File(name);
        URL netPath = new URL(path);
        FileUtils.copyURLToFile(netPath, file);
        return file.toString();
    }
}
