package main;

import javax.swing.*;
import java.io.File;

class FileChooser extends JPanel{
    public File[] getInput(JFrame frame){

        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);

        // Show the dialog; wait until dialog is closed
        chooser.showOpenDialog(frame);

        // Retrieve the selected files.
        File[] files = chooser.getSelectedFiles();

        //close the panel
        frame.dispose();
        return files;
    }
}

public class Main extends JPanel {

    private static void printOutput(){

    }

    public static void main(String[] args){

        FileChooser fs = new FileChooser();

        //Create and set up the window.
        JFrame frame = new JFrame("FileChooserDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(fs);

        //Display the window.
        frame.pack();
        frame.setVisible(true);

        File[] files = fs.getInput(frame);

        for(int i=0;i<files.length;i++)
            System.out.println(files[i].getName());
    }
}
