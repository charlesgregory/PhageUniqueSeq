
import java.util.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
/**
 * Created by musta_000 on 11/17/2015.
 */
public class Cluster {
    ImportPhagelist list;
    Cluster(ImportPhagelist all){
        list =all;
    }

    public Set<Set<String>> assignClusters(){
        Map<String, List<String[]>> collect = list.full.stream()
                .collect(Collectors.groupingBy(l -> l[0]));
        Set<Set<String>> clusters = collect.entrySet().parallelStream()
                .map(x -> {
                            List<String> mapEntryValues = getPhageNames(x);
                            String firstName = mapEntryValues.get(0);
                            Set<String> clusterSet = Fasta.process(firstName,15);

                            x.getValue().stream().skip(1).forEach(y -> {
                                clusterSet.retainAll(Fasta.process(y[1], 15));

                                    }
                            );
                            return clusterSet;
                        }
                ).collect(Collectors.toSet());
        return clusters;
    }
    private static List<String> getPhageNames(Map.Entry<String, List<String[]>> mapEntry) {
        return mapEntry.getValue().stream()
                .map(x -> x[1])
                .collect(toList());
    }
    public static Set<Set<String>> unique(Set<Set<String>> common){
        common.forEach(x->
            common.stream().filter(f -> f != x).forEach(x::removeAll)
        );
        return common;
    }
}