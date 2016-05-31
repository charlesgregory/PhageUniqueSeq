import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**Copyright (C) 2016  Thomas Gregory

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.

 * Created by Thomas on 12/22/2015. Used for determination of ideal primers.
 * Depreciated due to the program now using Hsqldb as opposed to mainly using
 * CSV files
 */
@Deprecated
public class PrimerDesign {
    public static double Primer3Complementarity(CharSequence primer1, CharSequence primer2,
                                                DpalLoad.Dpal INSTANCE){
        DpalLoad.Dpal.dpal_args args = new DpalLoad.Dpal.dpal_args();
        DpalLoad.Dpal.dpal_results out = new DpalLoad.Dpal.dpal_results();
        INSTANCE.set_dpal_args(args);
        INSTANCE.dpal(primer1.toString().getBytes(),
                primer2.toString().getBytes(),args,out);
        return out.score;
    }
    public static void hairpin(CharSequence primer1, CharSequence primer2, ThalLoad.Thal INSTANCE){
        ThalLoad.Thal.thal_args args = new ThalLoad.Thal.thal_args();
        ThalLoad.Thal.thal_results out = new ThalLoad.Thal.thal_results();
        INSTANCE.set_thal_default_args(args);
        INSTANCE.thal(primer1.toString().getBytes(),primer2.toString().getBytes(),args,out);
        System.out.println(new String(out.msg));
        System.out.println(out.temp);
        System.out.println(args.dimer);
    }

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
        return 64.9 +(41*((g*1.0)+(c*1.0)-16.4)/((a+t+g+c)*1.0));
    }

    public static double primerTm(CharSequence primer, double salt_conc, double dna_conc,
                                  double divalent,double dntp){
        /**
         *Tables of nearest-neighbor thermodynamics for DNA bases, from the
         * paper [SantaLucia JR (1998) "A unified view of polymer, dumbbell
         * and oligonucleotide DNA nearest-neighbor thermodynamics", Proc Natl
         * Acad Sci 95:1460-65 http://dx.doi.org/10.1073/pnas.95.4.1460]
         */
        final double T_KELVIN = 273.15;

        //delta S for nucleotide pairs
        int DS_A_A = 222;
        int DS_A_C = 224;
        int DS_A_G = 210;
        int DS_A_T = 204;
        int DS_A_N = 224;
        int DS_C_A = 227;
        int DS_C_C = 199;
        int DS_C_G = 272;
        int DS_C_T = 210;
        int DS_C_N = 272;
        int DS_G_A = 222;
        int DS_G_C = 244;
        int DS_G_G = 199;
        int DS_G_T = 224;
        int DS_G_N = 244;
        int DS_T_A = 213;
        int DS_T_C = 222;
        int DS_T_G = 227;
        int DS_T_T = 222;
        int DS_T_N = 227;
        int DS_N_A = 168;
        int DS_N_C = 210;
        int DS_N_G = 220;
        int DS_N_T = 215;
        int DS_N_N = 220;

        //delta H for nucleotide pairs
        int DH_A_A = 79;
        int DH_A_C = 84;
        int DH_A_G = 78;
        int DH_A_T = 72;
        int DH_A_N = 72;
        int DH_C_A = 85;
        int DH_C_C = 80;
        int DH_C_G = 106;
        int DH_C_T = 78;
        int DH_C_N = 78;
        int DH_G_A = 82;
        int DH_G_C = 98;
        int DH_G_G = 80;
        int DH_G_T = 84;
        int DH_G_N = 80;
        int DH_T_A = 72;
        int DH_T_C = 82;
        int DH_T_G = 85;
        int DH_T_T = 79;
        int DH_T_N = 72;
        int DH_N_A = 72;
        int DH_N_C = 80;
        int DH_N_G = 78;
        int DH_N_T = 72;
        int DH_N_N = 72;

        // Delta G's of disruption * 1000.
        int DG_A_A = 1000;
        int DG_A_C =  1440;
        int DG_A_G = 1280;
        int DG_A_T = 880;
        int DG_A_N = 880;
        int DG_C_A = 1450;
        int DG_C_C = 1840;
        int DG_C_G = 2170;
        int DG_C_T = 1280;
        int DG_C_N = 1450;
        int DG_G_A = 1300;
        int DG_G_C = 2240;
        int DG_G_G = 1840;
        int DG_G_T = 1440;
        int DG_G_N = 1300;
        int DG_T_A =  580;
        int DG_T_C = 1300;
        int DG_T_G = 1450;
        int DG_T_T = 1000;
        int DG_T_N =  580;
        int DG_N_A =  580;
        int DG_N_C = 1300;
        int DG_N_G = 1280;
        int DG_N_T =  880;
        int DG_N_N =  580;

        //loops through primer to determine dh and ds
        int dh = 0;
        int ds = 0;
        int dg = 0;
        for(int i = 0;i<(primer.length()-1);i++){
            char first = primer.charAt(i);
            char sec = primer.charAt(i+1);
            if(first=='g'||first=='G'){
                if(sec=='g'||sec=='G'){
                    dh += DH_G_G;
                    dg += DG_G_G;
                    ds += DS_G_G;
                }
                else if(sec=='c'||sec=='C'){
                    dh += DH_G_C;
                    dg += DG_G_C;
                    ds += DS_G_C;
                }
                else if(sec=='t'||sec=='T'){
                    dh += DH_G_T;
                    dg += DG_G_T;
                    ds += DS_G_T;
                }
                else if(sec=='a'||sec=='A'){
                    dh += DH_G_A;
                    dg += DG_G_A;
                    ds += DS_G_A;
                }
            }
            else if(first=='c'||first=='C'){
                if(sec=='g'||sec=='G'){
                    dh += DH_C_G;
                    dg += DG_C_G;
                    ds += DS_C_G;
                }
                else if(sec=='c'||sec=='C'){
                    dh += DH_C_C;
                    dg += DG_C_C;
                    ds += DS_C_C;
                }
                else if(sec=='t'||sec=='T'){
                    dh += DH_C_T;
                    dg += DG_C_T;
                    ds += DS_C_T;
                }
                else if(sec=='a'||sec=='A'){
                    dh += DH_C_A;
                    dg += DG_C_A;
                    ds += DS_C_A;
                }
            }
            else if(first=='t'||first=='T'){
                if(sec=='g'||sec=='G'){
                    dh += DH_T_G;
                    dg += DG_T_G;
                    ds += DS_T_G;
                }
                else if(sec=='c'||sec=='C'){
                    dh += DH_T_C;
                    dg += DG_T_C;
                    ds += DS_T_C;
                }
                else if(sec=='t'||sec=='T'){
                    dh += DH_T_T;
                    dg += DG_T_T;
                    ds += DS_T_T;
                }
                else if(sec=='a'||sec=='A'){
                    dh += DH_T_A;
                    dg += DG_T_A;
                    ds += DS_T_A;
                }
            }
            else if(first=='a'||first=='A'){
                if(sec=='g'||sec=='G'){
                    dh += DH_A_G;
                    dg += DG_A_G;
                    ds += DS_A_G;
                }
                else if(sec=='c'||sec=='C'){
                    dh += DH_A_C;
                    dg += DG_A_C;
                    ds += DS_A_C;
                }
                else if(sec=='t'||sec=='T'){
                    dh += DH_A_T;
                    dg += DG_A_T;
                    ds += DS_A_T;
                }
                else if(sec=='a'||sec=='A'){
                    dh += DH_A_A;
                    dg += DG_A_A;
                    ds += DS_A_A;
                }
            }
        }
        //checks for symmetry
        int sym = -1;
        if(primer.length()%2 == 1){sym =0;}
        else{
            for(int i = 0; i<(primer.length()/2);i++){
                if ((primer.charAt(i)=='A' && primer.charAt(primer.length()-i)!='T'-1)
                        || (primer.charAt(i)=='T' && primer.charAt(primer.length()-i-1)!='A')
                        || (primer.charAt(i)=='C' && primer.charAt(primer.length()-i-1)!='G')
                        || (primer.charAt(i)=='G' && primer.charAt(primer.length()-i-1)!='C')) {
                    sym = 0;
                    break;
                }
                i++;
            }
            if(sym==-1){
                sym =1;
            }
        }
        //Assigns AT end penalty
        if(primer.charAt(0)=='A' || primer.charAt(0)=='T'){
            ds += -41;
            dh += -23;
        }
        else if (primer.charAt(0)=='G' || primer.charAt(0)=='C') {
            ds += 28;
            dh += -1;
        }

        if(primer.charAt(primer.length()-1)=='A' || primer.charAt(primer.length()-1)=='T'){
            ds += -41;
            dh += -23;
        }
        else if (primer.charAt(primer.length()-1)=='G' || primer.charAt(primer.length()-1)=='C') {
            ds += 28;
            dh += -1;
        }
        if(divalent==0) dntp=0;
        if(divalent<dntp) divalent=dntp;
        salt_conc = salt_conc +120*(Math.sqrt(divalent-dntp));



        double delta_H = dh * -100.0;
        double delta_S = ds * -0.1;
        delta_S = delta_S + 0.368 * (primer.length() - 1) * Math.log(salt_conc / 1000.0 );
        double tm;

        if(sym == 1) {
        tm = delta_H / (delta_S + 1.987 * Math.log(dna_conc/1000000000.0)) - T_KELVIN;
        }  else {
            tm = delta_H / (delta_S + 1.987 * Math.log(dna_conc/4000000000.0)) - T_KELVIN;
        }
        return tm;
    }
    @Deprecated
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
    @Deprecated
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
    @Deprecated
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
    @Deprecated
    private static Set<CharSequence> selectPrimers(Set<String> primers){
        Set<CharSequence> filter = primers.parallelStream()
                .filter(x -> (gcContent(x) <= 0.60) && (gcContent(x) >= 0.40)).collect(Collectors.toSet());
        //&& (sequenceTm(x) >= 55) && (sequenceTm(x) <= 70)
        System.out.println(filter.size());
        return filter;
    }
    @Deprecated
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
            Set<String> unique = CSV.readCSV(x.getAbsolutePath());
            Set<CharSequence> uniqueFilter = selectPrimers(unique);
            try {
                CSV.writeFilteredCSV(cluster,uniqueFilter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
