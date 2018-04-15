package com.nlp.autoscoring;

import com.nlp.autoscoring.criteria.Criteria;
import com.nlp.autoscoring.spelling.SpellingChecker;

import javax.swing.*;
import java.io.File;
import com.nlp.autoscoring.criteria.Criteria;
import com.nlp.autoscoring.spelling.SpellingChecker;

import javax.swing.*;
import java.io.File;

class FileChooser extends JPanel {
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

