package com.nlp.autoscoring.coherence;

import com.google.common.collect.Sets;
import com.nlp.autoscoring.parser.StanfordParser;
import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import net.didion.jwnl.dictionary.Dictionary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class TopicCoherence {
    public float checkforcoherence (File file, String fileContents, List<String> tags, List<String> lemma) {

        //Getting topic from index file
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("./input/testing/index.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String topic = "";
        assert scanner != null;
        Boolean isHeadline=true;
        while (scanner.hasNext()) {
            if(isHeadline) {
                isHeadline=false;
            }else {
                String newLine = scanner.nextLine();
                String[] fileDetails = newLine.split(";");
                String[] topicCol = fileDetails[1].split("\t");
                if (fileDetails[0].contains(file.getName()))
                    topic = topicCol[2];
            }
        }
        scanner.close();

        //getting nouns in topic
        HashSet<String> topicWordList = getNounList(topic, null,null);

        //getting list of nouns in essay
        HashSet<String> nouns =  getNounList(fileContents, tags, lemma);


        //WordNet initialization
        try {
            JWNL.initialize(new FileInputStream("./properties.xml"));
        } catch (Exception ex) {
            System.out.println("Unable to initialize");
        }

        //Getting dictionary instance
        final Dictionary dictionary = Dictionary.getInstance();

        //Synonyms
        IndexWord indexWord = null;
        HashSet<String> topicSynonyms = new HashSet();
        Set<String> topicSyn = Sets.newHashSet("");

        //Getting set of all synonyms related to the topic nouns
        try {
            for(String word: topicWordList) {

                indexWord = dictionary.getIndexWord(POS.NOUN, word);
                if(indexWord!=null) {
                    Synset[] senses = indexWord.getSenses();
                    for (Synset set : senses) {
                        Word[] wordList = set.getWords();
                        for(Word syn : wordList){
                            //System.out.println(syn.getLemma());
                            topicSynonyms.add(syn.getLemma());
                            topicSyn.add(syn.getLemma());
                        }
                    }

                }
            }

        } catch (JWNLException e) {
            e.printStackTrace();
        }


        //Finding if the synonym of the noun list exist in the topic noun synonyms list then increase count
        int count =0;
        try {
            for(String noun: nouns) {
                indexWord = dictionary.getIndexWord(POS.NOUN, noun);
                Set<String> synonyms = Sets.newHashSet("");
                if(indexWord!=null) {
                    Synset[] senses = indexWord.getSenses();
                    for (Synset set : senses) {
                        Word[] wordList = set.getWords();
                        for(Word syn : wordList){
                            synonyms.add(syn.getLemma());
                        }
                    }
                    if(!Sets.intersection(topicSyn,synonyms).isEmpty()) {
                        count++;
                    }
                }
            }

        } catch (JWNLException e) {
            e.printStackTrace();
        }

       // System.out.println("Number of common nouns used "+count);
       // System.out.println("Total nouns in the essay "+nouns.size());
        return count/(float)nouns.size();
    }

    /**
     * Getting list of nouns in the given string
     * @param text
     * @return list of nouns
     */
    private HashSet<String> getNounList(String text, List<String> tags, List<String> lemma) {
        HashSet<String> nouns =  new HashSet<>();
        if(tags==null && lemma==null) {
           /* List<String>*/ tags = StanfordParser.posTagging(text);
           /* List<String> */ lemma = StanfordParser.lemmatize(text);
        }
        for(int index=0; index<lemma.size();index++){
            if(tags.get(index).contains("NN"))
                nouns.add(lemma.get(index));
        }
        return nouns;

    }

}
