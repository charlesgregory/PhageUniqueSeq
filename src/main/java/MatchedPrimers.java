import java.util.Arrays;

/**
 * Created by musta_000 on 7/11/2016.
 */
public class MatchedPrimers {
//    init.executeUpdate(" CREATE CACHED TABLE MatchedPrimers( " +
//            " id INTEGER GENERATED BY DEFAULT AS IDENTITY, " +
//            " Primer BIGINT NOT NULL, " +
//            " PrimerMatch BIGINT NOT NULL, " +
//            " Comp FLOAT NULL, " +
//            " FragAVG FLOAT NULL, " +
//            " FragVAR FLOAT NULL, " +
//            " H2SD FLOAT NULL, " +
//            " L2SD FLOAT NULL, " +
//            " Strain VARCHAR(45) NOT NULL, " +
//            " Cluster VARCHAR(45) NOT NULL, " +
//            " PRIMARY KEY (id)) ");
    private long Primer;
    private long PrimerMatch;
    private double[] frags;
    private String Strain;
    private String Cluster;

    public long getPrimer() {
        return Primer;
    }

    public void setPrimer(long primer) {
        Primer = primer;
    }

    public long getPrimerMatch() {
        return PrimerMatch;
    }

    public void setPrimerMatch(long primerMatch) {
        PrimerMatch = primerMatch;
    }

    public double[] getFrags() {
        return frags;
    }

    public void setFrags(double[] frags) {
        this.frags = frags;
    }

    public String getStrain() {
        return Strain;
    }

    public void setStrain(String strain) {
        Strain = strain;
    }

    public String getCluster() {
        return Cluster;
    }

    public void setCluster(String cluster) {
        Cluster = cluster;
    }

    @Override
    public String toString() {
        return "MatchedPrimers{" +
                "Primer=" + Primer +
                ", PrimerMatch=" + PrimerMatch +
                ", frags=" + Arrays.toString(frags) +
                ", Strain='" + Strain + '\'' +
                ", Cluster='" + Cluster + '\'' +
                '}';
    }

    public void clear(){
        Primer=0L;
        PrimerMatch=0L;
        Strain="";
        Cluster="";
    }
}
