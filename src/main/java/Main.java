import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Thomas on 3/13/2016.
 */
public class Main {
    //Controls entry to other mains through arguments to the command prompt
    //actions must be specified
    public static void main(String[] args) throws ClassNotFoundException,
            SQLException, InstantiationException, IOException,
            IllegalAccessException, NoSuchFieldException {
        if(args.length==0){
            System.out.println("No actions specified\n" +
                    "options are:\n" +
                    "-all: performs all actions sequentially\n" +
                    "-build basepairnum: initially builds database of primers basepairnum\n" +
                    "-update basepairnum: Runs update on database of primers basepairnum\n" +
                    "-design: Designs primers");
        }
        else if (args[0].equals("-all")){
            HSqlManager.main(args);
            Update.main(args);
            HSqlPrimerDesign.main(args);
        }
        else if(args[0].equals("-build")){
            HSqlManager.main(args);
        }
        else if(args[0].equals("-update")){
            Update.main(args);
        }
        else if(args[0].equals("-design")){
            HSqlPrimerDesign.main(args);
        }
        else {
            System.out.println("Incorrect action:"+args[0]+"\n" +
                    "options are:\n" +
                    "-all: performs all actions sequentially\n" +
                    "-build basepairnum: initially builds database of primers basepairnum\n" +
                    "-update basepairnum: Runs update on database of primers basepairnum\n" +
                    "-design: Designs primers");
        }

    }
}
