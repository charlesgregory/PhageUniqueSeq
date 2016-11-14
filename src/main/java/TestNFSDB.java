import com.questdb.ex.JournalException;
import com.questdb.query.api.QueryAll;
import com.questdb.query.api.QueryAllBuilder;
import org.biojava.nbio.core.sequence.DNASequence;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by musta_000 on 7/14/2016.
 */
@SuppressWarnings("Duplicates")
public class TestNFSDB {
    public static void main(String[] args) throws JournalException {
        NFSDBManager db = new NFSDBManager();
//        db.makePhagesTable();
//        db.makePrimerTable();
        int count=0;
//        System.out.println(count);
//        db.insertPhage("K","L","M");
//        db.insertPrimer(0L,"k","l",false);
//        System.out.println(++count);
//        for (int i = 0; i < 100000000; i++) {
//            db.insertPrimer(1045728378578L,"V","Mycobacterium",false);
//            count++;
//            if(i%100000==0) {
//                System.out.print("\r");
//                System.out.print(count);
//            }
//        }
//        db.insertPhageCommit();
//        db.insertPrimerCommit();
//        System.out.println();
//        count = 0;
        QueryAllBuilder<MatchedPrimers> primersQueryAllBuilder =
                db.db.bulkReader(MatchedPrimers.class).query().all().withKeys("Cluster","A1");
        QueryAllBuilder<Primers> primers2QueryAllBuilder =
                db.db.bulkReader(Primers.class).query().all().withKeys("Cluster","A1");//.filter("Strain","Mycobacterium");
        for(MatchedPrimers p:primersQueryAllBuilder.asResultSet()){
            assert p!=null;
            if(p.getStrain().equals("Mycobacterium")){
                count++;
//                boolean k=false;
//                for(Primers p2:primers2QueryAllBuilder.asResultSet()){
//                    assert p2!=null;
//                    if(p.getPrimerMatch()==p2.getSequence()){
//                        k=true;
//                    }
//                }
//                if(!k){
//                    System.out.println("Error");
//                }

            }
        }
        System.out.println(count);
        db.db.close();
    }
    public static void testmatches() throws JournalException, IOException {
        NFSDBManager db = new NFSDBManager();
        QueryAllBuilder<Primers> primersQueryAllBuilder =
                db.db.bulkReader(Primers.class).query().all().withKeys("Cluster","A1");
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
        String x="Mycobacterium";
        Set<String> clust = phages.stream().filter(y -> y[0].equals(x)).map(y -> y[1]).collect(Collectors.toSet());
        Map<String, Integer> clustersNum = new HashMap<>();
        Map<Integer, String> clustersName = new HashMap<>();
        Map<Integer, List<String>> clusters = new HashMap<>();
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
            System.out.print("\n");
            System.out.print(clustersName.get(z));
            for (String phage : clustphages) {
                boolean unique=true;
                boolean contains=false;

                Set<Long> phagprimers= new HashSet<>();
                List<String> id = new ArrayList<>();
                id.add(x);
                id.add(clustersName.get(z));
                id.add(phage);
                String sequence = fastas.get(id).getSequenceAsString()+
                        fastas.get(id).getReverseComplement().getSequenceAsString();
                for (int bps =18;bps <=25;bps++) {
                    for (int k = 0; k <= sequence.length() - bps; k++) {
                        phagprimers.add(Encoding.twoBitEncoding(sequence.substring(k, k + bps)));
                    }
                }

                for (Primers p:primersQueryAllBuilder.asResultSet()){
                    if(!phagprimers.contains(p.getSequence())){
                        unique=false;
                        break;
                    }
                }
                for (Primers p:primersQueryAllBuilder.asResultSet()){
                    if(phagprimers.contains(p.getSequence())){
                        contains=true;
                    }
                }
                if(!(unique ==contains)) {
                    System.out.println(phage + " " + unique + " " + contains);
                }
            }

        }
        db.db.close();
    }
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
