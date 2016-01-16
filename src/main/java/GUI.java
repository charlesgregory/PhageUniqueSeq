import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

/**
 * Created by Charles Gregory on 12/22/2015. Setups and runs GUI.
 */
public class GUI {
    GUI(){
        try {
            GUI.generateWindow(GUI.analysisContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static JButton p;
    private static JButton co;
    private static JButton u;
    private static JButton fil;
    private static int bp;
    private static JComboBox bpList;
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
        String[] list = {"15", "16", "17", "18", "19", "20"};
        bpList = new JComboBox(list);

        ListListener l = new ListListener();
        bpList.addActionListener(l);

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
