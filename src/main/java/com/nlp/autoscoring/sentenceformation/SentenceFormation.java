package com.nlp.autoscoring.sentenceformation;

import com.google.common.collect.Sets;
import com.nlp.autoscoring.parser.StanfordParser;
import com.nlp.autoscoring.preprocessing.Preprocessing;
import edu.stanford.nlp.trees.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class SentenceFormation {


    public float countOfFragments(String fileContents, List<String> sentences) {
        StanfordParser stp = new StanfordParser();
        int count = 0;
        //List<String> sentences = StanfordParser.sentenceSplit(fileContents);
        //System.out.println("Total sentences in this essay: "+sentences.size());
        for(String sentence : sentences) {
            List<Tree> trees = stp.parse(sentence.toString());

            //list of determiners that can be used with a pronoun
            Set<String> determiner = Sets.newHashSet("all)", "both)", "half)", "either)", "neither)", "what)", "rather)", "quiet)");

            //For each tree in each sentence in the essay find the invalid sentences
            for (Tree tree : trees) {
                String strTree = tree.toString();

                //If the sentence is a fragment
                if (strTree.contains("(ROOT (FRAG")) {
                   // System.out.println(strTree);
                    count++;
                } else if (tree.toString().contains("(ROOT (SINV ")) { //Checking for inverse sentence if contains a subj but not an obj then increase count
                    Boolean check = false;
                    if(!tree.toString().contains(" (SBAR ")) //if the sentence contains a clause then it might be incomplete
                            check=true;
                    if(check) {
                        // One time setup
                        TreebankLanguagePack tlp = new PennTreebankLanguagePack();
                        // Uncomment the following line to obtain original Stanford Dependencies
                        tlp.setGenerateOriginalDependencies(true);
                        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
                        // For each Tree
                        GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
                        Collection<TypedDependency> tdl = gs.typedDependencies();
                        // System.out.println(tdl);


                        if ((tdl.toString().contains("nsubj") || tdl.toString().contains("nsubjpass")) && (!tdl.toString().contains("dobj") && !tdl.toString().contains("aux"))) {
                            //System.out.println(tdl);
                            count++;
                        }
                    }
                } else if (strTree.contains("(ROOT (SBAR ")) { //if it is only a subordinate clause
                   // System.out.println(tree);
                    count++;
                } else {
                    String[] tag = strTree.split(" ");

                    for (int i = 0; i < tag.length; i++) { //
                        //Pattern of the type "because running alone"
                        if (tag[i].toLowerCase().equals("because)") && tag[i + 1].equals("(S") && tag[i + 2].equals("(VP") && tag[i + 3].equals("(VBG")) {
                            count++;
                           // System.out.println(strTree);
                        }
                        //identifies incorrect Determiner - Pronoun pairs of the form  -  the my, the your, an my etc excluding the determiners mentioned in the Set
                        else if (i < tag.length - 3) {
                            Set<String> temp = Sets.newHashSet(tag[i + 1]);
                            if (tag[i].equals("(DT") && tag[i + 2].equals("(PRP$") && Sets.intersection(determiner, temp).isEmpty()) {
                                count++;
                               // System.out.println(tag[i + 1] + " " + tag[i + 3]);
                            }
                        }

                        else if(i < tag.length - 7){
                            //Run on sentences - [SBAR [S [NP [PRP - independent clauses in one sentence or without a conjunction joining the two clauses
                            if(tag[i].equals("(SBAR") && tag[i+2].equals("(S") && tag[i+4].equals("(NP") && tag[i+6].equals("(PRP")){
                                count++;
                                System.out.println(tag[i+1]+" "+tag[i+3]+" "+tag[i+5]+" "+tag[i+7]);
                            }

                        }
                        //Fragments of the form - "The dog was waiting in the window when his owner got home. Then, excited, wagging his tail."
                        else if(tag[i].equals("(ROOT") && tag[i+2].equals("(S") && tag[i+4].equals("(ADVP") && tag[i+6].equals("(RB")) {
                            System.out.println(sentence);
                            count++;
                        }
                    }

                }

            }
        }
        return count/(float) sentences.size(); //returning the percentage depending on overall size of essay
    }

}





