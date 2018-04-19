package com.nlp.autoscoring.length;

import com.nlp.autoscoring.parser.StanfordParser;
import edu.stanford.nlp.trees.Tree;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LengthOfEssay {

    private static final Set<String> verbTags = com.google.common.collect.Sets.newHashSet( "[VBZ", "[VBP", "[VBD", "[VBN", "[VBG");

    public int findLengthOfEssay(String fileContents){
        StanfordParser stp = new StanfordParser();
        int countSentences = 0;
        List<Tree> trees = stp.parse(fileContents);

        for (Tree tree : trees) {
                String strTree = tree.toString();
                strTree = strTree.replace('(', '[');
                strTree = strTree.replace(')', ']');

            if(strTree.contains("[S")) {
                countSentences++;
                String[] splitStr = strTree.split(" ");
                Map<String, Integer> tagCount = new HashMap<>();
                tagCount.put("verb", 0);
                tagCount.put("[CC", 0);
                Boolean clause = false;

                for (int i = 0; i < splitStr.length; i++) {
                    //checking if a clause is present in the sentence then the main verb count is not taken
                    if (splitStr[i].equals("[SBAR")) {
                        clause = true;
                    }
                }

                for (int i = 0; i < splitStr.length; i++) {
                    if (verbTags.contains(splitStr[i]) && !clause) { //contains a verb and the sentence is not have a clause
                            tagCount.put("verb", tagCount.get("verb") + 1);
                    }
                    if(i<splitStr.length-2){
                        if(splitStr[i].equals("[CC") && splitStr[i+2].equals("[S")){
                            tagCount.put(splitStr[i], tagCount.get(splitStr[i]) + 1);
                        }
                    }
                }

                if (tagCount.get("verb") > tagCount.get("[CC")) {
                   // if (tagCount.containsKey("verb"))
                        if (tagCount.get("verb") > 1) {
                            countSentences += tagCount.get("verb") - 1;
                        }
                } else {
                    //if (tagCount.containsKey("[CC"))
                        countSentences += tagCount.get("[CC");
                }

            }

        }

        return countSentences;
    }

}


