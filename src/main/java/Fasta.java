
import org.apache.commons.io.FileUtils;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

 * Created by Charles Gregory on 11/6/2015.
 * Controls the fasta files. This class can download fasta files from phagesdb.org
 * using the phagelist. It can also parse the fasta sequence and split the fasta into pieces.
 */
@Deprecated
public class Fasta {
    //Reads fasta sequence
    public static String[] parse(String path){
        String seq1 = null;
        String seq2 = null;
        String[] r = new String[2];
        try{
            //
            LinkedHashMap<String, DNASequence> f = FastaReaderHelper.readFastaDNASequence(new File(path));
            List<String> dnaList = new ArrayList<>(1);
            for (Map.Entry<String, DNASequence> stringDNASequenceEntry : f.entrySet()) {
                dnaList.add(stringDNASequenceEntry.getValue().getSequenceAsString().toUpperCase());
                dnaList.add(stringDNASequenceEntry.getValue()
                        .getReverseComplement().getSequenceAsString().toUpperCase());
            }
            seq1 = dnaList.get(0);
            seq2 = dnaList.get(1);
            r[0] = seq1;
            r[1] = seq2;
        }
        catch (java.lang.Exception e){
            System.out.print(e);
        }
        return r;
    }
    /**Splits the fasta sequence into a set of every possible
    //sequence of a certain size which can be found in the sequence
    including the reverse strand*/
    public static Set<CharSequence> splitFasta(String[] seq, int length){

        Set<CharSequence> collect = IntStream.range(0, length).mapToObj(start -> {
            List<CharSequence> primers = new ArrayList<>();
            for (int i = start; i < seq[0].length() - length; i += length) {
                CharSequence s = seq[0].substring(i, i + length);
                primers.add(s);
            }
            return primers;
        }).flatMap((i) -> i.stream()).collect(Collectors.toSet());
        Set<CharSequence> collect2 = IntStream.range(0, length).mapToObj(start -> {
            List<CharSequence> primers = new ArrayList<>();
            for (int i = start; i < seq[1].length() - length; i += length) {
                CharSequence s = seq[1].substring(i, i + length);
                primers.add(s);
            }
            return primers;
        }).flatMap((i) -> i.stream()).collect(Collectors.toSet());
        collect.addAll(collect2);
        return collect;
    }
    //Downloads the fasta files from phagesdb.org based off the name of the phage
    //Also controls for inconsistencies in the phagelist
    public static String Download(String name) {
        String path;
        if(name.equals("BrownCNA")){
            path ="http://phagesdb.org/media/fastas/Browncna.fasta";
        }
        else if(name.equals("GUmbie")){
            path ="http://phagesdb.org/media/fastas/Gumbie.fasta";
        }
        else if(name.equals("Numberten")){
            path ="http://phagesdb.org/media/fastas/NumberTen.fasta";
        }
        else if(name.equals("Seabiscuit")){
            path ="http://phagesdb.org/media/fastas/SeaBiscuit.fasta";
        }
        else if(name.equals("Caliburn")) {
            path = "http://phagesdb.org/media/fastas/Excalibur.fasta";
        }
        else if(name.equals("Godpower")) {
            path = "http://phagesdb.org/media/fastas/GodPower.fasta";
        }
        else if(name.equals("Romney")) {
            path = "http://phagesdb.org/media/fastas/Romney2012.fasta";
        }
        else{
            path = "http://phagesdb.org/media/fastas/"+name+".fasta";
        }
        String base = new File("").getAbsolutePath();
        name = base+"/Fastas/"+name+".fasta";
        File file = new File(name);
        try {
            if(!file.exists()) {
                URL netPath = new URL(path);
                FileUtils.copyURLToFile(netPath, file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.toString();
    }
    //processes a phage based off name by downloading, parsing, and splitting it
    public static Set<CharSequence> process(String name, int bps){
        String path = Download(name);
        String[] seq = parse(path);
        return splitFasta(seq, bps);
    }
    private static List<CharSequence> splitFasta2(String[] seq, int length){

        List<CharSequence> collect = IntStream.range(0, length).mapToObj(start -> {
            List<CharSequence> primers = new ArrayList<>();
            for (int i = start; i < seq[0].length() - length; i += length) {
                CharSequence s = seq[0].substring(i, i + length);
                primers.add(s);
            }
            return primers;
        }).flatMap((i) -> i.stream()).collect(Collectors.toList());
        List<CharSequence> collect2 = IntStream.range(0, length).mapToObj(start -> {
            List<CharSequence> primers = new ArrayList<>();
            for (int i = start; i < seq[1].length() - length; i += length) {
                CharSequence s = seq[1].substring(i, i + length);
                primers.add(s);
            }
            return primers;
        }).flatMap((i) -> i.stream()).collect(Collectors.toList());
        collect.addAll(collect2);
        return collect;
    }
    public static List<CharSequence> processPrimers(String name, int bps){
        String path = Download(name);
        String[] seq = parse(path);
        return splitFasta2(seq, bps);
    }
}
