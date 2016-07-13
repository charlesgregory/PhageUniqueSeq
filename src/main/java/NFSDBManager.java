import com.nfsdb.journal.Journal;
import com.nfsdb.journal.JournalWriter;
import com.nfsdb.journal.exceptions.JournalException;
import com.nfsdb.journal.factory.JournalFactory;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;

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
    public NFSDBManager() throws JournalException {
        i=0;
        count=0;
        db = new JournalFactory("NFSDB2");
    }
    public void makeDB() throws JournalException {
        makePhagesTable();
        makePrimerTable();
        makeMatchedPrimersTable();
    }

    public void makePrimerTable() throws JournalException {
        primerTable= db.bulkWriter(Primers.class);
    }
    private void makePhagesTable() throws JournalException {
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
        phagesTable.commit();
    }
    public void insertPrimer(long seq,String cluster,String strain,boolean hairp) throws JournalException {
        Primers p = new Primers();
        p.setSequence(seq);
        p.setStrain(strain);
        p.setCluster(cluster);
        p.setHairpin(hairp);
        primerTable.append(p);
        primerTable.commit();
    }
    public void insertMatchedPrimer(long primer,long rprimer,String cluster,String strain,double[] frags) throws
            JournalException, CompoundNotFoundException {
        MatchedPrimers p = new MatchedPrimers();
        DescriptiveStatistics stats = new DescriptiveStatistics(frags);
        p.setPrimer(primer);
        p.setPrimerMatch(rprimer);
        p.setFrags(frags);
        p.setStrain(strain);
        p.setCluster(cluster);
        matchedPrimersTable.append(p);
        matchedPrimersTable.commit();
    }
    public Journal<Phages> readPhages() throws JournalException {
        return db.bulkReader(Phages.class);
    }
    public Journal<Primers> readPrimers() throws JournalException {
        return db.bulkReader(Primers.class);
    }
}
