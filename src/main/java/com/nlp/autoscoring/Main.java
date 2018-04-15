package com.nlp.autoscoring;

import com.nlp.autoscoring.agreement.SentenceAgreement;
import com.nlp.autoscoring.criteria.Criteria;
import com.nlp.autoscoring.spelling.SpellingChecker;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.logging.RedwoodConfiguration;


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
        /*for(File file : files) {
           *//* String fileContent = null;
            try {
                fileContent = FileToStringConverter.toStringConverter(file);
            } catch (IOException e) {
                e.printStackTrace();
            }*//*
              //1. length
            //    spellingChecker.countSpellingMistakes(fileContent);
            //SentenceAgreement.countAgreementFailures(fileContent);
        }*/
    }
}
