import javafx.util.Pair;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
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

 * Created by Charles Gregory on 11/17/2015.
 * Manages the cluster operations. Takes all phages from the phage list
 * and can perform determination of all common sequences and all unique sequences
 * Depreciated due to the switch to Hsqldb as information storage
 */
@Deprecated
public class Cluster {
    //Creates primer data for each phage
    public static void allPhages(int bps) {
        ImportPhagelist list = null;
        try {
            list = ImportPhagelist.getInstance();
            list.full = list.readFile(list.path, list.chosenStrain);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String base = new File("").getAbsolutePath();
        File file = new File(base + "\\PhageData");
        CSV.makeDirectory(file);
        int count = 0;
        list.full.forEach(x -> {
            try {
                CSV.writeDataCSV(x[1], Fasta.process(x[1], bps),bps);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        System.out.println("Phages Processed");
    }

    /*Creates common sequences for each cluster using
    phage data from the data created from the allPhages method
     */
    public static void assignClusters() {
        String base = new File("").getAbsolutePath();
        ImportPhagelist list = null;
        File file = new File(base + "\\Common");
        CSV.makeDirectory(file);
        try {
            list = ImportPhagelist.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, List<String[]>> collect = list.full.stream()
                .collect(Collectors.groupingBy(l -> l[0]));
        Map<String, Set<String>> clusters = collect.entrySet().parallelStream()
                .map(x -> {
                    List<String> mapEntryValues = getPhageNames(x);
                    String firstName = mapEntryValues.get(0);
                    Set<String> clusterSet = CSV.readCSV(base + "\\PhageData\\" + firstName + ".csv");

                    x.getValue().stream().skip(1).forEach(y -> {
                                clusterSet.retainAll(CSV.readCSV(base + "\\PhageData\\" + y[1] + ".csv"));

                            }
                    );
                    return new Pair<>(x.getKey(), clusterSet);
                }).collect(Collectors.toMap(pair -> pair.getKey(), s -> s.getValue()));
        clusters.entrySet().forEach(x -> {
            try {
                CSV.writeCommonCSV(x.getKey(), x.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        System.out.println("Common done");
    }

    //get phage names for use in previous method
    private static List<String> getPhageNames(Map.Entry<String, List<String[]>> mapEntry) {
        return mapEntry.getValue().stream()
                .map(x -> x[1])
                .collect(toList());
    }

    //creates unique sequences for all clusters using the common
    public static void unique() {
        String base = new File("").getAbsolutePath();
        File file = new File(base + "\\Unique");
        CSV.makeDirectory(file);
        ImportPhagelist list = null;
        try {
            list = ImportPhagelist.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File[] files1 = new File(base + "\\Common\\").listFiles();
        List<File> commonFiles = new ArrayList<>();
        for (File x : files1) {
            commonFiles.add(x);
        }
        File[] files2 = new File(base + "\\PhageData\\").listFiles();
        List<File> phageFiles = new ArrayList<>();
        for (File x : files2) {
            phageFiles.add(x);
        }
        Map<String, List<String[]>> collect = list.full.stream()
                .collect(Collectors.groupingBy(l -> l[0]));
        commonFiles.stream().forEach(x -> {
            Set<String> common = CSV.readCSV(x.getAbsolutePath());
            String cluster = x.getAbsolutePath().substring(x.getAbsolutePath().indexOf("on\\") + 3,
                    x.getAbsolutePath().indexOf(".csv"));
            Set<String> clusterPhages = collect.get(cluster).stream().map(z -> z[1]).collect(Collectors.toSet());
            phageFiles.stream().forEach(y -> {
                String phage = y.getAbsolutePath().substring(y.getAbsolutePath().indexOf("ata\\") + 4,
                        y.getAbsolutePath().indexOf(".csv"));
                if (clusterPhages.contains(phage)) {
                } else {
                    common.removeAll(CSV.readCSV(y.getAbsolutePath()));
                }
            });

            try {
                CSV.writeUniqueCSV(cluster, common);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(cluster);

        });
    }
}