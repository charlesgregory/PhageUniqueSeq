import javafx.util.Pair;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.io.InputStreamReader;

/**
 * Created by musta_000 on 11/10/2015.
 */
public class ImportPhagelist {
    List<String[]> full;
    List<String> clusters;
    List<String> allPhages;
    ImportPhagelist() throws IOException {
        String path =Download();
        this.full = readFile(path);

    }
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
                    String[] r = new String[2];
                    r[0] =x[2];
                    r[1]=x[0];
                    return r;
                }
                ).collect(Collectors.toList());
        return collect;
    }
    public static String Download() throws IOException {
        String path = "http://phagesdb.org/data/?set=seq&type=full";
        String base = new File("").getAbsolutePath();
        String name = base + "\\src\\main\\java\\Fastas\\PhagesDB_Data.txt";
        File file = new File(name);
        URL netPath = new URL(path);
        FileUtils.copyURLToFile(netPath, file);
        return file.toString();
    }
//    public List<String> clusterList(List<String[]> all){
//        List<String> clu = new ArrayList<>();
//        for(String[] x : all){
//            clu.add(x[2]);
//        }
//        Set<String> clust =new HashSet<String>(clu);
//        List<String> cluster = new ArrayList<String>(clust);
//        return cluster;
//    }
//    public List<String> phageNames(List<String[]> all){
//        List<String> phages = new ArrayList<>();
//        for(String[] x:all){
//            phages.add(x[0]);
//        }
//        return phages;
//    }
}
