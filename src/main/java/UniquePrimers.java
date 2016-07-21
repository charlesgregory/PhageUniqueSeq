//import com.nfsdb.journal.exceptions.JournalException;
import com.questdb.ex.JournalException;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.biojava.nbio.core.sequence.DNASequence;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by musta_000 on 7/11/2016.
 */
public class UniquePrimers {
    public static void primerDBsetup() throws SQLException, IOException, JournalException {
        NFSDBManager db=new NFSDBManager();
        db.makePhagesTable();
        System.out.println("Building DB");
        FastaManager fastaManager=FastaManager.getInstance();
        Map<List<String>, DNASequence> fastaMap = fastaManager.getMultiFasta();
        for (List<String> x:fastaMap.keySet()){
            db.insertPhage(x.get(2),x.get(1),x.get(0));
        }
        System.out.println("DB Built");
        db.insertPhageCommit();
        db.db.close();
    }
    public static void primerAnalysis( int bps, NFSDBManager db)throws IOException, JournalException {
        long time = System.currentTimeMillis();
        System.out.println((System.currentTimeMillis()-time ) / Math.pow(10, 3)/60);
        time = System.currentTimeMillis();
        FastaManager fastaManager=FastaManager.getInstance();
        Map<List<String>, DNASequence> fastas = fastaManager.getMultiFasta();
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
            Set<String> clust = phages.stream().filter(y -> y[0].equals(x)).map(y -> y[1]).collect(Collectors.toSet());
            Map<String, Integer> clustersNum = new HashMap<>();
            Map<Integer, String> clustersName = new HashMap<>();
            Map<Integer, List<String>> clusters = new HashMap<>();
            Map<Long, UniquePrimers.Primer> primers = new HashMap<>();
            int i = 0;
            for (String cluster : clust) {
                clustersName.put(i, cluster);
                clustersNum.put(cluster, i);
                i++;
            }
            clust.parallelStream().forEach(cluster -> clusters.put(clustersNum.get(cluster),
                    phages.stream()
                            .filter(a -> a[0].equals(x) && a[1].equals(cluster))
                            .map(a -> a[2])
                            .collect(Collectors.toList())));
            for (int z : clusters.keySet()) {

                List<String> clustphages = clusters.get(z);
                for (String phage : clustphages) {

                    Set<Long> phagprimers= new HashSet<>();
                    List<String> id = new ArrayList<>();
                    id.add(x);
                    id.add(clustersName.get(z));
                    id.add(phage);
                    String sequence = fastas.get(id).getSequenceAsString()+
                            fastas.get(id).getReverseComplement().getSequenceAsString();
                    for (int k = 0; k <= sequence.length() - bps; k++) {
                        phagprimers.add(Encoding.twoBitEncoding(sequence.substring(k, k + bps)));
                    }
                    for (long primer : phagprimers) {
                        if (!primers.containsKey(primer)) {
                            primers.put(primer, new UniquePrimers.Primer(z));
                        } else {
                            UniquePrimers.Primer select = primers.get(primer);
                            select.phageCount++;
                            if (!select.containsCluster(z)) {
                                select.addCluster(z);
                            }
                        }

                    }

                }
                System.out.print("\r");
                System.out.print(clustersName.get(z)+"                             ");
            }
            int count = 0;

            Iterator<Map.Entry<Long, UniquePrimers.Primer>> primersSet = primers.entrySet().iterator();
            while (primersSet.hasNext()) {

                Map.Entry<Long, UniquePrimers.Primer> primer = primersSet.next();
                UniquePrimers.Primer primerInf = primer.getValue();
                if (primerInf.clusters.length != 1) {
                    primer.setValue(null);
                } else {
                    int primerClust = -1;
                    for (int cluster : primerInf.clusters) {
                        primerClust = cluster;
                    }
                    if (primerInf.phageCount == clusters.get(primerClust).size()) {
//                        primer.setValue(null);
                        db.insertPrimer(primer.getKey(),clustersName.get(primerClust),x,PrimerDesign.calcHairpin(
                                Encoding.twoBitDecode(primer.getKey()), 4));
                        count++;
                    }
//                    } else {
//                        count++;
//                    }
                }
            }
            System.out.print("\r");
            System.out.println("Strain "+x+" done!");
            System.out.print("Unique Count: ");
            System.out.println(count);
            System.out.print("Primer Count: ");
            System.out.println(primers.size());

//            for (Long a : primers.keySet()) {
//                UniquePrimers.Primer primerInf = primers.get(a);
//                if (primerInf != null) {
//                    String primerClust = "";
//                    for (int cluster : primerInf.clusters) {
//                        primerClust = clustersName.get(cluster);
//                    }
//                    db.insertPrimer(a,primerClust,x,HSqlPrimerDesign.calcHairpin(Encoding.twoBitDecode(a), 4));
//                }
//            }
            System.out.println(bps+" Unique Updated");
            System.out.println((System.currentTimeMillis() - time) / Math.pow(10, 3) / 60);
        }
        db.insertPrimerCommit();
        db.db.close();
    }


    public static void clearDatabase(Connection connection) throws SQLException {
        Connection db = connection;
        Statement stmt = db.createStatement();
        stmt.execute("TRUNCATE SCHEMA Primerdb RESTART IDENTITY AND COMMIT NO CHECK");
    }
    //Key object for unique sequence map
    private static class Bytes{
        byte[] bytes;
        Bytes(byte[] b){
            bytes=b;
        }
        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                    // if deriving: appendSuper(super.hashCode()).
                            append(bytes).
                            toHashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof UniquePrimers.Bytes))
                return false;
            if (obj == this)
                return true;

            UniquePrimers.Bytes rhs = (UniquePrimers.Bytes) obj;

            return Arrays.equals(bytes,rhs.bytes);
        }
    }
    //entry object that stores the data of the key in the unique sequence map
    private static class Primer{
        int[] clusters;
        int phageCount;
        Primer(int cluster){
            clusters = new int[1];
            addCluster(cluster);
            phageCount=1;
        }
        public boolean containsCluster(int cluster){
            for(int i=0;i<clusters.length;i++){
                if(clusters[i]== cluster){
                    return true;
                }
            }
            return false;
        }
        public void addCluster(int cluster){
            int[] temp = new int[phageCount+1];
            for(int i =0;i<clusters.length;i++){
                temp[i]=clusters[i];
            }
            clusters=temp;
            clusters[phageCount++]=cluster;
        }
    }
}

