
import org.apache.commons.io.FileUtils;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.io.InputStreamReader;

/**
 * Created by Charles Gregory on 11/10/2015.
 * Controls the import of the phage list
 * from phagesdb.org
 */
public class ImportPhagelist {
    public static List<String[]> full;
    String path;
    Set<String> strains;
    String chosenStrain;
    //Singleton pattern
    private static ImportPhagelist instance;

    private ImportPhagelist() throws IOException {
        String base = new File("").getAbsolutePath();
        File file = new File(base+"\\Fastas");
        CSV.makeDirectory(file);
        this.path =Download();
        getStrains(path);
    }

    public static ImportPhagelist getInstance() throws IOException {
        if (instance == null) {
            instance = new ImportPhagelist();
        }
        return instance;
    }
    private void getStrains(String path1) throws IOException {
        String cvsSplitBy = "\\t";
        List<String[]> lines = null;
        try (FileInputStream fis = new FileInputStream(path1);
             BufferedReader br = new BufferedReader( new InputStreamReader(fis))) {
            lines = br.lines().map((l) -> l.split(cvsSplitBy)).collect(Collectors.toList());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.strains = lines.stream().skip(1).map(x -> x[1]).collect(Collectors.toSet());;

    }
    //parses phagelist tsv file and preselects only mycobacterium smegmatis phages
    public List<String[]> readFile(String path1, String strain) throws IOException {
        String cvsSplitBy = "\\t";
        List<String[]> lines = null;
        try (FileInputStream fis = new FileInputStream(path1);
            BufferedReader br = new BufferedReader( new InputStreamReader(fis))) {
            lines = br.lines().map((l) -> l.split(cvsSplitBy)).collect(Collectors.toList());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        List<String[]> collect = lines.stream().skip(1).filter(x -> x[1].equals(strain))
                .filter(x->!(x[0].equals("Byougenkin")||x[0].equals("phiBT1")))
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
    //used for testing and bug fixing
    public List<String[]> readFileAll(String path1) throws IOException {
        String cvsSplitBy = "\\t";
        List<String[]> lines = null;
        try (FileInputStream fis = new FileInputStream(path1);
             BufferedReader br = new BufferedReader( new InputStreamReader(fis))) {
            lines = br.lines().map((l) -> l.split(cvsSplitBy)).collect(Collectors.toList());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        List<String[]> collect = lines.stream().skip(1).filter(x->!(x[0].equals("Byougenkin")||x[0].equals("phiBT1")))
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
    public List<String[]> readFileAllStrains(String path1) throws IOException {
        String cvsSplitBy = "\\t";
        List<String[]> lines = null;
        try (FileInputStream fis = new FileInputStream(path1);
             BufferedReader br = new BufferedReader( new InputStreamReader(fis))) {
            lines = br.lines().map((l) -> l.split(cvsSplitBy)).collect(Collectors.toList());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        List<String[]> collect = lines.stream().skip(1).filter(x->!(x[0].equals("Byougenkin")||x[0].equals("phiBT1")))
                .map(x -> {
                            if(x[2].equals("Singleton")){
                                String[] r = new String[3];
                                r[0] = x[0];
                                r[1] = x[0];
                                r[2] = x[1];
                                return r;
                            }
                            else{
                                String[] r = new String[3];
                                r[0] = x[2];
                                r[1] = x[0];
                                r[2] = x[1];
                                return r;
                            }
                        }
                ).collect(Collectors.toList());
        return collect;
    }
    //Downloads phagelist file from phagesdb.org
    private static String Download() throws IOException {
        String path = "http://phagesdb.org/data/?set=seq&type=full";
        String base = new File("").getAbsolutePath();
        String name = base + "/Fastas/PhagesDB_Data.txt";
        File file = new File(name);
        URL netPath = new URL(path);
        FileUtils.copyURLToFile(netPath, file);
        return file.toString();
    }
}
