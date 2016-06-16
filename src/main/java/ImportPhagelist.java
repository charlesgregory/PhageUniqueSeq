
import org.apache.commons.io.FileUtils;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.io.InputStreamReader;

/**
 * Copyright (C) 2016  Thomas Gregory

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.

 * Created by Charles Gregory on 11/10/2015.
 * Controls the import of the phage list
 * from phagesdb.org
 */
public class ImportPhagelist {
    public static List<String[]> full;
    String path;
    String pathSimple;
    Set<String> strains;
    String chosenStrain;
    //Singleton pattern
    private static ImportPhagelist instance;

    private ImportPhagelist() throws IOException {
        String base = new File("").getAbsolutePath();
        File file = new File(base+"\\Fastas");
        CSV.makeDirectory(file);
        this.path =Download();
        this.pathSimple = DownloadSimple();
//        getStrains(path);
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
    //parses phagelist tsv file and preselects only strain phages
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
                .filter(x->!(x[0].equals("Byougenkin")||x[0].equals("phiBT1")||x[0].equals("Swenson")))
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
        List<String[]> collect = lines.stream().skip(1).filter(x->!(x[0].equals("Byougenkin")||x[0].equals("phiBT1")||x[0].equals("Swenson")))
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
        List<String[]> collect = lines.stream()
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
    public List<String[]> readFileAllStrainsSimple(String path1) throws IOException {
        String cvsSplitBy = "\\t";
        List<String[]> lines = null;
        try (FileInputStream fis = new FileInputStream(path1);
             BufferedReader br = new BufferedReader( new InputStreamReader(fis))) {
            lines = br.lines().map((l) -> l.split(cvsSplitBy)).collect(Collectors.toList());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        List<String[]> collect = lines.stream().map(x -> {
                            if(x[1].equals("Singleton")){
                                String[] r = new String[2];
                                r[0] = x[0];
                                r[1] = x[0];
                                return r;
                            }
                            else{
                                String[] r = new String[2];
                                r[0] = x[1];
                                r[1] = x[0];
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
    private static String DownloadSimple() throws IOException {
        String path = "http://phagesdb.org/data/?set=seq&type=simple";
        String base = new File("").getAbsolutePath();
        String name = base + "/Fastas/PhagesDB_Data_Simple.txt";
        File file = new File(name);
        URL netPath = new URL(path);
        FileUtils.copyURLToFile(netPath, file);
        return file.toString();
    }
    @Deprecated
    public void parseAllPhagePrimers(int bps) throws IOException {
        this.readFileAll(this.path)
                .stream().forEach(x -> {
            try {
                CSV.writeDataCSV(x[1], Fasta.processPrimers(x[1], bps),bps);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    @Deprecated
    public void parseAllPhages(int bps) throws IOException {
        this.readFileAll(this.path)
                .stream().forEach(x -> {
            try {
                CSV.writeDataCSV(x[1], Fasta.process(x[1], bps),bps);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
