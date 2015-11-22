import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by musta_000 on 11/5/2015.
 */

public class Test{
    public static void main(String[] args)throws IOException {
        ImportPhagelist list = new ImportPhagelist();
        Cluster c = new Cluster(list);
        Set<Set<String>> d = c.assignClusters();
        System.out.println(d.size());
//        List<Fasta> fastas = new ArrayList<>();
//        List<Fasta> fastas = list.allPhages.parallelStream().map((x) -> {
//            Fasta seq2 = null;
//            try {
//                return new Fasta(x, 15);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return seq2;
//        }).collect(Collectors.toList());
//        System.out.println(fastas.toString());
    }
}
