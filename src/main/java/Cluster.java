import java.util.*;

/**
 * Created by musta_000 on 11/17/2015.
 */
public class Cluster {
    ImportPhagelist list;
    Cluster(ImportPhagelist all){
        list =all;
    }
    public HashMap<String, String> assignClusters(){
        HashMap<String, String> clusters = new HashMap<>();
        list.full.stream().forEach(
                x-> clusters.put(x[2],x[0])
        );


        return clusters;
    }
}
