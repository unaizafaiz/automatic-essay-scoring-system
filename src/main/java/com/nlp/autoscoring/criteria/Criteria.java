package main;

import com.nlp.autoscoring.parser.StanfordParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class Criteria {


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

        //Tokenizing and POStagging the string
        StanfordParser sparser = new StanfordParser();
        List<String> tokens = sparser.tokenize(fileContents);
        List<String> posTags = sparser.posTagging(fileContents);

        for(String token: tokens){
           // System.out.println(token);
            if(token.contains("."))
                length++;
        }

        System.out.println("Length of file "+filename.getName()+" is "+length);
        return length;
    }
}
