package com.nlp.autoscoring.spelling;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class SpellingChecker {
    private String fileValue(File fileName) throws IOException {
        FileReader fileReader = new FileReader(fileName);
        String fileContents = "";

        int i ;

        while((i =  fileReader.read())!=-1){
            char ch = (char)i;

            fileContents = fileContents + ch;
        }
        return fileContents;
    }

    public void countSpellingMistakes(File fileName) throws IOException {
        SpellingChecker spellingChecker = new SpellingChecker();
        spellingChecker.countSpellingMistakes(fileName);
        String fileContent = fileValue(fileName);
        System.out.println(fileContent);

    }

}
