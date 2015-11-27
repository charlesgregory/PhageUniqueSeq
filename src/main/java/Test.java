import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by musta_000 on 11/5/2015.
 */

public class Test{
    //Used for testing classes
    public static void main(String[] args)throws IOException {
        Cluster c = new Cluster();
        List<Pair<String, Set<String>>> f =c.unique(c.assignClusters());
        System.out.println(f.size());
    }
}
