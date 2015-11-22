
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

    public static String[] parse(String path){
        String seq1 = null;
        String seq2 = null;
        String[] r = new String[2];
        try{
            LinkedHashMap<String, DNASequence> f = FastaReaderHelper.readFastaDNASequence(new File(path));
            List<String> dnaList = new ArrayList<>(1);
            for (Map.Entry<String, DNASequence> stringDNASequenceEntry : f.entrySet()) {
                dnaList.add(stringDNASequenceEntry.getValue().getSequenceAsString());
                dnaList.add(stringDNASequenceEntry.getValue()
                        .getReverseComplement().getSequenceAsString());
            }
            seq1 = dnaList.get(0);
            seq2 = dnaList.get(1);
            r[0] = seq1;
            r[1] = seq2;
        }
        catch (java.lang.Exception e){
            System.out.print(e);
        }
        return r;
    }
    public static Set<String> splitFasta(String[] seq, int length){

        Set<String> collect = IntStream.range(0, length).mapToObj(start -> {
            List<String> primers = new ArrayList<>();
            for (int i = start; i < seq[0].length() - length; i += length) {
                primers.add(seq[0].substring(i, i + length));
            }
            return primers;
        }).flatMap((i) -> i.stream()).collect(Collectors.toSet());
        Set<String> collect2 = IntStream.range(0, length).mapToObj(start -> {
            List<String> primers = new ArrayList<>();
            for (int i = start; i < seq[1].length() - length; i += length) {
                primers.add(seq[1].substring(i, i + length));
            }
            return primers;
        }).flatMap((i) -> i.stream()).collect(Collectors.toSet());
        collect.addAll(collect2);
        return collect;
    }
    public static String Download(String name) {
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
        try {
            URL netPath = new URL(path);
            FileUtils.copyURLToFile(netPath,file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.toString();
    }
    public static Set<String> process(String name, int bps){
        String path = Download(name);
        String[] seq = parse(path);
        Set<String> prims = splitFasta(seq, bps);
        return prims;
    }
}
