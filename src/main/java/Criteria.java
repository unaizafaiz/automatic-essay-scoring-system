import java.StanfordParser;
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
            //temp = sc.nextLine();
            fileContents += sc.nextLine();
        }
        sc.close();

        //Cleaning the string to avoid sentences of the form " time.This"
        String[] fileClean = fileContents.split("\\.");
        fileContents = "";
        for(int index=0; index<fileClean.length; index++) {
            System.out.println(fileClean[index]);
            fileContents += fileClean[index] + ". ";
        }

       //String fileContents = " Hi! My name is U. Faiz. How are you doing? ";
        //Tokenizing and POStagging the string
        StanfordParser sparser = new StanfordParser();
        List<String> tokens = sparser.tokenize(fileContents);
        List<String> posTags = sparser.posTagging(fileContents);
       /* List<String> sentences = sparser.sentenceSplit(fileContents);
        for (String sentence : sentences) {
            length++;
            System.out.print("[" + sentence + "] ");
        }*/

        for(int index = 0; index < tokens.size(); index++) {
            String token = tokens.get(index);
            String posTag = posTags.get(index);

            System.out.print("[" + token + "/" + posTag + "] ");
        }
        for(String posTag: posTags){
           // System.out.println(token);
            if(posTag.equals("."))
                length++;
        }
        if(!posTags.get(posTags.size()-1).equals("."))
            length++;

        System.out.println();
       System.out.println("Length of file "+filename.getName()+" is "+length);
        return length;
    }

    public static void main(String[] args){
        Criteria criteria = new Criteria();
        int length = criteria.findLength(new File("./index.csv"));
    }
}
