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
        return count * 1.0 /primer.length();
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
        return 64.9 +(41*((g*1.0)+(c*1.0)-16.4)/((a+t+g+c)*1.0));
    }
    //gets the location of a primer within a fasta file
    private static int primerLocation(CharSequence primer, String path) {
        int loc;
        List<CharSequence> seq = CSV.readNonSetCSV(path);
        if (seq.contains(primer)){
            loc = seq.indexOf(primer.toString());
        }
        else{
            System.out.println("Doesn't contain primer");
            loc=0;
        }
        return loc;
    }
    //gets the average location of a primer within a cluster
    private static int getAverageLocation(String cluster,CharSequence primer){
        String base = new File("").getAbsolutePath();
        ImportPhagelist list = null;
        try {
            list = ImportPhagelist.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, List<String[]>> collect = list.full.stream()
                .collect(Collectors.groupingBy(l -> l[0]));
        List<String[]> phages = collect.get(cluster);
        int count = 0;
        for (String[] x: phages){
             int position = primerLocation(primer,base+"\\PhageData\\"+x[1]+".csv");
             count = count+position;
        }
        return count/phages.size();

    }
    //sets parameters to select primers
    public static void getAllLocations(){
        String base = new File("").getAbsolutePath();
        ImportPhagelist list = null;
        File file = new File(base+"\\Locations");
        CSV.makeDirectory(file);
        File[] files1 = new File(base+"\\Unique\\").listFiles();
        List<File> uniqueFiles = new ArrayList<>();
        for(File x: files1){uniqueFiles.add(x);}
        uniqueFiles.stream().forEach(x->{
            String cluster = x.getAbsolutePath().substring(x.getAbsolutePath().indexOf("ue\\") + 3,
                    x.getAbsolutePath().indexOf(".csv"));
            List<CharSequence> unique = CSV.readNonSetCSV(x.getAbsolutePath());
            int[] locations = new int[unique.size()];
            int count = 0;
            for(CharSequence y: unique){
                locations[count] = getAverageLocation(cluster, y);
                count++;
            }
            try {
                CSV.writeLocationCSV(cluster,locations);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    private static Set<CharSequence> selectPrimers(Set<CharSequence> primers){
        Set<CharSequence> filter = primers.parallelStream()
                .filter(x -> (gcContent(x) <= 0.60) && (gcContent(x) >= 0.40)).collect(Collectors.toSet());
        //&& (sequenceTm(x) >= 55) && (sequenceTm(x) <= 70)
        System.out.println(filter.size());
        return filter;
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
            String cluster = x.getAbsolutePath().substring(x.getAbsolutePath().indexOf("ue\\") + 3,
                    x.getAbsolutePath().indexOf(".csv"));
            Set<CharSequence> unique = CSV.readCSV(x.getAbsolutePath());
            Set<CharSequence> uniqueFilter = selectPrimers(unique);
            try {
                CSV.writeFilteredCSV(cluster,uniqueFilter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
