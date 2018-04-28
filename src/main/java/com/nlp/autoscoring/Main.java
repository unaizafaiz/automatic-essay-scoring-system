package com.nlp.autoscoring;

import com.nlp.autoscoring.essayevaluation.Score;
import com.nlp.autoscoring.parser.StanfordParser;
import com.nlp.autoscoring.preprocessing.Preprocessing;
import edu.stanford.nlp.dcoref.CorefChain;


import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;


public class Main{ //extends JPanel{

    private static void compareFinalGrade(){
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("./essayscores.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int countLow=0;
        int countHigh=0;

        assert scanner != null;
        while (scanner.hasNext()) {
            String newLine = scanner.nextLine();
            String[] fileDetails = newLine.split(";");
            if(!fileDetails[10].equals(" "+fileDetails[11].toUpperCase()))
                if(fileDetails[11].equals("low")) {
                    countLow++;
                    System.out.println(fileDetails[0]+" is marked high but is low");
                } else {
                    countHigh++;
                    System.out.println(fileDetails[0]+" is marked low but is high");
                }
        }
        scanner.close();
        System.out.println("Count low mismatch = "+countLow);
        System.out.println("Count high mismatch = "+countHigh);

    }

    public static void main(String[] args){
        //FileChooser fs = new FileChooser();
        //File[] files = fs.getInput();
        File folder = new File("./input/testing/essays");
        File[] filesInFolder = folder.listFiles();

        //Score score = new Score();
        //score.findCriteriaAndScore(filesInFolder);
        Preprocessing preprocessing = new Preprocessing();
        StanfordParser stp = new StanfordParser();
        for(File file: filesInFolder){
            String a = file.toString();
            System.out.println(a);
            System.out.println(file.getName()+"-----------------");
            String text = preprocessing.cleanFile(file);
            Map<Integer,CorefChain> graph = stp.coreferenceResolution(text);
            for (CorefChain entry : graph.values()) {
                System.out.println(entry);
            }
            System.out.println();
        }
       // compareFinalGrade();

    }


}
