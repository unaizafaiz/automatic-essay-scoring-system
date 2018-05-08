package com.nlp.autoscoring.coherence;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.nlp.autoscoring.parser.StanfordParser;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.Dictionaries;
import edu.stanford.nlp.util.IntPair;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class TextCoherence {
    private ClassLoader classLoader = getClass().getClassLoader();
    // Loading the pronouns.txt file for collecting all the pronouns from the essay
    private final File fileName = new File(Objects.requireNonNull(classLoader.getResource("pronouns.txt").getFile()));
    private static Charset charset = Charset.forName("UTF-8");
    private Set<String> pronoun;

    {
        try {
            pronoun = Sets.newHashSet(Files.readLines(fileName, charset));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public float checkCoherency(String text, Map<Integer, CorefChain> corefText) {
        // Count is number of pronouns that are not co-referenced
        float count = 0;
        // totalCount is the number of pronouns occurring in the essay
        float totalCount = 0;
        // Loading all the pronouns mapped to the sentence in which they appeared
        Map<Integer, List<String>> corefTect = extractCorefPronoun(corefText);
        Map<Integer, List<String>> pronounText = checkPronoun(text, pronoun);

        // Calculating the value of count and totalCount
        for(int index : pronounText.keySet()){
            if(corefTect.get(index) == null) {
                count += pronounText.get(index).size();
                totalCount += pronounText.get(index).size();
            } else{
                for(String stringItems : pronounText.get(index)) {
                    if (!corefTect.get(index).contains(stringItems)) {
                        count++;
                    }
                    totalCount++;
                }
            }
        }
        return count / totalCount;
    }

    // Collecting all the pronouns co-referenced which are mainly co-referenced but also considering pronouns which need not be co-referenced like "in 'OUR' world"
    private Map<Integer, List<String>> extractCorefPronoun(Map<Integer, CorefChain> corefText) {
        ListMultimap<Integer, String> result = ArrayListMultimap.create();
        for (int index : corefText.keySet()) {
            Map<IntPair, Set<CorefChain.CorefMention>> mentionMap = corefText.get(index).getMentionMap();
            for (IntPair val : mentionMap.keySet()) {
                int sentenceNumber = val.getSource();
                Set<CorefChain.CorefMention> mentions = mentionMap.get(val);
                for(CorefChain.CorefMention mention : mentions){
                    String mentionWord = mention.mentionSpan;
                    if(Dictionaries.MentionType.PRONOMINAL.equals(mention.mentionType)){
                        result.put(sentenceNumber, mentionWord.toLowerCase());
                    } else {
                        for(String word : StanfordParser.tokenize(mentionWord)){
                            if(pronoun.contains(word.toLowerCase())){
                                result.put(sentenceNumber, word.toLowerCase());
                            }
                        }
                    }
                }
            }

        }
        return Multimaps.asMap(result);
    }

    // Collecting all the pronouns in the essay
    private Map<Integer, List<String>> checkPronoun(String text, Set<String> pronoun) {
        Map<Integer, List<String>> result = new HashMap<>();
        List<String> textList = StanfordParser.sentenceSplit(text);
        for (String sentence : textList) {
            List<String> sent = StanfordParser.tokenize(sentence);
            List<String> s = new ArrayList<>();
            for (String word : sent) {
                if (pronoun.contains(word.toLowerCase())) {
                    s.add(word.toLowerCase());
                }
            }
            if(!s.isEmpty()) {
                result.put(textList.indexOf(sentence)+1, s);
            }
        }
        return result;
    }
}
