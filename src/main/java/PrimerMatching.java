import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.DNASequence;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Thomas on 7/6/2016.
 */
@SuppressWarnings("Duplicates")
public class PrimerMatching {
    public static void matchPrimers(Connection db) throws ClassNotFoundException,
            SQLException, InstantiationException, IllegalAccessException, IOException, CompoundNotFoundException {
        long time = System.nanoTime();
        String base = new File("").getAbsolutePath();
        Map<List<String>, DNASequence> fastas = FastaManager.getMultiFasta();
        Statement stat = db.createStatement();
        PrintWriter log = new PrintWriter(new File("javalog.log"));
        stat.execute("SET AUTOCOMMIT FALSE;");
        DBManager insert = new DBManager(db);
        ResultSet call = stat.executeQuery("Select * From Phages;");
        List<String[]> phages = new ArrayList<>();
        while (call.next()) {
            String[] r = new String[3];
            r[0]=call.getString("Strain");
            r[1]=call.getString("Cluster");
            r[2]=call.getString("Name");
            phages.add(r);
        }
        call.close();
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
                Set<PrimerMatch> primerFragSet = new HashSet<>();
//                Set<Matches> matched = new HashSet<>();
                Set<String> clustphage = phages.stream()
                        .filter(a -> a[0].equals(x) && a[1].equals(z)).map(a -> a[2])
                        .collect(Collectors.toSet());
                String[] clustphages = clustphage.toArray(new String[clustphage.size()]);


                if (clustphages.length > 1) {

                    /**
                    GRAB PRIMERS
                     */
                    try {
                        ResultSet resultSet = stat.executeQuery("Select * from primers" +
                                " where Strain ='" + x + "' and Cluster ='" + z + "' and UniqueP = true" +
                                " and Hairpin = false");
                        while (resultSet.next()) {
//                            Primer primer = new Primer(resultSet.getLong("Sequence"));
                            long primer =resultSet.getLong("Sequence");
//                            primer.setTm(HSqlPrimerDesign.easytm(Encoding.twoBitDecode(primer.getSequence())));
//                            primers.add(primer);
                            if(!primerTm.containsKey(primer)) {
                                primerTm.put(primer, HSqlPrimerDesign.easytm(
                                        Encoding.twoBitDecode(primer)));
                            }
                            long rprimer = Encoding.reEncodeReverseComplementTwoBit(primer);
                            if(!primerTm.containsKey(rprimer)) {
                                primerTm.put(rprimer, HSqlPrimerDesign.easytm(
                                        Encoding.twoBitDecode(rprimer)));
                            }
                            primers.add(primer);
                        }
                        resultSet.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("Error occurred at " + x + " " + z);
                    }
                    System.out.println(primers.size());
                    Long[] primers2 = primers.toArray(new Long[primers.size()]);
//                    Map<String, Map<Long, Primer>> locations = Collections.synchronizedMap(
//                            new HashMap<>());
                    primerFragSet=match(x,z,clustphages[0],fastas,primers2,primerTm,primerFragSet,true);
                    for (int i = 1; i < clustphages.length; i++) {
                        /**
                         * FOR EACH PHAGE
                         */
                        primerFragSet =match(x,z,clustphages[i],fastas,primers2,primerTm,primerFragSet,false);
                    }
                }
                System.out.println((System.nanoTime() - time) / Math.pow(10, 9) / 60.0);
                System.out.println("Matches Compiled");
                System.out.println(primerFragSet.size());
                System.out.println();
                for(PrimerMatch m:primerFragSet){
                    insert.matchedPrimerSubmit(m.foward,m.reverse,m.frags,z,x);
                }
                insert.matchPrimerInsertFinal();
                log.println(z);
                log.flush();
                System.gc();
            }
            System.out.println((System.nanoTime() - time) / Math.pow(10, 9) / 60.0);
        }

        stat.execute("SET AUTOCOMMIT TRUE;");
//        st.close();
        stat.close();
        System.out.println("Matches Submitted");
    }
    public static Set<PrimerMatch> match(String x, String z, String phage, Map<List<String>,
            DNASequence> fastas, Long[] primers2, Map<Long, Double>primerTm,
                                         Set<PrimerMatch>primerFragSet, boolean first){
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

        for (int i = 0; i <= sequence.length() - 10; i++) {
            long sub = Encoding.twoBitEncoding(sequence.substring(i, i + 10));
            if (seqInd.containsKey(sub)) {
                int[] r =seqInd.get(sub);
                int[] temp = new int[r.length+1];
                System.arraycopy(r, 0, temp, 0, r.length);
                temp[r.length]=i;
                seqInd.replace(sub,temp);
            } else {
                int[] list =new int[1];
                list[0]=i;
                seqInd.put(sub, list);
            }
        }

        /**
         LOCATION INDEXING
         */
        Map<Integer, Long> forward = new HashMap<>();
        Map<Integer, Long> reverse = new HashMap<>();
        for (Long primer : primers2) {
            String sequence1 = Encoding.twoBitDecode(primer);
            long frag = Encoding.twoBitEncoding(sequence1.substring(0, 10));
            int[] integers = seqInd.get(frag);
            if (integers != null) {
                for (int i : integers) {
                    if ((sequence1.length() + i) < sequence.length() &&
                            sequence.substring(i, sequence1.length() + i).equals(sequence1)) {
                        forward.put(i,primer);
                    }
                }
            }
            long rprimer =Encoding.reEncodeReverseComplementTwoBit(primer);
            String sequence2 = Encoding.twoBitDecode(rprimer);
            long frag2 = Encoding.twoBitEncoding(sequence2.substring(0, 10));
            int[] integersr = seqInd.get(frag2);
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
        Set<PrimerMatch> remove = new HashSet<>();
        for(int a: f){
//            System.out.println(count);
//            count++;
            int b=r.get(index);
            int frag =b-a;
            while(index<r.size()-1&&b<a){
                index++;
                b=r.get(index);
                frag =b-a;
            }
            while(frag<500&&index<r.size()-1){
                index++;
                b=r.get(index);
                frag = b-a;
            }
            while(frag<=2000&& index<r.size()-1){
                b=r.get(index);
                frag = b-a;
                Long pF = forward.get(a);
                Long pR = reverse.get(b);
                if(Math.abs(primerTm.get(pF)-primerTm.get(pR))<5.0){
                    PrimerMatch match = new PrimerMatch(pF,pR, frag+0.0);
                    if(!primerMatchSet.containsKey(match)){
                        primerMatchSet.put(match,match);
                    }else{
                        remove.add(match);
                    }
                    index++;
                }else{
                    index++;
                }
            }
            index =0;
        }
        for(PrimerMatch m:remove){
            primerMatchSet.remove(m,m);
        }
        if(first){
            return primerMatchSet.keySet();
        }else{
            primerFragSet.forEach(y->{
                if(primerMatchSet.containsKey(y)){
                    y.frags=Arrays.copyOf(y.frags,y.frags.length+1);

                    y.frags[y.frags.length-1]=primerMatchSet.get(y).frags[0];
                }
            });
            return primerFragSet;
        }

    }
    private static class PrimerMatch{
        long foward;
        long reverse;
        double[] frags;
        public PrimerMatch(long f,long r, double frag){
            frags=new double[1];
            foward=f;
            reverse =r;
            frags[0]=frag;
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
            return new EqualsBuilder().
                    append(foward, rhs.foward).
                    append(reverse, rhs.foward).
                    isEquals();
        }


    }
}
