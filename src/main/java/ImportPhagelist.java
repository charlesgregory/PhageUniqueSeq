import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        full = readFile(path);
        clusters = clusterList(full);
        allPhages = phageNames(full);

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
        List<String[]> remove = new ArrayList<>();
        int i =0;
        for(String[] x:lines){
            if(!(x[1].equals("Mycobacterium smegmatis mc²155"))){
                remove.add(x);
            }
        }
        for(String[] x:remove){
            lines.remove(lines.indexOf(x));
        }
        return lines;
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
    public List<String> clusterList(List<String[]> all){
        List<String> clu = new ArrayList<>();
        for(String[] x : all){
            clu.add(x[2]);
        }
        Set<String> clust =new HashSet<String>(clu);
        List<String> cluster = new ArrayList<String>(clust);
        return cluster;
    }
    public List<String> phageNames(List<String[]> all){
        List<String> phages = new ArrayList<>();
        for(String[] x:all){
            phages.add(x[0]);
        }
        return phages;
    }
}
