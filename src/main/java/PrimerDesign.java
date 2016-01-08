import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Thomas on 12/22/2015. Will be used for future analysis for ideal primers.
 */
public class PrimerDesign {
    //finds gc content of a nucleotide
    public static double gcContent(CharSequence primer){
        int i = 0;
        int count = 0;
        while(i < primer.length()){
            char nuc = primer.charAt(i);
            if((nuc == 'G')||(nuc == 'C')||(nuc == 'g')||(nuc == 'c')){
                count +=1;
            }
            i++;
        }
        return count/primer.length();
    }
    //estimates a nucleotide melting temperature
    public static double sequenceTm(CharSequence primer){
        //64.9 +41*(yG+zC-16.4)/(wA+xT+yG+zC)
        int g = 0;
        int c = 0;
        int a = 0;
        int t = 0;
        int i = 0;
        while(i < primer.length()){
            char nuc = primer.charAt(i);
            if((nuc == 'G')||(nuc == 'g')){
                g++;
            }
            if((nuc == 'C')||(nuc == 'c')){
                c++;
            }
            if((nuc == 'A')||(nuc == 'a')){
                a++;
            }
            if((nuc == 'T')||(nuc == 't')){
                t++;
            }
            i++;
        }
        return 64.9 +41*(g+c-16.4)/(a+t+g+c);
    }
    //sets parameters to select primers for
    public static Set<CharSequence> selectPrimers(Set<CharSequence> primers){
        return primers.parallelStream()
                .filter(x-> (gcContent(x)<=0.60)&&(gcContent(x)>=0.40)
                &&(sequenceTm(x)>=55)&&(sequenceTm(x)<=70)).collect(Collectors.toSet());
    }
    public static int primerLocation(CharSequence primer, File fasta) {
        int loc;
        String[] seq = Fasta.parse(fasta.getAbsolutePath());
        if (seq[0].contains(primer)){
            loc = seq[0].indexOf(primer.toString());
        }
        else if(seq[1].contains(primer)){
            loc = seq[1].indexOf(primer.toString());
        }
        else{
            System.out.println("Doesn't contain primer");
            loc=0;
        }
        return loc;
    }
    public static void getAverageLocation(String cluster,CharSequence primer){
        String base = new File("").getAbsolutePath();
        ImportPhagelist list = null;
        try {
            list = ImportPhagelist.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File[] files1 = new File(base+"\\Fastas\\").listFiles();
        List<File> fastaFiles = new ArrayList<>();
        Map<String, List<String[]>> collect = list.full.stream()
                .collect(Collectors.groupingBy(l -> l[0]));
        List<String[]> phages = collect.get(cluster);

    }
}
