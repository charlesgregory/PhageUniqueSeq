import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by musta_000 on 11/17/2015.
 */
public class Cluster {
    ImportPhagelist list;
    Cluster(ImportPhagelist all){
        list =all;
    }
    public Map<String, List<String[]>> assignClusters(){
        Map<String, List<String[]>> collect = list.full.stream()
                .collect(Collectors.groupingBy(l -> l[0]));
        collect.entrySet().stream()
            .map(x->{
                Set<String> s = new HashSet<>();
                x.getValue().forEach(y->{
                            try {
                                s.retainAll(Fasta.process(y[1],15));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                )
                    }
            );
        return clusters;
    }
}
