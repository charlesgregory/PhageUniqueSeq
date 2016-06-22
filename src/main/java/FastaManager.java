import org.apache.commons.io.FileUtils;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by Thomas on 6/14/2016.
 */
@SuppressWarnings("Duplicates")
public class FastaManager {

    public static void download() throws IOException {
        String base = new File("").getAbsolutePath();
        File file = new File(base+"/Fastas/Mycobacteriophages-All.fasta");
        URL netPath = new URL("http://phagesdb.org/media/Mycobacteriophages-All.fasta");
        FileUtils.copyURLToFile(netPath, file);
    }
    public static Map<List<String>, DNASequence> getMultiFasta() throws IOException {
        ImportPhagelist list = ImportPhagelist.getInstance();
        List<String[]> rawlist = list.readFileAllStrains(list.path);
        List<String[]> rawlistSimple = list.readFileAllStrainsSimple(list.pathSimple);
        String base = new File("").getAbsolutePath();
        File file = new File(base+"/Fastas/Mycobacteriophages-All.fasta");
//        URL netPath = new URL("http://phagesdb.org/media/Mycobacteriophages-All.fasta");
//        FileUtils.copyURLToFile(netPath, file);
        LinkedHashMap<String, DNASequence> f = FastaReaderHelper.readFastaDNASequence(
                new File(file.getAbsolutePath()));
        Map<List<String>, DNASequence> newFastaList = new HashMap<>();
        for(String x:f.keySet()){
            List<String>entry = new ArrayList<>();
            String[] words = x.split(" ");
//            System.out.println(x);
            entry.add(words[0].replace(">","").replace("Mycobacteriophage","Mycobacterium"));
            String name = words[2].replace(",", "").replace("_complete","");
            if(name.equals("sequence")){
                name = words[0].replace(",", "");
            }else if(name.equals("Revised")){
                name = words[3].replace(",", "");
            }
            for(String[] z:rawlist){
                if(z[1].equalsIgnoreCase(name)){
                    entry.add(z[0]);
                }
            }
            if(entry.size()!=2){
                for(String[] z:rawlistSimple){
                    if(z[1].equalsIgnoreCase(name)){
                        entry.add(z[0]);
                    }
                }
                if(entry.size()!=2){
                    int i = 0;
                    for(String word:words){
                        if(word.equals("Cluster")||word.equals("cluster")){
                            break;
                        }
                        i++;
                    }
                    if(i+1<words.length) {
                        entry.add(words[i + 1]);
                        entry.add(words[2]);
                        newFastaList.put(entry, f.get(x));
                    }else{
                        System.out.println(x);
                    }

                }else {
                    entry.add(words[2]);
                    newFastaList.put(entry, f.get(x));
                }
            }else {
                entry.add(words[2]);
                newFastaList.put(entry, f.get(x));
            }
        }
//        for(List<String> e:newFastaList.keySet()){
//            System.out.println(e.toString());
//        }
//        System.out.println(newFastaList.keySet().size());
//        System.out.println(f.keySet().size());
        return newFastaList;
    }
}
