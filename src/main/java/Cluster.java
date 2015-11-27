
import javafx.util.Pair;
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
    ImportPhagelist list;
    Cluster(){
        try {
            list =new ImportPhagelist();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Creates common sequences set for all clusters
    public List<Pair<String, Set<String>>> assignClusters(){
        Map<String, List<String[]>> collect = list.full.stream()
            .collect(Collectors.groupingBy(l -> l[0]));
        List<Pair<String, Set<String>>> clusters = collect.entrySet().parallelStream()
            .map(x -> {
                List<String> mapEntryValues = getPhageNames(x);
                String firstName = mapEntryValues.get(0);
                Set<String> clusterSet = Fasta.process(firstName,15);

                x.getValue().stream().skip(1).forEach(y -> {
                    clusterSet.retainAll(Fasta.process(y[1], 15));

                        }
                );
                return new Pair<>(x.getKey(),clusterSet);
            }).collect(Collectors.toList());
        System.out.println("Common done");
        return clusters;
    }
    //get phage names for use in previous method
    private static List<String> getPhageNames(Map.Entry<String, List<String[]>> mapEntry) {
        return mapEntry.getValue().stream()
                .map(x -> x[1])
                .collect(toList());
    }
    //creates unique sequences for all clusters using the common
    public List<Pair<String, Set<String>>> unique(List<Pair<String, Set<String>>> common){
        Map<String, List<String[]>> collect = list.full.stream()
                .collect(Collectors.groupingBy(l -> l[0]));
        List<Pair<String, Set<String>>> unique;
        unique = common.stream().map(x-> {
            Set<String> s = x.getValue();
            collect.entrySet().stream().forEach(y-> {
                if (x.getKey().equals(y.getKey())){}
                else{
                    y.getValue().forEach(z->s.removeAll(Fasta.process(z[1],15)));
                }
            });
            System.out.println(x.getKey() + " done");
            return new Pair<>(x.getKey(),s);
        }).collect(Collectors.toList());
        return unique;
    }
}