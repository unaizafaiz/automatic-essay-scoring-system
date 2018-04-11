package main;

import javax.swing.*;
import java.io.File;

class FileChooser extends JPanel{
    public File[] getInput(){


        //Create and set up the window.
        JFrame frame = new JFrame("File Chooser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JFileChooser chooser = new JFileChooser();

        //Add content to the window.
        frame.add(new FileChooser());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
        chooser.setMultiSelectionEnabled(true);

        // Show the dialog; wait until dialog is closed
        chooser.showOpenDialog(frame);

        // Retrieve the selected files.
        File[] files = chooser.getSelectedFiles();
        frame.dispose();
        return files;
    }
}

public class Main extends JPanel {

    private static void printOutput(){

    }

    public static void main(String[] args){

        FileChooser fs = new FileChooser();
        File[] files = fs.getInput();

        Criteria criteria = new Criteria();
        SpellingChecker spellingChecker = new SpellingChecker();

        //for each file find each criteria
        for(int i=0;i<files.length;i++) {
            criteria.findLength(files[i]); //1. length
            spellingChecker.countSpellingMistakes(files[i]);

        }
    }
}
