import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Thomas on 7/4/2016.
 */
public class DBManager {
    int i;
    int count;
    Connection db;
    private PreparedStatement matchPrimerInsert;
    public DBManager(Connection conn) throws SQLException {
        db = conn;
        matchPrimerInsert = db.prepareStatement("INSERT INTO MatchedPrimers(" +
                "Primer, PrimerMatch, Comp,FragAVG,FragVAR,H2SD,L2SD, Cluster, Strain)" +
                "Values(?,?,?,?,?,?,?,?,?)");
        i=0;
        count=0;
    }
    public void matchedPrimerSubmit(long primer1, long primer2, double[] frags, String cluster, String strain)
            throws SQLException, CompoundNotFoundException {
        DescriptiveStatistics stats = new DescriptiveStatistics(frags);
        matchPrimerInsert.setLong(1, primer1);
        matchPrimerInsert.setLong(2, primer2);
        matchPrimerInsert.setDouble(3, HSqlPrimerDesign.align(
                Encoding.twoBitDecode(primer1),
                Encoding.twoBitDecode(primer2)));
        matchPrimerInsert.setDouble(4, stats.getMean());
        matchPrimerInsert.setDouble(5, stats.getVariance());
        matchPrimerInsert.setDouble(6, stats.getMean() + 2 * stats.getStandardDeviation());
        matchPrimerInsert.setDouble(7, stats.getMean() - 2 * stats.getStandardDeviation());
        matchPrimerInsert.setString(8, cluster);
        matchPrimerInsert.setString(9, strain);
        matchPrimerInsert.addBatch();
        i++;
        count++;
        if (i == 10000) {
            i = 0;
            matchPrimerInsert.executeBatch();
            db.commit();
            System.out.print("\r");
            System.out.print(count);
        }
    }
    public void matchPrimerInsertFinal() throws SQLException {
        if(i>0){
            i = 0;
            matchPrimerInsert.executeBatch();
            db.commit();
        }
        System.out.print("\r");
        System.out.println(count);
        count=0;
    }
}
