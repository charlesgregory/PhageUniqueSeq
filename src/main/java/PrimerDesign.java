import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.alignment.SimpleGapPenalty;
import org.biojava.nbio.core.alignment.matrices.SubstitutionMatrixHelper;
import org.biojava.nbio.core.alignment.template.SequencePair;
import org.biojava.nbio.core.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;

/**
 * Created by musta_000 on 7/21/2016.
 */
public class PrimerDesign {
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
}
