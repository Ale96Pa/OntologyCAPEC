/**
 * This class shows the output of the reasoning tasks in a suitable window.
 */
package ontoapp;

import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class GuiResult {
    
    public GuiResult(ArrayList<String> list, String title, int index){
        JFrame theFrame = new JFrame();
        theFrame.setTitle(title);
        theFrame.setLocation(index*300, 0);

        JPanel mainPanel = new JPanel();
        JTextArea theText = new JTextArea();
        theText.setEditable(false);
        theText.setVisible(true);
        
        for(String text : list){
            theText.append(text + "\n"); //append the contents of the array list to the text area
        }
        mainPanel.add(theText); //add the text area to the panel
        
        JScrollPane scroll = new JScrollPane(theText);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        theFrame.add(scroll);
        
        theFrame.pack();
        theFrame.setSize(450,700);
        theFrame.setVisible(true);
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
