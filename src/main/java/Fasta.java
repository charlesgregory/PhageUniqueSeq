
import org.apache.commons.io.FileUtils;
import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.io.FastaReaderHelper;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by musta_000 on 11/6/2015.
 */
public class Fasta {
    Set<String> prims;
    String seq;
    int bp;
    Fasta(String name, int bps){
        bp = bps;
    }
    public String parse(String path){
        String seq1 = null;
        try{
            LinkedHashMap<String, DNASequence> f = FastaReaderHelper.readFastaDNASequence(new File(path));
            List<String> dnaList = new ArrayList<>(1);
            for (Map.Entry<String, DNASequence> stringDNASequenceEntry : f.entrySet()) {
                dnaList.add(stringDNASequenceEntry.getValue().getSequenceAsString());
            }
            seq1 = dnaList.get(0);
        }
        catch (java.lang.Exception e){
            System.out.print(e);
        }
        return seq1;
    }
    public Set<String> splitFasta(String seq, int length){

        String[] prim = new String[seq.length()-length];
        Set<String> collect = IntStream.range(0, length).mapToObj(start -> {
            List<String> primers = new ArrayList<>();
            for (int i = start; i < seq.length() - length; i += length) {
                primers.add(seq.substring(i, i + length));
            }
            return primers;
        }).flatMap((i) -> i.stream()).collect(Collectors.toSet());
        return collect;
    }
    public static String Download(String name) throws IOException {
        String path;
        if(name.equals("BrownCNA")){
            path ="http://phagesdb.org/media/fastas/Browncna.fasta";
        }
        else if(name.equals("GUmbie")){
            path ="http://phagesdb.org/media/fastas/Gumbie.fasta";
        }
        else if(name.equals("Numberten")){
            path ="http://phagesdb.org/media/fastas/NumberTen.fasta";
        }
        else if(name.equals("Seabiscuit")){
            path ="http://phagesdb.org/media/fastas/SeaBiscuit.fasta";
        }
        else{
            path = "http://phagesdb.org/media/fastas/"+name+".fasta";
        }
        String base = new File("").getAbsolutePath();
        name = base+"\\src\\main\\java\\Fastas\\"+name+".fasta";
        File file = new File(name);
        URL netPath = new URL(path);
        FileUtils.copyURLToFile(netPath,file);
        return file.toString();
    }
    public Set<String> process(String name) throws IOException {
        String path = Download(name);
        seq = parse(path);
        prims = splitFasta(seq,bp);
        return prims;
    }
}
