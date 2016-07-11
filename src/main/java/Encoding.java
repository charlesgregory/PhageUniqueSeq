/**
 * Created by Thomas on 6/15/2016.
 */
public class Encoding {
    final static long ENCODING_MASK = 3L;
    final static long LEN_MASK =31L;

    public static void main(String[] args) {
        long encoded = twoBitEncoding("TGACGAGCC");
        long comp = reEncodeReverseComplementTwoBit(encoded);
        System.out.println(encoded);
        String decoded =twoBitDecode(encoded);
//        String decoded =Long.toBinaryString(encoded);
        System.out.println(decoded);
        System.out.println(comp);
        String dec = twoBitDecode(comp);
        System.out.println(dec);
    }

    public static long twoBitEncoding(String seq){
        long encoded = 0L;
        int curShift =5;
        encoded =encoded ^ Integer.toUnsignedLong(seq.length());
        for(char x:seq.toCharArray()){
            long baseEncoded;
            if(x=='A'){
                baseEncoded=0L;
            }else if (x=='G'){
                baseEncoded=1L;
            }else if (x=='C'){
                baseEncoded=2L;
            }else {
                baseEncoded=3L;
            }
            encoded=baseEncoded << curShift ^ encoded;
            curShift+=2;
        }
        return encoded;
    }
    public static long reEncodeReverseComplementTwoBit(long encoding) {
        long length= encoding & LEN_MASK;
        long newEncoding = 0L;
        newEncoding = newEncoding | length;

        for (int i=5;i<=62;i=i+2){
            if((i-5)/2L==length){
                break;
            }
                    //Only have the bits of the letter
            long maskedLetter = encoding & (ENCODING_MASK << i);
            long rcEncoded = maskedLetter >> i;
            if(rcEncoded==0L) {
                rcEncoded = 3L;
            }else if (rcEncoded==1L) {
                rcEncoded = 2L;
            }else if (rcEncoded==2L) {
                rcEncoded = 1L;
            }else{
                rcEncoded=0L;
            }
            newEncoding = (rcEncoded << (((int) length)+4)*2-i | newEncoding);
        }

        return newEncoding;
    }
    public static String twoBitDecode(long encoded){
        long len = encoded & LEN_MASK;
        StringBuilder builder =new StringBuilder();
        for(int i=5;i<=62;i=i+2){
            if((i-5)/2L==len){
                break;
            }
            long maskedLetter = encoded & (ENCODING_MASK << i);
            long decodedChar = maskedLetter >> i;
            char newChar;
            if(decodedChar==0L){
                newChar='A';
            }else if(decodedChar==1L){
                newChar='G';
            }else if(decodedChar==2L){
                newChar='C';
            }else{
                newChar='T';
            }
            builder.append(newChar);

        }
        return builder.toString();
    }
}