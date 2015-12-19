
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by Charles Gregory on 11/5/2015.
 */

public class Test{

    private static class Phages implements ActionListener{
        public void actionPerformed(ActionEvent e){
            Cluster.allPhages(15);
        }
    }
    private static class Common implements ActionListener{
        public void actionPerformed(ActionEvent e){
            Cluster.assignClusters();
        }
    }
    private static class Unique implements ActionListener{
        public void actionPerformed(ActionEvent e){
            Cluster.unique();
        }
    }
    //Used for testing classes
    public static void main(String[] args)throws IOException {
        JButton p = new JButton("Process Phages");
        JButton co = new JButton("Common Analysis");
        JButton u = new JButton("Unique Analysis");

        Phages lp = new Phages();
        p.addActionListener(lp);

        Common lc = new Common();
        co.addActionListener(lc);

        Unique lu = new Unique();
        u.addActionListener(lu);

        JPanel content = new JPanel();
        content.setLayout(new FlowLayout());
        content.add(p);
        content.add(co);
        content.add(u);

        JFrame window = new JFrame("PhageUniqueSeq");
        window.setContentPane(content);
        window.setSize(400,400);
        window.setLocation(100,100);
        window.setVisible(true);
    }
}
