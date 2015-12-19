
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
/**
 * Created by Charles Gregory on 11/17/2015.
 * Manages the cluster operations. Takes all phages from the phage list
 * and can perform determination of all common sequences and all unique sequences
 */
public class Cluster {

    public static void allPhages(int bps){
        ImportPhagelist list = null;
        try {
            list = ImportPhagelist.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
        list.full.forEach(x->{
            try {
                CSV.writeDataCSV(x[1],Fasta.process(x[1],bps));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        System.out.println("Phages Processed");
    }
    //Creates common sequences set for all clusters
    public static void assignClusters(){
        String base = new File("").getAbsolutePath();
        ImportPhagelist list = null;
        try {
            list = ImportPhagelist.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, List<String[]>> collect = list.full.stream()
            .collect(Collectors.groupingBy(l -> l[0]));
        Map<String, Set<CharSequence>> clusters = collect.entrySet().parallelStream()
            .map(x -> {
                List<String> mapEntryValues = getPhageNames(x);
                String firstName = mapEntryValues.get(0);
                Set<CharSequence> clusterSet = CSV.readCSV(base+"\\src\\main\\java\\PhageData\\"+firstName+".csv");

                x.getValue().stream().skip(1).forEach(y -> {
                    clusterSet.retainAll(CSV.readCSV(base+"\\src\\main\\java\\PhageData\\"+y[1]+".csv"));

                        }
                );
                return new Pair<>(x.getKey(),clusterSet);
            }).collect(Collectors.toMap(pair -> pair.getKey(), s-> s.getValue()));
        clusters.entrySet().forEach(x -> {
            try {
                CSV.writeCommonCSV(x.getKey(), x.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        System.out.println("Common done");
    }
    //get phage names for use in previous method
    private static List<String> getPhageNames(Map.Entry<String, List<String[]>> mapEntry) {
        return mapEntry.getValue().stream()
                .map(x -> x[1])
                .collect(toList());
    }
    //creates unique sequences for all clusters using the common
    public static void unique(){
        String base = new File("").getAbsolutePath();
        ImportPhagelist list = null;
        try {
            list = ImportPhagelist.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File[] files1 = new File(base+"\\src\\main\\java\\Common\\").listFiles();
        List<File> commonFiles = new ArrayList<>();
        for(File x: files1){commonFiles.add(x);}
        File[] files2 = new File(base+"\\src\\main\\java\\PhageData\\").listFiles();
        List<File> phageFiles = new ArrayList<>();
        for(File x: files2){phageFiles.add(x);}
        Map<String, List<String[]>> collect = list.full.stream()
                .collect(Collectors.groupingBy(l -> l[0]));
        commonFiles.parallelStream().forEach(x-> {
            Set<CharSequence> common = CSV.readCSV(x.getAbsolutePath());
            String cluster = x.getAbsolutePath().substring(x.getAbsolutePath().indexOf("on\\") + 3,
                    x.getAbsolutePath().indexOf(".csv"));
            Set<String> clusterPhages = collect.get(cluster).stream().map(z -> z[1]).collect(Collectors.toSet());
            phageFiles.stream().forEach(y->{
                String phage = y.getAbsolutePath().substring(y.getAbsolutePath().indexOf("ata\\") + 4,
                        y.getAbsolutePath().indexOf(".csv"));
                if (clusterPhages.contains(phage)) {
                } else {
                    common.removeAll(CSV.readCSV(y.getAbsolutePath()));
                }
            });

            try {
                CSV.writeUniqueCSV(cluster,common);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(cluster);

        });
    }
}