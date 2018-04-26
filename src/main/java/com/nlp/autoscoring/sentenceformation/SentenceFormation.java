package com.nlp.autoscoring.sentenceformation;

import com.nlp.autoscoring.parser.StanfordParser;
import com.nlp.autoscoring.preprocessing.Preprocessing;
import edu.stanford.nlp.trees.*;

import java.io.File;
import java.util.List;

public class SentenceFormation {

    public int countOfFragments(String fileContents) {
        StanfordParser stp = new StanfordParser();
        int count = 0;
        List<Tree> trees = stp.parse(fileContents);

        for (Tree tree : trees) {
            String strTree = tree.toString();

           // System.out.println(strTree);
            if (strTree.contains("(ROOT (FRAG")) {
                count++;
            } else
            {
                String[] tag = strTree.split(" ");

                for(int i=0; i<tag.length;i++) {
                    if (tag[i].toLowerCase().equals("because)") && tag[i + 1].equals("(S") && tag[i + 2].equals("(VP") && tag[i + 3].equals("(VBG")) {
                        count++;
                        //System.out.println(strTree);
                    }

                }

            }
        }
        return count;
    }

    public static void main(String args[]){
        //File[] files = fs.getInput();
       //File file = new File("./input/testing/essays/52951.txt");
        //File[] files = fs.getInput();
        File folder = new File("./input/testing/essays");
        File[] filesInFolder = folder.listFiles();

        for(File file: filesInFolder){
            Preprocessing preprocessing = new Preprocessing();
            String fileContents = preprocessing.cleanFile(file);
            SentenceFormation sentenceFormation = new SentenceFormation();
            System.out.println(file.getName()+" -- "+sentenceFormation.countOfFragments(fileContents));
           // System.out.println(sentenceFormation.countOfFragments("Because running alone"));
        }

    }
}





