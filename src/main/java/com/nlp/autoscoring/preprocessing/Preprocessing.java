package com.nlp.autoscoring.preprocessing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Preprocessing {

    public String cleanFile(File file){

        //Reading file contents into a string
        Scanner sc = null;

        String fileContents="";
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while(sc.hasNextLine()){
            //temp = sc.nextLine();
            fileContents += sc.nextLine();
        }
        sc.close();

        //Replace all more than occurence of ?,!, . with a single occurence
        fileContents = fileContents.replaceAll("\\?+","?");
        fileContents = fileContents.replaceAll("(!+)", "!");
        fileContents = fileContents.replaceAll("\\.+", ".");

        //Add a space after punctuations
        fileContents = addSpaces(fileContents, "\\.",".");
        fileContents = addSpaces(fileContents, "\\?","?");
        fileContents = addSpaces(fileContents, "!","!");
        fileContents = addSpaces(fileContents, ",",",");
        fileContents = addSpaces(fileContents, ";",";");

        return fileContents;
    }

    private String addSpaces(String fileContents, String rexp, String punctuation) {
        //Cleaning the string to avoid sentences of the form 'time.This'
        char lastChar = fileContents.charAt(fileContents.length()-1);
        Boolean check = false;
        if(lastChar=='.')
            check = true;
        String[] fileClean = fileContents.split(rexp);
        fileContents = "";
        for(int index=0; index<fileClean.length; index++) {
            fileContents += fileClean[index];
            if(index!=fileClean.length-1){
                fileContents+=punctuation+" ";
            } else {
                if(check)
                    fileContents+=".";
            }
        }
        return fileContents;
    }
}
