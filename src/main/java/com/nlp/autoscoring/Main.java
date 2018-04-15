package com.nlp.autoscoring;

import com.nlp.autoscoring.criteria.Criteria;
import com.nlp.autoscoring.spelling.SpellingChecker;

import javax.swing.*;
import java.io.File;
import java.io.IOException;


public class Main extends JPanel {


    public static void main(String[] args){

        FileChooser fs = new FileChooser();
        File[] files = fs.getInput();

        Criteria criteria = new Criteria();
        SpellingChecker spellingChecker = new SpellingChecker();

        criteria.findCriteriaAndScore(files);
        //for each file find each criteria
        for(int i=0;i<files.length;i++) {
            //criteria.findCriteriaAndScore(files[i]); //1. length
            try {
                spellingChecker.countSpellingMistakes(files[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
