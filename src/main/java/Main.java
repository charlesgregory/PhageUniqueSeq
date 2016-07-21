//import com.nfsdb.journal.exceptions.JournalException;
import com.questdb.ex.JournalException;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;

import java.io.IOException;
import java.sql.SQLException;

/**Copyright (C) 2016  Thomas Gregory

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

 * Created by Thomas on 3/13/2016.
 */
public class Main {
    //Controls entry to other mains through arguments to the command prompt
    //actions must be specified
    public static void main(String[] args) throws ClassNotFoundException, SQLException,
            InstantiationException, IOException, IllegalAccessException, CompoundNotFoundException, JournalException {
        if(args.length==0){
            System.out.println("No actions specified\n" +
                    "options are:\n" +
                    "-all: performs all actions sequentially\n" +
                    "-build basepairnum: initially builds database of primers basepairnum\n" +
                    "-update basepairnum: Runs update on database of primers basepairnum\n" +
                    "-design: Designs primers");
        }
        else if(args[0].equals("-build")){
            HSqlManager.main(args);
        }
        else if(args[0].equals("-update")){
            Update.main(args);
        }
        else if(args[0].equals("-test")){
            Test.main(args);
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
