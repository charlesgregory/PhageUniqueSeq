import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Thomas on 12/22/2015. Used for determination of ideal primers.
 */
public class PrimerDesign {
    //finds gc content of a nucleotide
    private static double gcContent(CharSequence primer){
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
    private static double sequenceTm(CharSequence primer){
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
    //sets parameters to select primers
    private static Set<CharSequence> selectPrimers(Set<CharSequence> primers){
        return primers.parallelStream()
                .filter(x-> (gcContent(x)<=0.60)&&(gcContent(x)>=0.40)
                        &&(sequenceTm(x)>=55)&&(sequenceTm(x)<=70)).collect(Collectors.toSet());
    }
    //Uses the select primers method to filter primers for all uniques
    public static void filterInitialUnique(){
        String base = new File("").getAbsolutePath();
        ImportPhagelist list = null;
        File file = new File(base+"\\Filter");
        CSV.makeDirectory(file);
        File[] files1 = new File(base+"\\Unique\\").listFiles();
        List<File> uniqueFiles = new ArrayList<>();
        for(File x: files1){uniqueFiles.add(x);}
        uniqueFiles.stream().forEach(x->{
            Set<CharSequence> unique = CSV.readCSV(x.getAbsolutePath());
            String cluster = x.getAbsolutePath().substring(x.getAbsolutePath().indexOf("ue\\") + 3,
                    x.getAbsolutePath().indexOf(".csv"));
            Set<CharSequence> uniqueFilter = selectPrimers(unique);
            try {
                CSV.writeFilteredCSV(cluster,uniqueFilter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
