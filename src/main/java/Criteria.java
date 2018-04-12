
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Scanner;

public class Criteria {

   /* public Criteria(File[] files){

    }*/


    public void findCriteriaAndScore(File[] files){
        LengthOfEssay lengthOfEssay = new LengthOfEssay();
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

        //getting average scores for all low and high essays in the training corpus
       /* float averageLow = Math.round(lengthOfEssay.averageLength(fileGrades, "low"));
        float averageHigh = Math.round(lengthOfEssay.averageLength(fileGrades, "high"));

        float length;
        float score;
        System.out.println("Average - low: "+averageLow+" | high - "+averageHigh);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("essaygrades.csv", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

*/
        for(File file: files) {
            Preprocessing preprocessing = new Preprocessing();
            String fileContents = preprocessing.cleanFile(file);
            //length = (float) lengthOfEssay.findLengthOfEssay(fileContents);
           // score = scoreLength(length,averageHigh,averageLow);
            Syntax syntax = new Syntax();
            syntax.findMissingVerb(fileContents);
           // writer.println(file+";"+length+";"+score);
        }
       // writer.close();
    }

    private float scoreLength(float length, float averageHigh, float averageLow) {
        float score=1;

        if(length<10)
            score = 1;
        else {
            if (length>=averageHigh)
                score = 5;
            else if(length<averageHigh && length >= (averageHigh+averageLow)/2)
                score = 4;
            else if(length>=averageLow && length<(averageHigh+averageLow)/2)
                score = 3;
            else if(length<averageLow)
                score = 2;
        }
        return score;
    }
}
