
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
/**
 * Created by musta_000 on 11/17/2015.
 * Manages the cluster operations. Takes all phages from the phage list
 * and can perform determination of all common sequences and all unique sequences
 */
public class Cluster {
    String base = new File("").getAbsolutePath();
    int bps;
    ImportPhagelist list;
    Cluster(int bp){
        bps=bp;
        try {
            list =new ImportPhagelist();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void allPhages(){
        list.full.forEach(x->{
            try {
                CSVOut.writeDataCSV(x[1],Fasta.splitFasta(Fasta.parse(Fasta.Download(x[1])),bps));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        System.out.println("Phages Processed");
    }
    //Creates common sequences set for all clusters
    public void assignClusters(){
        Map<String, List<String[]>> collect = list.full.stream()
            .collect(Collectors.groupingBy(l -> l[0]));
        Map<String, Set<CharSequence>> clusters = collect.entrySet().parallelStream()
            .map(x -> {
                List<String> mapEntryValues = getPhageNames(x);
                String firstName = mapEntryValues.get(0);
                Set<CharSequence> clusterSet = ImportCSV.readFile(base+"\\src\\main\\java\\PhageData\\"+firstName+".csv");

                x.getValue().stream().skip(1).forEach(y -> {
                    clusterSet.retainAll(ImportCSV.readFile(base+"\\src\\main\\java\\PhageData\\"+y[1]+".csv"));

                        }
                );
                return new Pair<>(x.getKey(),clusterSet);
            }).collect(Collectors.toMap(pair -> pair.getKey(), s-> s.getValue()));
        clusters.entrySet().forEach(x -> {
            try {
                CSVOut.writeCommonCSV(x.getKey(), x.getValue());
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
    public Map<String, Set<CharSequence>> unique(Map<String, Set<CharSequence>> common){
        Map<String, Set<CharSequence>> unique;
        unique = common.entrySet().parallelStream().map(x-> {
            Set<CharSequence> s = x.getValue();
            s.removeAll(Fasta.processAll(list,x.getKey(),bps));
            System.out.println(x.getKey() + " done");
            return new Pair<>(x.getKey(),s);
        }).collect(Collectors.toMap(pair -> pair.getKey(), s-> s.getValue()));
        return unique;
    }
    public void unique2(){
        File[] files1 = new File(base+"\\src\\main\\java\\Common\\").listFiles();
        List<File> commonFiles = new ArrayList<>();
        for(File x: files1){commonFiles.add(x);}
        File[] files2 = new File(base+"\\src\\main\\java\\PhageData\\").listFiles();
        List<File> phageFiles = new ArrayList<>();
        for(File x: files2){phageFiles.add(x);}
        Map<String, List<String[]>> collect = list.full.stream()
                .collect(Collectors.groupingBy(l -> l[0]));
        commonFiles.parallelStream().forEach(x-> {
            Set<CharSequence> common = ImportCSV.readFile(x.getAbsolutePath());
            String cluster = x.getAbsolutePath().substring(x.getAbsolutePath().indexOf("on\\") + 3,
                    x.getAbsolutePath().indexOf(".csv"));
            Set<String> clusterPhages = collect.get(cluster).stream().map(z -> z[1]).collect(Collectors.toSet());
            phageFiles.stream().forEach(y->{
                String phage = y.getAbsolutePath().substring(y.getAbsolutePath().indexOf("ata\\") + 4,
                        y.getAbsolutePath().indexOf(".csv"));
                if (clusterPhages.contains(phage)) {
                } else {
                    common.removeAll(ImportCSV.readFile(y.getAbsolutePath()));
                }
            });

            try {
                CSVOut.writeUniqueCSV(cluster,common);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(cluster);

        });
    }
}