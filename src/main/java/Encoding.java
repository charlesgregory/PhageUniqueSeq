/**
 * Created by Thomas on 6/15/2016.
 */
public class Encoding {
    final static long MASK = 3L;

    public static void main(String[] args) {
        long encoded = twoBitEncoding("ATGCATGCATGCATGCATGCATGCATGCATG");
        System.out.println(encoded);
        String decoded =twoBitDecode(encoded,31);
//        String decoded =Long.toBinaryString(encoded);
        System.out.println(decoded);
    }
    public static long twoBitEncoding(String seq){
        long encoded = 0L;
        int curShift =0;
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
    public static String twoBitDecode(long encoded,int len){
        StringBuilder builder =new StringBuilder();
        for(int i=0;i<=62;i=i+2){
            if(i/2==len){
                break;
            }
            long maskedLetter = encoded & (MASK << i);
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
