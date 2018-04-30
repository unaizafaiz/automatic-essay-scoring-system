package com.nlp.autoscoring.spelling;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.nlp.autoscoring.parser.StanfordParser;

public class SpellingChecker {
    private ClassLoader classLoader = getClass().getClassLoader();
    private final File fileName = new File(Objects.requireNonNull(classLoader.getResource("dictionary.txt")).getFile());
    private final File stopWordFileName = new File(Objects.requireNonNull(classLoader.getResource("wordStopper.txt").getFile()));
    private static Charset charset = Charset.forName("UTF-8");
    private Set<String> dictionary;
    private Set<String> wordStopper;

    {
        try {
            dictionary = Sets.newHashSet(Files.readLines(fileName, charset));
            wordStopper = Sets.newHashSet(Files.readLines(stopWordFileName, charset));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Counting the words which are misspelled checking if the words are present in the dictionary.txt or stop-words.txt
    public String countSpellingMistakes(String fileName, List<String> tokenizedFileContents,List<String> posTaggedFileContents ){
        int mistakesCount = 0 ;
        //List<String> tokenizedFileContents = StanfordParser.tokenize(fileName);
       // List<String> posTaggedFileContents;
        //posTaggedFileContents = StanfordParser.posTagging(fileName);
        int wordCount = posTaggedFileContents.size();
        Set<String> tags = Sets.newHashSet("$","#","\"","(",")",",",".",":");
        //List<edu.stanford.nlp.trees.Tree> structure = StanfordParser.parse(fileContents);
        //System.out.println(structure);
        for(String posTag: posTaggedFileContents){
            if(tags.contains(posTag))
                wordCount--;
        }
        for(String word : tokenizedFileContents){
            if(!dictionary.contains(word) && !wordStopper.contains(word) && posTaggedFileContents.get(tokenizedFileContents.indexOf(word)).equals("NN")){
                mistakesCount++;
            }
        }

       // System.out.println(mistakesCount+" "+wordCount);
        return mistakesCount+" "+tokenizedFileContents.size();
    }
}
