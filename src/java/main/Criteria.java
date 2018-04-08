package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Criteria {

    public void averageSentences(File[] files){
        Scanner scanner = null;
        HashMap<String, String> fileGrades = new HashMap<>();

        //reading essay file name and grade from the csv file and saving in a hash map
        try {
            scanner = new Scanner(new File("./index.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (scanner.hasNext()) {
            String newLine = scanner.nextLine();
            String[] fileDetails = newLine.split(";");
            fileGrades.put(fileDetails[0],fileDetails[2]);
        }
        scanner.close();

        //Calculating average no. of sentences in the essays marked as low
        float averageLow,  totalLow = 0;
        float sumLow = 0;
        for (Map.Entry<String, String> fileGrade: fileGrades.entrySet()) {
            if(fileGrade.getValue().equals("low")){
                sumLow += findLength(new File("./essays/"+fileGrade.getKey()));
                totalLow++;
            }

        }
        if(totalLow!=0)
            averageLow = sumLow/totalLow;
        else
            averageLow = 0;

        //Calculating average no. of sentences in the essays marked as high
        float averageHigh = 0;
        float sumHigh = 0;
        float totalHigh = 0;
        for (Map.Entry<String, String> fileGrade: fileGrades.entrySet()) {
            if(fileGrade.getValue().equals("high")){
                sumHigh += findLength(new File("./essays/"+fileGrade.getKey()));
                totalHigh++;
            }

        }
        if(totalLow!=0)
            averageHigh = sumHigh/totalHigh;
        else
            averageHigh = 0;

        System.out.println("Average length of low grades is "+sumLow+"/"+totalLow+"="+averageLow);
        System.out.println("Average length of high grades is "+sumHigh+"/"+totalHigh+"="+averageHigh);

    }

    public int findLength(File filename)  {
        int length=0;

        //Reading file contents into a string
        Scanner sc = null;

        try {
            sc = new Scanner(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String fileContents ="";

        while(sc.hasNextLine()){
            fileContents += sc.nextLine();
        }
        sc.close();

        //Tokenizing and POStagging the string
        StanfordParser sparser = new StanfordParser();
        List<String> tokens = sparser.tokenize(fileContents);
        List<String> posTags = sparser.posTagging(fileContents);

        for(String token: tokens){
           // System.out.println(token);
            if(token.contains(".")||token.contains("?")||token.contains("!"))
                length++;
        }

       // System.out.println("Length of file "+filename.getName()+" is "+length);
        return length;
    }
}
