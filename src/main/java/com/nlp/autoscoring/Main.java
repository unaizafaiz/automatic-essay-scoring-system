package com.nlp.autoscoring;

import com.nlp.autoscoring.essayevaluation.Score;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class Main{ //extends JPanel{

    //Test function to get no of files marked wrong
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
        // File folder =  new File("/Users/unaizafaiz/Downloads/essays_dataset/essays/development");

        //Reading files from the testing essay folder
        File folder = new File("./input/testing/essays");
        File[] filesInFolder = folder.listFiles();

        //Calculating score for the essays
        Score score = new Score();
        score.findCriteriaAndScore(filesInFolder);

        //Comparing the result
        //compareFinalGrade();

    }


}
