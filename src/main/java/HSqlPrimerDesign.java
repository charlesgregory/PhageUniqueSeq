import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.biojava.nbio.core.sequence.DNASequence;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
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
public class HSqlPrimerDesign {
    static DpalLoad.Dpal Dpal_Inst;
    static final String JDBC_DRIVER_HSQL = "org.hsqldb.jdbc.JDBCDriver";
    static final String DB_SERVER_URL ="jdbc:hsqldb:hsql://localhost/primerdbTest";
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
        DpalLoad.main(args);
        Dpal_Inst = DpalLoad.INSTANCE_WIN64;
//        int[][] arr =calcHairpin("GGGGGGCCCCCCCCCCCCGGGGGGG",4);
//        if(arr.length<=1){
//            System.out.println("No Hairpin's found");
//        }else{
//            System.out.println("Hairpin(s) found");
//        }
        Statement stat = conn.createStatement();
        ResultSet call = stat.executeQuery("Select * From " +
                "Primerdb.primers where Cluster ='A1' and UniqueP =True and Bp = 20");
        Set<CharSequence> primers = new HashSet<>();
        while (call.next()) {
            primers.add(call.getString("Sequence"));
        }
//        primers.stream().forEach(x->{
//            log.println(x);
//            log.println(complementarity(x, x, Dpal_Inst));
//            int[][] arr =calcHairpin((String)x,4);
//            if(arr.length<=1){
//                log.println("No Hairpin's found");
//            }else{
//                log.println("Hairpin(s) found");
//            }
//            log.println();
//            log.flush();
//        });
    }

    @SuppressWarnings("Duplicates")
    //uses a library compiled from primer3 source code
    public static double complementarity(CharSequence primer1, CharSequence primer2,
                                         DpalLoad.Dpal INSTANCE){
        DpalLoad.Dpal.dpal_args args1 = new DpalLoad.Dpal.dpal_args();
        DpalLoad.Dpal.dpal_results out1 = new DpalLoad.Dpal.dpal_results();
        INSTANCE.set_dpal_args(args1);
        INSTANCE.dpal(primer1.toString().getBytes(),
                primer2.toString().getBytes(),args1,out1);
        double o1 = out1.score / 100;
        DpalLoad.Dpal.dpal_args args2 = new DpalLoad.Dpal.dpal_args();
        DpalLoad.Dpal.dpal_results out2 = new DpalLoad.Dpal.dpal_results();
        INSTANCE.set_dpal_args(args2);
        INSTANCE.dpal(primer1.toString().getBytes(),
                new StringBuilder(makeComplement(primer2.toString()))
                        .reverse().toString().getBytes(),args2,out2);
        double o2 = out2.score / 100;
        return Math.max(o1,o2);
    }

    @SuppressWarnings("Duplicates")
    @Deprecated
    public static int hairpin(CharSequence primer1, CharSequence primer2, ThalLoad.Thal INSTANCE){
        ThalLoad.Thal.thal_args args = new ThalLoad.Thal.thal_args();
        ThalLoad.Thal.thal_results out = new ThalLoad.Thal.thal_results();
        INSTANCE.set_thal_default_args(args);
        args.temp = 70.0;
        INSTANCE.thal(primer1.toString().getBytes(),primer2.toString().getBytes(),args,out);
        System.out.println(new String(out.msg));
        System.out.println(out.temp);
        return args.dimer;
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
    @SuppressWarnings("Duplicates")
    public static void locations(Connection connection) throws ClassNotFoundException,
            SQLException, InstantiationException, IllegalAccessException, IOException {
        long time = System.nanoTime();
        String base = new File("").getAbsolutePath();
        DpalLoad.main(new String[0]);
        Dpal_Inst =DpalLoad.INSTANCE_WIN64;
        System.out.println(Dpal_Inst);
        Map<List<String>, DNASequence> fastas = FastaManager.getMultiFasta();
        Connection db = connection;
        Statement stat = db.createStatement();
        PrintWriter log = new PrintWriter(new File("javalog.log"));
        stat.execute("SET AUTOCOMMIT FALSE;");
        stat.execute("SET FILES LOG FALSE;");
        PreparedStatement st = db.prepareStatement("INSERT INTO Primerdb.MatchedPrimers(" +
                "Bp1, Primer, Bp2, PrimerMatch, Comp,FragAVG,FragVAR,H2SD,L2SD, Cluster, Strain)" +
                "Values(?,?,?,?,?,?,?,?,?,?,?)");
        ResultSet call = stat.executeQuery("Select * From Primerdb.Phages;");
        List<String[]> phages = new ArrayList<>();
        while (call.next()) {
            String[] r = new String[3];
            r[0]=call.getString("Strain");
            r[1]=call.getString("Cluster");
            r[2]=call.getString("Name");
            phages.add(r);
//            if(strain.equals("-myco")) {
//                if (r[2].equals("xkcd")) {
//                    strain = r[0];
//                }
//            }else if(strain.equals("-arthro")){
//                if (r[2].equals("ArV1")) {
//                    strain = r[0];
//                }
//            }
        }
        call.close();
        Set<String>strains = phages.stream().map(y->y[0]).collect(Collectors.toSet());
        for(String x:strains) {
            Set<String> clust = phages.stream().filter(y -> y[0].equals(x)).map(y -> y[1]).collect(Collectors.toSet());
            String[] clusters = clust.toArray(new String[clust.size()]);
//        String z ="A1";
            for (String z : clusters) {
                System.out.println("Starting:" + z);
                List<Primer> primers = new ArrayList<>();
                Set<Matches> matched = new HashSet<>();
                Set<String> clustphage = phages.stream()
                        .filter(a -> a[0].equals(x) && a[1].equals(z)).map(a -> a[2])
                        .collect(Collectors.toSet());
                String[] clustphages = clustphage.toArray(new String[clustphage.size()]);
                if (clustphages.length > 1) {
                    try {
                        ResultSet resultSet = stat.executeQuery("Select * from primerdb.primers" +
                                " where Strain ='" + x + "' and Cluster ='" + z + "' and UniqueP = true" +
                                " and Hairpin = false");
                        while (resultSet.next()) {
                            int len = resultSet.getInt("Bp");
                            Primer primer = new Primer(resultSet.getLong("Sequence"),len);
                            primer.setTm(resultSet.getDouble("Tm"));
                            primers.add(primer);
                        }
                        resultSet.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("Error occurred at " + x + " " + z);
                    }
                    System.out.println(primers.size());
                    Set<Primer> primerlist2 = primers.stream().collect(Collectors.toSet());
                    Primer[] primers2 = primerlist2.toArray(new Primer[primerlist2.size()]);
                    Map<String, Map<Long, List<Integer>>> locations = Collections.synchronizedMap(
                            new HashMap<>());
                    clustphage.stream().forEach(phage -> {
                        List<String>id = new ArrayList<>();
                        id.add(x);
                        id.add(z);
                        id.add(phage);
                        String sequence = fastas.get(id).getSequenceAsString()+
                                fastas.get(id).getReverseComplement().getSequenceAsString();
                        Map<Long, List<Integer>> seqInd = new HashMap<>();
                        for (int i = 0; i <= sequence.length() - 10; i++) {
                            long sub = Encoding.twoBitEncoding(sequence.substring(i, i + 10));
                            if (seqInd.containsKey(sub)) {
                                seqInd.get(sub).add(i);
                            } else {
                                List<Integer> list = new ArrayList<>();
                                list.add(i);
                                seqInd.put(sub, list);
                            }
                        }
                        Map<Long, List<Integer>> alllocs = new HashMap<>();
                        for (Primer primer : primers2) {
                            List<Integer> locs = new ArrayList<>();
                            String sequence1 = Encoding.twoBitDecode(primer.getSequence(),primer.getBps());
                            long frag = Encoding.twoBitEncoding(sequence1.substring(0, 10));
                            List<Integer> integers = seqInd.get(frag);
                            if (integers != null) {
                                for (Integer i : integers) {
                                    if ((sequence1.length() + i) < sequence.length() &&
                                            sequence.substring(i, sequence1.length() + i).equals(sequence1)) {
                                        locs.add(i);
                                    }
                                }
                            }
                            alllocs.put(Encoding.twoBitEncoding(sequence1), locs);
                        }
                        locations.put(phage, alllocs);
                    });
                    System.out.println("locations found");
                    System.out.println((System.nanoTime() - time) / Math.pow(10, 9) / 60.0);
                    final int[] k = new int[]{0};
                    primerlist2.parallelStream().forEach(a -> {
                        int matches = 0;
                        int i = 0;
                        while (primers2[i] != a) {
                            i++;
                        }
                        for (int j = i + 1; j < primers2.length; j++) {
                            double[] frags = new double[clustphages.length];
                            int phageCounter = 0;
                            Primer b = primers2[j];
                            boolean match = true;
                            if (matches > 0) {
                                break;
                            }
                            if (Math.abs(a.getTm() - b.getTm()) > 5.0 ||
                                    a.getSequence()==(b.getSequence())) {
                                continue;
                            }
                            for (String phage : clustphages) {
                                List<Integer> loc1 = locations.get(phage).get(
                                        a.getSequence());
                                List<Integer> loc2 = locations.get(phage).get(
                                        b.getSequence());
//                            if(loc1.size()==0){
//                                System.out.println(phage+" "+a.getSequence());
//                            }
                                if (loc1.size() == 0 || loc2.size() == 0) {
//                                if (loc1.size()!=1||loc2.size()!=1){
                                    match = false;
                                    break;
                                }
                                boolean found = false;
                                int fragCount =0;
                                int l1 = loc1.get(0);
                                int l2 = loc2.get(0);
                                int count1 = 0;
                                int count2 = 0;
                                int frag = Math.abs(l1 - l2);
                                while (!found) {
                                    if (frag >= 500 &&
                                            frag <= 2000) {
                                        fragCount++;
                                        if(++count1< loc1.size())
                                            l1 = loc1.get(count1);
                                        else if(++count2< loc2.size())
                                            l2 = loc2.get(count2);
                                    } else if (l1 < l2 && frag < 500) {
                                        count2++;
                                    } else if (l1 > l2 && frag < 500) {
                                        count1++;
                                    } else if (l1 > l2 && frag > 2000) {
                                        count2++;
                                    } else if (l1 < l2 && frag > 2000) {
                                        count1++;
                                    } else {
                                        break;
                                    }
                                    if (count1 < loc1.size() &&
                                            count2 < loc2.size()) {
                                        l1 = loc1.get(count1);
                                        l2 = loc2.get(count2);
                                        frag = Math.abs(l1 - l2);
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
                                matched.add(new Matches(a, b, frags));
                            }
                        }
//                    k[0]++;
//                    System.out.println(k[0]);
                    });
                    System.out.println((System.nanoTime() - time) / Math.pow(10, 9) / 60.0);
                    System.out.println("Primers matched");
                    int c = 0;
                    int i = 0;
                    try {
                        for (Matches primerkey : matched) {
                            c++;
                            long primer1 = primerkey.one.getSequence();
                            long primer2 = primerkey.two.getSequence();
                            st.setInt(1,primerkey.one.getBps());
                            st.setLong(2, primer1);
                            st.setInt(3,primerkey.two.getBps());
                            st.setLong(4, primer2);
                            st.setDouble(5, complementarity(
                                    Encoding.twoBitDecode(primer1,primerkey.one.getBps()),
                                    Encoding.twoBitDecode(primer2,primerkey.two.getBps()),
                                    Dpal_Inst));
                            st.setDouble(6, primerkey.stats.getMean());
                            st.setDouble(7, primerkey.stats.getVariance());
                            st.setDouble(8, primerkey.stats.getMean() + 2 * primerkey.stats.getStandardDeviation());
                            st.setDouble(9, primerkey.stats.getMean() - 2 * primerkey.stats.getStandardDeviation());
                            st.setString(10, z);
                            st.setString(11, x);
                            st.addBatch();
                            i++;
                            if (i == 1000) {
                                i = 0;
                                st.executeBatch();
                                db.commit();
                            }
                        }

                        if (i > 0) {
                            st.executeBatch();
                            db.commit();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("Error occurred at " + x + " " + z);
                    }
                    System.out.println(c);
                }
                log.println(z);
                log.flush();
                System.gc();
            }
        }

        stat.execute("SET AUTOCOMMIT TRUE;");
        stat.execute("SET FILES LOG TRUE;");
        st.close();
        stat.close();
        System.out.println("Matches Submitted");
    }
    public static void checker(Connection connection, int bps) throws ClassNotFoundException,
            SQLException, InstantiationException, IllegalAccessException, IOException {
        String base = new File("").getAbsolutePath();
        Connection db = connection;
        db.setAutoCommit(false);
        Statement stat = db.createStatement();
        PrintWriter log = new PrintWriter(new File("checkertest.log"));
        ImportPhagelist.getInstance().parseAllPhagePrimers(bps);
        stat.execute("SET FILES LOG FALSE;\n");
        ResultSet call = stat.executeQuery("Select * From Primerdb.Phages;");
        List<String[]> phages = new ArrayList<>();
        String strain = "";
        while (call.next()) {
            String[] r = new String[3];
            r[0]=call.getString("Strain");
            r[1]=call.getString("Cluster");
            r[2]=call.getString("Name");
            phages.add(r);
            if(r[2].equals("xkcd")) {
                strain = r[0];
            }
        }
        String x = strain;
        phages.stream().filter(y->y[0].equals(x)).map(y->y[1]).collect(Collectors.toSet())
                .forEach(z->{
                    System.out.println("Starting:"+z);
                    try {
                        List<String> primers = new ArrayList<String>();
                        Set<String> clustphages = phages.stream()
                                .filter(a -> a[0].equals(x) && a[1].equals(z)).map(a -> a[2])
                                .collect(Collectors.toSet());

                        ResultSet resultSet = stat.executeQuery("Select * from primerdb.primers" +
                                " where Strain ='" + x + "' and Cluster ='" + z + "' and UniqueP = true" +
                                " and Bp = " + Integer.valueOf(bps) + " and Hairpin = false");
                        while (resultSet.next()) {
                            primers.add(resultSet.getString("Sequence"));
                        }
                        if(primers.size()>0) {
                            for (int i = 0; i < 4; i++) {
                                String primer = primers.get(i);
                                for (String clustphage : clustphages) {
                                    if (!CSV.readCSV(base + "/PhageData/" + Integer.toString(bps)
                                            + clustphage + ".csv")
                                            .contains(primer))
                                        log.println("Problem " + z);
                                }
                            }
                            Set<String> nonclustphages = phages.stream()
                                    .filter(a -> a[0].equals(x) && !a[1].equals(z)).map(a -> a[2])
                                    .collect(Collectors.toSet());
                            log.println("Cluster phages done");
                            for (int i = 0; i < 4; i++) {
                                String primer = primers.get(i);
                                for (String nonclustphage : nonclustphages) {
                                    if (CSV.readCSV(base + "/PhageData/" + Integer.toString(bps)
                                            + nonclustphage + ".csv")
                                            .contains(primer))
                                        log.println("Problem " + z);
                                }
                            }
                            log.println("NonCluster phages done");
                        }
                    }catch (SQLException e){
                        e.printStackTrace();
                        System.out.println("Error occurred at " + x + " " + z);
                    }
                    log.println(z);
                    log.flush();
                    System.gc();
                });
        stat.execute("SET FILES LOG TRUE\n");
        stat.close();
        System.out.println("Primers Matched");
    }
    @Deprecated
    public static String readFile(String file) throws IOException {
        FileChannel inChannel = new RandomAccessFile(file, "r").getChannel();
        MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        char ch;
        StringBuffer line = new StringBuffer();
        for (int i = 0; i < buffer.limit(); i++)
        {
            ch = ((char) buffer.get());
                line.append(ch);
        }
        return line.toString();
    }
    @Deprecated
    public static String complementWC(String dna){
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<dna.length();i++){
            char c = dna.charAt(i);
            if(c == 'T'){
                builder.append('A');
            }
            if(c == 'A'){
                builder.append('T');
            }
            if(c == 'C'){
                builder.append('G');
            }
            if(c == 'G'){
                builder.append('T');
            }
        }
        return builder.reverse().toString();
    }

    private static class Primer{
        private long Sequence;
        private int bps;
        private double Tm;
        public Primer(long sequence,int bps){
            this.Sequence = sequence;
            this.bps=bps;
        }

        public int getBps() {
            return bps;
        }

        public long getSequence() {
            return Sequence;
        }

        public void setSequence(long sequence) {
            Sequence = sequence;
        }

        public double getTm() {
            return Tm;
        }

        public void setTm(double tm) {
            Tm = tm;
        }
    }
    private static class Matches{
        Primer one;
        Primer two;
        DescriptiveStatistics stats;
        public Matches(Primer primer1, Primer primer2, double[] arr){
            one=primer1;
            two=primer2;
            stats=new DescriptiveStatistics(arr);
        }

    }

}
