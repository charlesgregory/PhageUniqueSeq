//import com.nfsdb.journal.exceptions.JournalException;
import com.questdb.ex.JournalException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.DNASequence;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Thomas on 7/6/2016.
 */
@SuppressWarnings("Duplicates")
public class PrimerMatching {
    static Set<PrimerMatch> primerFragSet = new HashSet<>();
    static Map<PrimerMatch,Double[]>matchFrags= new HashMap<>();
    static int phagecount;
    static int clustsize;
    public static void matchPrimersNFSDB() throws ClassNotFoundException,
            SQLException, InstantiationException, IllegalAccessException, IOException, CompoundNotFoundException, JournalException {
        long time = System.nanoTime();
//        String base = new File("").getAbsolutePath();
        FastaManager fastaManager=FastaManager.getInstance();
        Map<List<String>, DNASequence> fastas = fastaManager.getMultiFasta();
        PrintWriter log = new PrintWriter(new File("javalog.log"));
        NFSDBManager db = new NFSDBManager();
        db.makeDB();
        List<String[]> phages = new ArrayList<>();
        for(Phages p:db.readPhages()) {
            String[] r = new String[3];
            r[0]=p.getStrain();
            r[1]=p.getCluster();
            r[2]=p.getName();
            phages.add(r);

        }
        Set<String> strains = phages.stream().map(y->y[0]).collect(Collectors.toSet());
        for(String x:strains) {

            /**
             FOR EACH STRAIN
             */

            Set<String> clust = phages.stream().filter(y -> y[0].equals(x)).map(y -> y[1]).collect(Collectors.toSet());
            String[] clusters = clust.toArray(new String[clust.size()]);
            for (String z : clusters) {

                /**
                 FOR EACH CLUSTER
                 */

                System.out.println("Starting:" + z);
                Map<Long,Double>primerTm = new HashMap<>();
                Set<Long> primers = new HashSet<>();
//                Set<Matches> matched = new HashSet<>();
                Set<String> clustphage = phages.stream()
                        .filter(a -> a[0].equals(x) && a[1].equals(z)).map(a -> a[2])
                        .collect(Collectors.toSet());
                String[] clustphages = clustphage.toArray(new String[clustphage.size()]);


                if (clustphages.length > 1) {

                    /**
                     GRAB PRIMERS
                     */
                    System.out.println(db.readPrimers(z).size());
                    long primer;
                    long rprimer;
                    for(Primers p:db.readPrimers(z)) {
                        if(p.getStrain().equals(x)&&(!p.isHairpin())) {
                            primer = p.getSequence();
                            if (!primerTm.containsKey(primer)) {
                                primerTm.put(primer, PrimerDesign.easytm(
                                        Encoding.twoBitDecode(primer)));
                            }
                            rprimer = Encoding.reEncodeReverseComplementTwoBit(primer);
                            if (!primerTm.containsKey(rprimer)) {
                                primerTm.put(rprimer, PrimerDesign.easytm(
                                        Encoding.twoBitDecode(rprimer)));
                            }
                            primers.add(primer);
//                    Map<String, Map<Long, Primer>> locations = Collections.synchronizedMap(
//                            new HashMap<>());
                        }
                    }
                    System.out.println(primers.size());
                    System.out.println(clustphages.length);
                    clustsize=clustphages.length;
                    phagecount=0;
                    Long[] primers2 = primers.toArray(new Long[primers.size()]);
                    match(x,z,clustphages[0],fastas,primers2,primerTm, true);
                    phagecount++;
                    for (int i = 1; i < clustphages.length; i++) {
                        /**
                         * FOR EACH PHAGE
                         */
                        match(x,z,clustphages[i],fastas,primers2,primerTm,false);
                        phagecount++;
//                        System.out.println(primerFragSet.size());
                    }
                }
                System.out.println((System.nanoTime() - time) / Math.pow(10, 9) / 60.0);
                System.out.println("Matches Compiled");
                System.out.println(primerFragSet.size());
                int count=0;
                double[] newA;
                Double[] arr;
                for(PrimerMatch m:primerFragSet){
//                    System.out.println(m.frags.length);
                    arr = matchFrags.get(m);
                    newA = new double[arr.length];
                    for(int i=0;i<arr.length;i++){
                        newA[i]=arr[i];
                    }
                    db.insertMatchedPrimer(m.foward,m.reverse,z,x,newA);
                    count++;
                }
                System.out.println(count);
                System.out.println();
                log.println(z);
                log.flush();
                System.gc();
                db.insertMatchedPrimerCommit();
            }
            System.out.println((System.nanoTime() - time) / Math.pow(10, 9) / 60.0);
        }
        System.out.println("Matches Submitted");
        db.db.close();
    }

    public static void match(String x, String z, String phage, Map<List<String>,
            DNASequence> fastas, Long[] primers2, Map<Long, Double> primerTm,
                             boolean first){
        /**
         * GRAB SEQ
         */

        List<String>id = new ArrayList<>();
        id.add(x);
        id.add(z);
        id.add(phage);
        String sequence = fastas.get(id).getSequenceAsString();
        Map<Long, int[]> seqInd = new HashMap<>();

        /**
         HASH SEQ
         */
        int[] rc,temp,list;
        long sub;
        for (int i = 0; i <= sequence.length() - 10; i++) {
            sub = Encoding.twoBitEncoding(sequence.substring(i, i + 10));
            if (seqInd.containsKey(sub)) {
                rc = seqInd.get(sub);
                temp = new int[rc.length+1];
                System.arraycopy(rc, 0, temp, 0, rc.length);
                temp[rc.length]=i;
                seqInd.replace(sub,temp);
            } else {
                list = new int[1];
                list[0]=i;
                seqInd.put(sub, list);
            }
        }

        /**
         LOCATION INDEXING
         */
        Map<Integer, Long> forward = new HashMap<>();
        Map<Integer, Long> reverse = new HashMap<>();
        long part,part2,rprimer;
        int[] integers,integersr;
        String sequence1,sequence2;
        for (Long primer : primers2) {
            sequence1 = Encoding.twoBitDecode(primer);
            part = Encoding.twoBitEncoding(sequence1.substring(0, 10));
            integers = seqInd.get(part);
            if (integers != null) {
                for (int i : integers) {
                    if ((sequence1.length() + i) < sequence.length() &&
                            sequence.substring(i, sequence1.length() + i).equals(sequence1)) {
                        forward.put(i,primer);
                    }
                }
            }
            rprimer = Encoding.reEncodeReverseComplementTwoBit(primer);
            sequence2 = Encoding.twoBitDecode(rprimer);
            part2 = Encoding.twoBitEncoding(sequence2.substring(0, 10));
            integersr = seqInd.get(part2);
            if (integersr != null) {
                for (int i : integersr) {
                    if ((sequence2.length() + i) < sequence.length() &&
                            sequence.substring(i, sequence2.length() + i).equals(sequence2)) {
                        reverse.put(i, rprimer);
                    }
                }
            }
        }
        /**
         * FRAGMENT FINDING
         */
        List<Integer> f = forward.keySet().stream().collect(Collectors.toList());
        List<Integer> r = reverse.keySet().stream().collect(Collectors.toList());
        Collections.sort(f);
        Collections.sort(r);
        int index =0;
//        int count =0;
        Map<PrimerMatch,PrimerMatch> primerMatchSet = new HashMap<>();
        Map<PrimerMatch, Double> matchFrags2 = new HashMap<>();
        Set<PrimerMatch> remove = new HashSet<>();
        int b,frag;
        PrimerMatch match;
        for(int a: f){
//            System.out.println(count);
//            count++;
            b=r.get(index);
            while(index<r.size()-1&&b<a){
                index++;
                b=r.get(index);
            }
            frag =b-a;
            while(frag<500&&index<r.size()-1){
                index++;
                b=r.get(index);
                frag = b-a;
            }
            while(frag<=2000&& index<r.size()-1){
                Long pF = forward.get(a);
                Long pR = reverse.get(b);
                if(Math.abs(primerTm.get(pF)-primerTm.get(pR))<5.0){
                    match = new PrimerMatch(pF,pR);
                    if(!primerMatchSet.containsKey(match)){
                        primerMatchSet.put(match,match);
                        matchFrags2.put(match,frag+0.0);
                    }else{
                        remove.add(match);
                    }
                    index++;
                    b=r.get(index);
                    frag = b-a;
                }else{
                    index++;
                    b=r.get(index);
                    frag = b-a;
                }
            }
            index =0;
        }
        for(PrimerMatch m:remove){
            primerMatchSet.remove(m);
            matchFrags2.remove(m);
        }
        if(first){
            primerFragSet=primerMatchSet.keySet();
            primerMatchSet.keySet().forEach(m->{
                Double[] arr = new Double[clustsize];
                arr[phagecount]=matchFrags2.get(m);
                matchFrags.put(m,arr);
            });
        }else{
            primerMatchSet.keySet().forEach(m->{
                if(primerFragSet.contains(m)){
                    Double[] arr = matchFrags.get(m);
                    arr[phagecount]=matchFrags2.get(m);
                    matchFrags.replace(m,arr);
                }
                else {
                    remove.add(m);
                }
            });
            for(PrimerMatch m:remove){
                primerMatchSet.remove(m);
            }
            primerFragSet=primerMatchSet.keySet();
        }
//        primerMatchSet.clear();
//        matchFrags2.clear();

    }
    private static class PrimerMatch{
        long foward;
        long reverse;
//        double[] frags;
//        public PrimerMatch(long f,long r, double frag){
        public PrimerMatch(long f,long r){
//            frags=new double[1];
            foward=f;
            reverse =r;
//            frags[0]=frag;
        }
        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 31)
                    .append(foward)
                    .append(reverse)
                    .toHashCode();
        }
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PrimerMatch))
                return false;
            if (obj == this)
                return true;

            PrimerMatch rhs = (PrimerMatch) obj;
            return foward==rhs.foward&&reverse==rhs.reverse;
        }


    }
}
