import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by Charles Gregory on 12/22/2015. Setups and runs GUI.
 */
public class GUI {
    private static JButton p;
    private static JButton co;
    private static JButton u;
    private static JButton fil;
    private static int bp;
    private static JComboBox bpList;
    private static JComboBox strainList;
    private static ImportPhagelist phagelist;
    GUI(){
        try {
            phagelist =ImportPhagelist.getInstance();
            GUI.generateWindow(GUI.analysisContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Combo box listener
    private static class ListListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if (e.getSource() == bpList){
                String select = (String)bpList.getSelectedItem();
                if (select.equals("15")){bp = 15;}
                else if (select.equals("16")){bp = 16;}
                else if (select.equals("17")){bp = 17;}
                else if (select.equals("18")){bp = 18;}
                else if (select.equals("19")){bp = 19;}
                else if (select.equals("20")){bp = 20;}
            }
            if (e.getSource() == strainList){
                 phagelist.chosenStrain= (String)strainList.getSelectedItem();

            }

        }
    }

    //Button Listener
    private static class ClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            if (e.getSource() == p){
                Cluster.allPhages(bp);
                p.setText("Finished");
            }
            else if (e.getSource() == co){
                Cluster.assignClusters();
                co.setText("Finished");
            }
            else if (e.getSource() == u){
                Cluster.unique();
                u.setText("Finished");
            }
            else if (e.getSource() == fil){
                PrimerDesign.filterInitialUnique();
                fil.setText("Finished");
            }
        }
    }
    //Creates JPanel content for the analysis view
    public static JPanel analysisContent()throws IOException {
        p = new JButton("Process Phages");
        co = new JButton("Common Analysis");
        u = new JButton("Unique Analysis");
        fil = new JButton("Filter results");
        String[] numberlist = {"15", "16", "17", "18", "19", "20"};
        List<String> strains = phagelist.strains.stream().collect(Collectors.toList());
        Collections.sort(strains);
        String[] strainArray = strains.toArray(new String[strains.size()]);
        bpList = new JComboBox(numberlist);
        strainList = new JComboBox(strainArray);

        ListListener bpListener = new ListListener();
        bpList.addActionListener(bpListener);

        ListListener strainListener = new ListListener();
        strainList.addActionListener(strainListener);

        ClickListener lp = new ClickListener();
        p.addActionListener(lp);

        ClickListener lc = new ClickListener();
        co.addActionListener(lc);

        ClickListener lu = new ClickListener();
        u.addActionListener(lu);

        ClickListener fi = new ClickListener();
        fil.addActionListener(fi);

        JPanel content = new JPanel();
        content.setLayout(new FlowLayout());
        content.add(new JLabel("Choose a bp size:"));
        content.add(bpList);
        content.add(new JLabel("Choose a strain:"));
        content.add(strainList);
        content.add(p);
        content.add(co);
        content.add(u);
        content.add(fil);
        return content;
    }
    //Future view
    public static JPanel viewCommonResults(){

        JPanel content = new JPanel();
        content.setLayout(new FlowLayout());
        return content;
    }
    //Future view
    public static JPanel viewResultsContent(){
        JPanel content = new JPanel();
        content.setLayout(new FlowLayout());
        return content;
    }
    //Create singleton instance of the JFrame
    private static JFrame instance;
    public static JFrame getInstance(){
        if (instance == null) {
            instance = new JFrame("PhageUniqueSeq");
        }
        return instance;
    }
    //generates a window
    public static void generateWindow(JPanel content){
        JFrame window = getInstance();
        window.setContentPane(content);
        window.setSize(400,400);
        window.setLocation(100,100);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

}
