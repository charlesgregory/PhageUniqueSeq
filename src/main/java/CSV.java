import java.io.*;
import java.util.*;

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

 * Created by Charles Gregory on 12/8/2015. Controls .csv file writing and reading.
 */
@SuppressWarnings("Duplicates")
@Deprecated
public class CSV {
    private static final String COMMA_DELIMITER = ",";
    //creates a directory
    public static void makeDirectory(File file){
        if(!file.exists()){
            file.mkdir();
        }
    }
    //Writes Common csv data
    public static void writeCommonCSV(String s, Set<String> data) throws IOException {
        String base = new File("").getAbsolutePath();
        FileWriter fileWriter = new FileWriter(base+"\\Common\\"+s+".csv");
        data.forEach(x ->{
            try {
                fileWriter.append(x);
                fileWriter.append(COMMA_DELIMITER);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        fileWriter.flush();
        fileWriter.close();
    }
    //Writes phageData csv data
    public static void writeDataCSV(String s,Set<CharSequence> data,int bps) throws IOException {
        String base = new File("").getAbsolutePath();
        File file =new File(base+"/PhageData/"+Integer.toString(bps)+s+".csv");
        if(!file.exists()) {
            FileWriter fileWriter = new FileWriter(base + "/PhageData/" + Integer.toString(bps) + s + ".csv");
            data.forEach(x -> {
                try {
                    fileWriter.append(x);
                    fileWriter.append(COMMA_DELIMITER);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            fileWriter.flush();
            fileWriter.close();
        }
    }
    public static void writeDataCSV(String s,List<CharSequence> data,int bps) throws IOException {
        String base = new File("").getAbsolutePath();
        FileWriter fileWriter = new FileWriter(base+"/PhageData/"+Integer.toString(bps)+s+"Primers.csv");
        data.forEach(x ->{
            try {
                fileWriter.append(x);
                fileWriter.append(COMMA_DELIMITER);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        fileWriter.flush();
        fileWriter.close();
    }
    //Writes unique csv data
    public static void writeUniqueCSV(String s, Set<String> data) throws IOException {
        String base = new File("").getAbsolutePath();
        FileWriter fileWriter = new FileWriter(base+"\\Unique\\"+s+".csv");
        data.forEach(x ->{
            try {
                fileWriter.append(x);
                fileWriter.append(COMMA_DELIMITER);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        fileWriter.flush();
        fileWriter.close();
    }
    //Writes filter csv data
    public static void writeFilteredCSV(String s,Set<CharSequence> data) throws IOException {
        String base = new File("").getAbsolutePath();
        FileWriter fileWriter = new FileWriter(base+"\\Filter\\"+s+".csv");
        data.forEach(x ->{
            try {
                fileWriter.append(x);
                fileWriter.append(COMMA_DELIMITER);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        fileWriter.flush();
        fileWriter.close();
    }
    //Writes Location csv data
    public static void writeLocationCSV(String s,int[] data) throws IOException {
        String base = new File("").getAbsolutePath();
        FileWriter fileWriter = new FileWriter(base+"\\Location\\"+s+".csv");
        for(int x: data){
            try {
                fileWriter.append(Integer.toString(x));
                fileWriter.append(COMMA_DELIMITER);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        fileWriter.flush();
        fileWriter.close();
    }
    //reads a csv written in any of the prior formats
    public static Set<String> readCSV(String path1){
        BufferedReader br = null;
        String line;
        String cvsSplitBy = ",";
        String[] prim =null;
        try {
            br = new BufferedReader( new FileReader(path1));
            while ((line = br.readLine()) != null) {

                prim = line.split(cvsSplitBy);
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Set<String> s = new HashSet<>();
        if(prim != null){
            Collections.addAll(s, prim);
        }
        return s;
    }
    //reads a csv into a list instead of a set
    public static List<CharSequence> readNonSetCSV(String path1){
        BufferedReader br = null;
        String line;
        String cvsSplitBy = ",";
        CharSequence[] prim =null;
        try {
            br = new BufferedReader( new FileReader(path1));
            while ((line = br.readLine()) != null) {

                prim = line.split(cvsSplitBy);
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        List<CharSequence> s = new ArrayList<>();
        if(prim != null){
            Collections.addAll(s, prim);
        }
        return s;
    }

}
