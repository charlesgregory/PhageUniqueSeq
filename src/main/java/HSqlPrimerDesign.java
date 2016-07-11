import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.alignment.SimpleGapPenalty;
import org.biojava.nbio.core.alignment.matrices.SubstitutionMatrixHelper;
import org.biojava.nbio.core.alignment.template.SequencePair;
import org.biojava.nbio.core.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Copyright (C) 2016  Thomas Gregory

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

 * Created by Thomas on 3/12/2016.
 * Used for Ideal Primer determination
 * The calcHairpin, getIndexOf, DoHairpinArrayInsert, and make complement methods were
 * converted from javascript into java from the source code of the web program
 * OligoCalc.
 *
 * The primerTm method was converted to java from C source code from the
 * standalone Primer3 source code.
 *
 * Kibbe WA. 'OligoCalc: an online oligonucleotide properties calculator'.
 * (2007) Nucleic Acids Res. 35(webserver issue): May 25
 *
 * Untergasser A, Cutcutache I, Koressaar T, Ye J, Faircloth BC, Remm M, Rozen SG (2012)
 * Primer3 - new capabilities and interfaces. Nucleic Acids Research 40(15):e115 Koressaar T,
 * Remm M (2007) Enhancements and modifications of primer design program
 * Primer3 Bioinformatics 23(10):1289-91
 */
@SuppressWarnings("Duplicates")
public class HSqlPrimerDesign {
    static final String JDBC_DRIVER_HSQL = "org.hsqldb.jdbc.JDBCDriver";
    static final String DB_SERVER_URL ="jdbc:hsqldb:hsql://localhost/primerdb";
    static final String DB_URL_HSQL_C = "jdbc:hsqldb:file:database/primerdb;ifexists=true";
    public static Connection conn;
    private static final String USER = "SA";
    private static final String PASS = "";
    //a main for testing
    public static void main(String[] args) throws NoSuchFieldException,
            IllegalAccessException, ClassNotFoundException, InstantiationException, SQLException, FileNotFoundException {
        Class.forName(JDBC_DRIVER_HSQL).newInstance();
        conn = DriverManager.getConnection(DB_SERVER_URL,USER,PASS);
        PrintWriter log = new PrintWriter(new File("javalog.log"));
        Statement stat = conn.createStatement();
        ResultSet call = stat.executeQuery("Select * From " +
                "primers where Cluster ='A1' and UniqueP =True and Bp = 20");
        Set<CharSequence> primers = new HashSet<>();
        while (call.next()) {
            primers.add(call.getString("Sequence"));
        }
    }




    //gets an index in a primer for the calcHairpin
    private static int[] getIndexOf(String seq, String subSeq, int startIndex, int minMatch)
    {
// look for subSeq in seq
/* returns an array where
	theResult[0] is the index of the first match of subseq that is of at least length minMatch in seq
	theResult[1] is the length of the match
*/
        int[] theResult= new int[2];
        theResult[0]=-1;
        theResult[1]=-1;
        for(int k=minMatch; k<=subSeq.length(); k++) {
            // can replace this with seq.search for GREP capabilities
            int theMatch = seq.indexOf(subSeq.substring(0, k), startIndex);
            if (theMatch < 0) {
                break;
            } else {
                theResult[0] = theMatch;
                theResult[1] = k;
            }
        }

        return theResult;
    }
    //Makes an array insert for the calcHairpin array
    private static int[][] DoHairpinArrayInsert(int a, int b, int c, int d, int[][] results)
    {
        int arrayCount=results.length;
        int[][] temp = new int[arrayCount+1][4];
        for (int i = 0; i < results.length; i++) {
            for (int j = 0; j < results[i].length; j++) {
                temp[i][j]=results[i][j];
            }
        }
        results=temp;
        if (a >= c || a >= b || c >= d || b>=c) {
            return results;
        }
        for (int i=0;i<arrayCount;i++) {
            if (results[i][0]<=a && results[i][1]>=b && results[i][2]<=c && results[i][3]>=d)
                return results;
            if (results[i][0]>=a && results[i][1]<=b && results[i][2]>=c && results[i][3]<=d) {
                results[i][0]=a;
                results[i][1]=b;
                results[i][2]=c;
                results[i][3]=d;
                return results;
            }
        }
        results[arrayCount][0]=a;
        results[arrayCount][1]=b;
        results[arrayCount][2]=c;
        results[arrayCount][3]=d;
        return results;
    }
    //Makes a primer complement
    public static String makeComplement(String seq){
        String newseq ="";
        for (int i = seq.length()-1; i >= 0; i--) {
            if(seq.charAt(i) == 'G' ||seq.charAt(i)=='g'){
                newseq=newseq+"C";
            }
            else if(seq.charAt(i) == 'T' ||seq.charAt(i)=='t'){
                newseq=newseq+"A";
            }
            else if(seq.charAt(i) == 'C' ||seq.charAt(i)=='c'){
                newseq=newseq+"G";
            }
            else if(seq.charAt(i) == 'A' ||seq.charAt(i)=='a'){
                newseq=newseq+"T";
            }
        }
        return newseq;
    }
    //Discovers the presence of primer dimerization
    public static boolean calcHairpin(String theFullSequence, int minHairpinLength) {
/*  compare theCompSeq with theFullSeq starting at theFullSeq[startPos]. Successful matches must be at least minMatch long */
/* The resulting array is an array of arrays. each result should be an array of 4 integers
	result[0]: position of start of match in sequence
	result[1]: position of end of match
	result[2]: position of the start of the complement (really the end since it would be 3'-5')
	result[3]: position of the end of the complement (really the start since it would be 3'-5')
*/
        int bubbleSize = 3;
        String theFullComplement=makeComplement(theFullSequence);
        int[][] theResults = new int[1][4];
        int[] theResult;
        int compPos;
        int seqPos;
        int maxSeqLength=Math.abs(theFullSequence.length()/2)-bubbleSize; // makes sure that we do not anneal the full length of the primer - that should come out in the dimerization report
        int maxMatch=0;
        for (compPos=0; compPos < theFullComplement.length()-2*minHairpinLength; compPos++) {
            maxMatch=0;
            for (seqPos=0; seqPos<theFullSequence.length()-maxSeqLength; seqPos++) {
                theResult=getIndexOf(theFullSequence.substring(0,seqPos+maxSeqLength),
                        theFullComplement.substring(compPos,theFullComplement.length()),
                        seqPos, minHairpinLength);
                if (theResult[0] > -1) {
                    // theResult[0] is the index of the first match of theFullComplement that is of at least length minHairpinLength in theFullSequence
                    // theResult[1] is the length of the match

                    theResults=DoHairpinArrayInsert(theResult[0],theResult[0]+theResult[1]-1,
                            theFullSequence.length()-compPos-theResult[1],theFullSequence.length()-compPos-1
                            ,theResults);
                    if (theResult[1] > maxMatch) maxMatch=theResult[1];
                    seqPos=theResult[0]+theResult[1]-minHairpinLength;  // move forward to guarantee nothing else is found that is a reasonable match
                    if (seqPos+minHairpinLength>=maxSeqLength) {
                        compPos+=maxMatch-minHairpinLength; // move compPos forward to stop identical checks if long match was found!
                        break; // we have moved far enough on the primer to guarentee we have everything  -further would give us the reverse match
                    }
                } else {
                    if (maxMatch > minHairpinLength) compPos+=maxMatch-minHairpinLength; // move compPos forward to stop identical checks if long match was found!
                    break;  //not found in the rest of the sequence!
                }
            }
        }
        if(theResults.length<=1){
            return false;
        }else{
            return true;
        }
    }
    public static double calcDimer(String theFullSequence, String seq2, int minHairpinLength) {
/*  compare theCompSeq with theFullSeq starting at theFullSeq[startPos]. Successful matches must be at least minMatch long */
/* The resulting array is an array of arrays. each result should be an array of 4 integers
	result[0]: position of start of match in sequence
	result[1]: position of end of match
	result[2]: position of the start of the complement (really the end since it would be 3'-5')
	result[3]: position of the end of the complement (really the start since it would be 3'-5')
*/
        int[][] theResults = new int[1][4];
        int[] theResult;
        int compPos;
        int seqPos;
//        seq2 = makeComplement(seq2);
        int maxSeqLength=Math.abs(theFullSequence.length()/2); // makes sure that we do not anneal the full length of the primer - that should come out in the dimerization report
        int maxMatch=0;
//        double sum =0.0;
        for (int pos=0; pos < seq2.length()-minHairpinLength; pos++) {
            int len=pos+minHairpinLength;
            int beg =theFullSequence.indexOf(seq2.substring(pos,len));
            if(beg!=-1){
                if(minHairpinLength>maxMatch)maxMatch=minHairpinLength;
                while(beg!=-1&&len<seq2.length()){
                    if(len-pos >maxMatch)maxMatch=len-pos;
                    beg=theFullSequence.indexOf(seq2.substring(pos,++len));
                }
//                System.out.println(seq2.substring(pos, len - 1));
//                sum += (len - pos - 1.0) / theFullSequence.length();
            }
        }
        return maxMatch/(1.0*theFullSequence.length());
    }
    //finds gc content of a nucleotide
    public static double gcContent(CharSequence primer){
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
    @SuppressWarnings("Duplicates")
    //calculates melting temperature of a oligonucleotide
    //based off Primer3 code source code
    public static double primerTm(CharSequence primer, double salt_conc, double dna_conc,
                                  double divalent, double dntp){
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
                if ((primer.charAt(i)=='A' && primer.charAt(primer.length()-i-1)!='T')
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
    public static double easytm(String primer){
        int a =0;
        int c =0;
        int g = 0;
        int t = 0;
        char[] chars =primer.toCharArray();
        for(char x:chars){
            if(x=='A'||x=='a'){
                a++;
            }else if (x=='G'||x=='g'){
                g++;
            }else if (x=='C'||x=='c'){
                c++;
            }else if (x=='T'||x=='t'){
                t++;
            }
        }
        return 64.9 +41*(g+c-16.4)/(a+t+g+c);
    }
    public static double align(String seq1, String seq2) throws CompoundNotFoundException {
        SubstitutionMatrix<NucleotideCompound> matrix = SubstitutionMatrixHelper.getNuc4_4();
        SimpleGapPenalty gap = new SimpleGapPenalty();
        SequencePair<DNASequence,NucleotideCompound> align= Alignments.getPairwiseAlignment(
                new DNASequence(seq1),new DNASequence(seq2), Alignments.PairwiseSequenceAlignerType.LOCAL,
                gap,matrix);
        int gapp =0;
        for(char c:align.getQuery().toString().toCharArray()){
            if(c=='-'){
                gapp++;
            }
        }
        for(char c:align.getTarget().toString().toCharArray()){
            if(c=='-'){
                gapp++;
            }
        }
        int nonmatch = align.getQuery().toString().length()-gapp-align.getNumIdenticals();
        double score = (align.getNumIdenticals())+((-1.0)*nonmatch)+((-2.0*gapp));
        if(score<0.0)
            score=0.0;

        return score;
    }
    @SuppressWarnings("Duplicates")
    public static void locations(Connection connection) throws ClassNotFoundException,
            SQLException, InstantiationException, IllegalAccessException, IOException, CompoundNotFoundException {
        long time = System.nanoTime();
        String base = new File("").getAbsolutePath();
        Map<List<String>, DNASequence> fastas = FastaManager.getMultiFasta();
        Connection db = connection;
        Statement stat = db.createStatement();
        PrintWriter log = new PrintWriter(new File("javalog.log"));
        stat.execute("SET AUTOCOMMIT FALSE;");
        stat.execute("SET LOG 0;");
        DBManager insert = new DBManager(connection);
//        PreparedStatement st = db.prepareStatement("INSERT INTO MatchedPrimers(" +
//                "Primer, PrimerMatch, Comp,FragAVG,FragVAR,H2SD,L2SD, Cluster, Strain)" +
//                "Values(?,?,?,?,?,?,?,?,?)");
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
        Set<String>strains = phages.stream().map(y->y[0]).collect(Collectors.toSet());
        for(String x:strains) {
            Set<String> clust = phages.stream().filter(y -> y[0].equals(x)).map(y -> y[1]).collect(Collectors.toSet());
            String[] clusters = clust.toArray(new String[clust.size()]);
//        String z ="A1";
            for (String z : clusters) {
                System.out.println("Starting:" + z);
                Map<Long,Double>primerTm = new HashMap<>();
                Set<Long> primers = new HashSet<>();
//                Set<Matches> matched = new HashSet<>();
                Set<String> clustphage = phages.stream()
                        .filter(a -> a[0].equals(x) && a[1].equals(z)).map(a -> a[2])
                        .collect(Collectors.toSet());
                String[] clustphages = clustphage.toArray(new String[clustphage.size()]);
                if (clustphages.length > 1) {
                    if(clustphages.length>10) {
                        fullPrimerMatching(stat, x, z, primerTm, primers, clustphage,
                                fastas, clustphages, insert, time);
                    }
                    else{
                        shortPrimerMatching(stat, x, z, primerTm, primers, clustphage,
                                fastas, clustphages, insert, time);
                    }
                    insert.matchPrimerInsertFinal();
                }
                log.println(z);
                log.flush();
                System.gc();
            }
        }

        stat.execute("SET AUTOCOMMIT TRUE;");
        stat.execute("SET LOG 1;");
//        st.close();
        stat.close();
        System.out.println("Matches Submitted");
    }
    public static void shortPrimerMatching(Statement stat,String x, String z,
                                          Map<Long,Double> primerTm,Set<Long> primers,
                                          Set<String> clustphage,
                                          Map<List<String>, DNASequence>fastas,
                                          String[]clustphages,
                                          DBManager insert,
                                          long time){
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
                primers.add(primer);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred at " + x + " " + z);
        }
        System.out.println(primers.size());
//                    Set<Long> primerlist2 = primers.stream().collect(Collectors.toSet());
//                    Long[] primers2 = primerlist2.toArray(new Long[primerlist2.size()]);
        Long[] primers2 = primers.toArray(new Long[primers.size()]);
        Map<String, Map<Long, Primer>> locations = Collections.synchronizedMap(
                new HashMap<>());
        clustphage.stream().forEach(phage -> {
            List<String>id = new ArrayList<>();
            id.add(x);
            id.add(z);
            id.add(phage);
            String sequence = fastas.get(id).getSequenceAsString();
            Map<Long, int[]> seqInd = new HashMap<>();
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
            Map<Long, Primer> alllocs = new HashMap<>();
            for (Long primer : primers2) {
//                            List<Integer> locs = new ArrayList<>();
                int[] locs = new int[0];
                String sequence1 = Encoding.twoBitDecode(primer);
                long frag = Encoding.twoBitEncoding(sequence1.substring(0, 10));
                int[] integers = seqInd.get(frag);
                if (integers != null) {
                    for (int i : integers) {
                        if ((sequence1.length() + i) < sequence.length() &&
                                sequence.substring(i, sequence1.length() + i).equals(sequence1)) {
//                                        locs.add(i);
                            int[] temp = new int[locs.length+1];
                            System.arraycopy(locs, 0, temp, 0, locs.length);
                            temp[locs.length]=i;
                            locs=temp;
                        }
                    }
                }
                int[] locs2 = new int[0];
                String sequence2 = Encoding.twoBitDecode(Encoding.reEncodeReverseComplementTwoBit(primer));
                long frag2 = Encoding.twoBitEncoding(sequence2.substring(0, 10));
                int[] integersr = seqInd.get(frag2);
                if (integersr != null) {
                    for (int i : integersr) {
                        if ((sequence2.length() + i) < sequence.length() &&
                                sequence.substring(i, sequence2.length() + i).equals(sequence2)) {
//                                        locs.add(i);
                            int[] temp = new int[locs2.length+1];
                            System.arraycopy(locs2, 0, temp, 0, locs2.length);
                            temp[locs2.length]=i;
                            locs2=temp;
                        }
                    }
                }
                alllocs.put(Encoding.twoBitEncoding(sequence1), new Primer(locs,locs2));
            }
            locations.put(phage, alllocs);
        });
        System.out.println("locations found");
        System.out.println((System.nanoTime() - time) / Math.pow(10, 9) / 60.0);
        final int[] k = new int[]{0};
        for(int i=0; i < primers2.length; i++){
            int c = 0;
            int i3 = 0;
            int matches = 0;
            long a = primers2[i];
//                        int i = 0;
//                        while (!Objects.equals(primers2[i], a)) {
//                            i++;
//                        }
            for (int j = i + 1; j < primers2.length; j++) {
                double[] frags = new double[clustphages.length];
                int phageCounter = 0;
                long b = primers2[j];
                boolean match = true;
                if (matches > 0) {
                    break;
                }
                if (Math.abs(primerTm.get(a) - primerTm.get(b))
                        > 5.0) {
                    continue;
                }
                for (String phage : clustphages) {
                    int[] loc1f = locations.get(phage).get(
                            a).foward;
//                    int[] loc1r = locations.get(phage).get(
//                            a).reverse;
//                    int[] loc2f = locations.get(phage).get(
//                            b).foward;
                    int[] loc2r = locations.get(phage).get(
                            b).reverse;
                    if (loc1f.length == 0 || loc2r.length == 0) {
                        match = false;
                        break;
                    }
                    boolean found = false;
                    int fragCount =0;
                    int l1 = loc1f[0];
                    int l2 = loc2r[0];
                    int count1 = 0;
                    int count2 = 0;
                    int frag = l2-l1;
                    while (!found) {
                        if (frag < 500) {
                            count2++;
                        } else if (frag > 2000) {
                            count1++;
                        }else{
                            fragCount++;
                            if(count1+1< loc1f.length)
                                count1++;
                            else
                                count2++;
                        }
                        if (count1 < loc1f.length &&
                                count2 < loc2r.length) {
                            l1 = loc1f[count1];
                            l2 = loc2r[count2];
                            frag = l2-l1;
                        } else {
                            if(fragCount==1){
                                found=true;
                                frags[phageCounter++] = frag + 0.0;
                            }
                            else{
                                break;
                            }
                        }
                    }
                    if (!found) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    matches++;
//                                matched.add(new Matches(a, b, frags));
                    try {
                        insert.matchedPrimerSubmit(a,Encoding.reEncodeReverseComplementTwoBit(b),frags,z,x);
                    } catch (SQLException | CompoundNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                for (String phage : clustphages) {
//                    int[] loc1f = locations.get(phage).get(
//                            a).foward;
//                    loc1r really
                    int[] loc1f = locations.get(phage).get(
                            a).reverse;
//                    loc2f really
                    int[] loc2r = locations.get(phage).get(
                            b).foward;
//                    int[] loc2r = locations.get(phage).get(
//                            b).foward;
                    if (loc1f.length == 0 || loc2r.length == 0) {
                        match = false;
                        break;
                    }
                    boolean found = false;
                    int fragCount =0;
                    int l1 = loc1f[0];
                    int l2 = loc2r[0];
                    int count1 = 0;
                    int count2 = 0;
                    int frag = l2-l1;
                    while (!found) {
                        if (frag < 500) {
                            count2++;
                        } else if (frag > 2000) {
                            count1++;
                        }else{
                            fragCount++;
                            if(count1+1< loc1f.length)
                                count1++;
                            else
                                count2++;
                        }
                        if (count1 < loc1f.length &&
                                count2 < loc2r.length) {
                            l1 = loc1f[count1];
                            l2 = loc2r[count2];
                            frag = l2-l1;
                        } else {
                            if(fragCount==1){
                                found=true;
                                frags[phageCounter++] = frag + 0.0;
                            }
                            else{
                                break;
                            }
                        }
                    }
                    if (!found) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    matches++;
//                                matched.add(new Matches(a, b, frags));
                    try {
                        insert.matchedPrimerSubmit(b,Encoding.reEncodeReverseComplementTwoBit(a),frags,z,x);
                    } catch (SQLException | CompoundNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }

//                    k[0]++;
//                    System.out.println(k[0]);
//            System.out.println(i);
//            System.out.println(insert.count);
        }
        System.out.println((System.nanoTime() - time) / Math.pow(10, 9) / 60.0);
        System.out.println("Primers matched");
    }
    public static void fullPrimerMatching(Statement stat,String x, String z,
                                          Map<Long,Double> primerTm,Set<Long> primers,
                                          Set<String> clustphage,
                                          Map<List<String>, DNASequence>fastas,
                                          String[]clustphages,
                                          DBManager insert,
                                          long time){
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
                primers.add(primer);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred at " + x + " " + z);
        }
        System.out.println(primers.size());
//                    Set<Long> primerlist2 = primers.stream().collect(Collectors.toSet());
//                    Long[] primers2 = primerlist2.toArray(new Long[primerlist2.size()]);
        Long[] primers2 = primers.toArray(new Long[primers.size()]);
        Map<String, Map<Long, Primer>> locations = Collections.synchronizedMap(
                new HashMap<>());
        clustphage.stream().forEach(phage -> {
            List<String>id = new ArrayList<>();
            id.add(x);
            id.add(z);
            id.add(phage);
            String sequence = fastas.get(id).getSequenceAsString();
            Map<Long, int[]> seqInd = new HashMap<>();
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
            Map<Long, Primer> alllocs = new HashMap<>();
            for (Long primer : primers2) {
//                            List<Integer> locs = new ArrayList<>();
                int[] locs = new int[0];
                String sequence1 = Encoding.twoBitDecode(primer);
                long frag = Encoding.twoBitEncoding(sequence1.substring(0, 10));
                int[] integers = seqInd.get(frag);
                if (integers != null) {
                    for (int i : integers) {
                        if ((sequence1.length() + i) < sequence.length() &&
                                sequence.substring(i, sequence1.length() + i).equals(sequence1)) {
//                                        locs.add(i);
                            int[] temp = new int[locs.length+1];
                            System.arraycopy(locs, 0, temp, 0, locs.length);
                            temp[locs.length]=i;
                            locs=temp;
                        }
                    }
                }
                int[] locs2 = new int[0];
                String sequence2 = Encoding.twoBitDecode(Encoding.reEncodeReverseComplementTwoBit(primer));
                long frag2 = Encoding.twoBitEncoding(sequence2.substring(0, 10));
                int[] integersr = seqInd.get(frag2);
                if (integersr != null) {
                    for (int i : integersr) {
                        if ((sequence2.length() + i) < sequence.length() &&
                                sequence.substring(i, sequence2.length() + i).equals(sequence2)) {
//                                        locs.add(i);
                            int[] temp = new int[locs2.length+1];
                            System.arraycopy(locs2, 0, temp, 0, locs2.length);
                            temp[locs2.length]=i;
                            locs2=temp;
                        }
                    }
                }
                alllocs.put(Encoding.twoBitEncoding(sequence1), new Primer(locs,locs2));
            }
            locations.put(phage, alllocs);
        });
        System.out.println("locations found");
        System.out.println((System.nanoTime() - time) / Math.pow(10, 9) / 60.0);
        final int[] k = new int[]{0};
        for(int i=0; i < primers2.length; i++){
            int c = 0;
            int i3 = 0;
            int matches = 0;
            long a = primers2[i];
//                        int i = 0;
//                        while (!Objects.equals(primers2[i], a)) {
//                            i++;
//                        }
            for (int j = i + 1; j < primers2.length; j++) {
                double[] frags = new double[clustphages.length];
                int phageCounter = 0;
                long b = primers2[j];
                boolean match = true;
//                            if (matches > 0) {
//                                break;
//                            }
                if (Math.abs(primerTm.get(a) - primerTm.get(b))
                        > 5.0) {
                    continue;
                }
                for (String phage : clustphages) {
                    int[] loc1f = locations.get(phage).get(
                            a).foward;
//                    int[] loc1r = locations.get(phage).get(
//                            a).reverse;
//                    int[] loc2f = locations.get(phage).get(
//                            b).foward;
                    int[] loc2r = locations.get(phage).get(
                            b).reverse;
                    if (loc1f.length == 0 || loc2r.length == 0) {
                        match = false;
                        break;
                    }
                    boolean found = false;
                    int fragCount = 0;
                    int l1 = loc1f[0];
                    int l2 = loc2r[0];
                    int count1 = 0;
                    int count2 = 0;
                    int frag = l2 - l1;
                    while (!found) {
                        if (frag < 500) {
                            count2++;
                        } else if (frag > 2000) {
                            count1++;
                        } else {
                            fragCount++;
                            if (count1 + 1 < loc1f.length)
                                count1++;
                            else
                                count2++;
                        }
                        if (count1 < loc1f.length &&
                                count2 < loc2r.length) {
                            l1 = loc1f[count1];
                            l2 = loc2r[count2];
                            frag = l2 - l1;
                        } else {
                            if (fragCount == 1) {
                                found = true;
                                frags[phageCounter++] = frag + 0.0;
                            } else {
                                break;
                            }
                        }
                    }
                    if (!found) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    matches++;
//                                matched.add(new Matches(a, b, frags));
                    try {
                        insert.matchedPrimerSubmit(a, Encoding.reEncodeReverseComplementTwoBit(b), frags, z, x);
                    } catch (SQLException | CompoundNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                for (String phage : clustphages) {
//                    int[] loc1f = locations.get(phage).get(
//                            a).foward;
//                    loc1r really
                    int[] loc1f = locations.get(phage).get(
                            a).reverse;
//                    loc2f really
                    int[] loc2r = locations.get(phage).get(
                            b).foward;
//                    int[] loc2r = locations.get(phage).get(
//                            b).foward;
                    if (loc1f.length == 0 || loc2r.length == 0) {
                        match = false;
                        break;
                    }
                    boolean found = false;
                    int fragCount = 0;
                    int l1 = loc1f[0];
                    int l2 = loc2r[0];
                    int count1 = 0;
                    int count2 = 0;
                    int frag = l2 - l1;
                    while (!found) {
                        if (frag < 500) {
                            count2++;
                        } else if (frag > 2000) {
                            count1++;
                        } else {
                            fragCount++;
                            if (count1 + 1 < loc1f.length)
                                count1++;
                            else
                                count2++;
                        }
                        if (count1 < loc1f.length &&
                                count2 < loc2r.length) {
                            l1 = loc1f[count1];
                            l2 = loc2r[count2];
                            frag = l2 - l1;
                        } else {
                            if (fragCount == 1) {
                                found = true;
                                frags[phageCounter++] = frag + 0.0;
                            } else {
                                break;
                            }
                        }
                    }
                    if (!found) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    matches++;
//                                matched.add(new Matches(a, b, frags));
                    try {
                        insert.matchedPrimerSubmit(b, Encoding.reEncodeReverseComplementTwoBit(a), frags, z, x);
                    } catch (SQLException | CompoundNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
//                    k[0]++;
//                    System.out.println(k[0]);
//            System.out.println(i);
//            System.out.println(insert.count);
        }
        System.out.println((System.nanoTime() - time) / Math.pow(10, 9) / 60.0);
        System.out.println("Primers matched");
    }

    private static class Primer{
        int[] foward;
        int[] reverse;
        public Primer(int[] f, int[] r){
            foward=f;
            reverse=r;
        }
    }
//    private static class Matches{
////        Primer one;
////        Primer two;
//        long one;
//        long two;
//        DescriptiveStatistics stats;
//        public Matches(long primer1, long primer2, double[] arr){
//            one=primer1;
//            two=primer2;
//            stats=new DescriptiveStatistics(arr);
//        }
//
//    }
}

