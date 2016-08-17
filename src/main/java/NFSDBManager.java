//import com.nfsdb.journal.Journal;
//import com.nfsdb.journal.JournalWriter;
//import com.nfsdb.journal.PartitionType;
//import com.nfsdb.journal.exceptions.JournalException;
//import com.nfsdb.journal.factory.JournalFactory;
//import com.nfsdb.journal.factory.configuration.JournalConfigurationBuilder;
//import com.nfsdb.journal.query.api.QueryAllBuilder;
import com.questdb.Journal;
import com.questdb.JournalWriter;
import com.questdb.ex.JournalException;
import com.questdb.factory.JournalFactory;
import com.questdb.factory.configuration.JournalConfigurationBuilder;
import com.questdb.query.UnorderedResultSet;
import com.questdb.query.api.QueryAllBuilder;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;

import java.util.concurrent.TimeUnit;

/**
 * Created by musta_000 on 7/11/2016.
 */
public class NFSDBManager {
    int i;
    int count;
    JournalFactory db;
    JournalWriter<Primers> primerTable;
    JournalWriter<Phages> phagesTable;
    JournalWriter<MatchedPrimers> matchedPrimersTable;
    public static final JournalConfigurationBuilder CONFIG = new JournalConfigurationBuilder() {{
//        $(Phages.class)
////                .keyColumn("Strain")
////                .keyColumn("Cluster")
////                .$sym("Strain").index()
////                .$sym("Cluster").index()
//                .$str("Strain")
//                .$str("Cluster")
//        ;
        $(Primers.class)
                .keyColumn("Cluster")//.txCountHint(100000000).recordCountHint(10000000).openFileTTL(1,TimeUnit.DAYS)
                .$sym("Cluster").index().size(60).noCache()
                //.txCountHint(100000000).recordCountHint(10000000).openFileTTL(1,TimeUnit.DAYS)
//                .$sym("Strain").index().size(60).noCache()
        ;
        $(MatchedPrimers.class)
                .keyColumn("Cluster")//.txCountHint(100000000).recordCountHint(10000000).openFileTTL(1,TimeUnit.DAYS)
//                .keyColumn("Strain")
                .$sym("Cluster").index().size(60).noCache()
                //.txCountHint(100000000).recordCountHint(10000000).openFileTTL(1,TimeUnit.DAYS)
//                .$sym("Strain").index().size(60).noCache()
        ;
    }};
    public NFSDBManager() throws JournalException {
        i=0;
        count=0;
//        db = new JournalFactory("NFSDB");
        db = new JournalFactory(CONFIG.build("NFSDB"));
    }
    public void reconnect() throws JournalException {
        db = new JournalFactory(CONFIG.build("NFSDB"));
        makePrimerTable();
    }
    public void makeDB() throws JournalException {
        makePhagesTable();
        makePrimerTable();
        makeMatchedPrimersTable();
    }

    public void makePrimerTable() throws JournalException {
        primerTable= db.bulkWriter(Primers.class);
    }
    public void makePhagesTable() throws JournalException {
        phagesTable= db.bulkWriter(Phages.class);
    }
    void makeMatchedPrimersTable() throws JournalException {
        matchedPrimersTable= db.bulkWriter(MatchedPrimers.class);
    }
    public void insertPhage(String name,String cluster,String strain) throws JournalException {
        Phages newP =new Phages();
        newP.setName(name);
        newP.setCluster(cluster);
        newP.setStrain(strain);
        phagesTable.append(newP);
    }
    public void insertPhageCommit() throws JournalException {
        phagesTable.commit();
    }
    public void insertPrimer(long seq,String cluster,String strain,boolean hairp) throws JournalException {
        Primers p = new Primers();
        p.setSequence(seq);
        p.setStrain(strain);
        p.setCluster(cluster);
        p.setHairpin(hairp);
//        p.setTimestamp(timestamp);
//        System.out.print("\r");
//        if(count++>450000){
//            System.out.print(p.toString());
//        }
//        System.out.print(p.toString());
//        System.out.print(cluster.getBytes().length);
//        System.out.print(count++);
        primerTable.append(p);
    }
    public void insertPrimerCommit() throws JournalException {
        primerTable.commit();
    }
    public void insertMatchedPrimer(long primer,long rprimer,String cluster,String strain,double[] frags) throws
            JournalException, CompoundNotFoundException {
        MatchedPrimers p = new MatchedPrimers();
        DescriptiveStatistics stats = new DescriptiveStatistics(frags);
        p.setPrimer(primer);
        p.setPrimerMatch(rprimer);
        p.setMean(stats.getMean());
        p.setStandardDev(stats.getStandardDeviation());
        p.setStrain(strain);
        p.setCluster(cluster);
        matchedPrimersTable.append(p);
    }
    public void insertMatchedPrimerCommit() throws JournalException {
        matchedPrimersTable.commit();
    }
    public Journal<Phages> readPhages() throws JournalException {
        return db.bulkReader(Phages.class);
    }
    public UnorderedResultSet<Primers> readPrimers(String cluster) throws JournalException {
        QueryAllBuilder<Primers> primersQueryAllBuilder =
                db.bulkReader(Primers.class).query().all().withKeys("Cluster", cluster);
        return primersQueryAllBuilder.asResultSet();
    }
    public void test() throws JournalException {
        makeDB();
//        QueryParser
        System.out.println(count);
    }
}
