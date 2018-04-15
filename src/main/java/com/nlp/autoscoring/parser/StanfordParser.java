package com.nlp.autoscoring.parser;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import edu.stanford.nlp.util.logging.RedwoodConfiguration;

/**
 * Standford Parser that parses the input string and gives the list of tokens and POS Tags.
 */

public class StanfordParser {

    public static List<String> tokenize(String text) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize");
        RedwoodConfiguration.empty().capture(System.err).apply();
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        RedwoodConfiguration.current().clear().apply();
        Annotation document = new Annotation(text);

        pipeline.annotate(document);
        List<CoreLabel> tokens = document.get(CoreAnnotations.TokensAnnotation.class);

        List<String> tokenList = new ArrayList<>();
        for (CoreLabel token : tokens) {
            String word = token.get(CoreAnnotations.TextAnnotation.class);
            tokenList.add(word);
        }
        return tokenList;
    }

    public static List<String> posTagging(String text) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos");
        RedwoodConfiguration.empty().capture(System.err).apply();
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        RedwoodConfiguration.current().clear().apply();

        Annotation document = new Annotation(text);

        pipeline.annotate(document);
        List<CoreLabel> tokenList = document.get(CoreAnnotations.TokensAnnotation.class);

        List<String> posTagList = new ArrayList<String>();
        for (CoreLabel token : tokenList) {
            String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
            posTagList.add(pos);
        }
        return posTagList;
    }

    public static List<String> sentenceSplit(String text) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit");
        RedwoodConfiguration.empty().capture(System.err).apply();
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        RedwoodConfiguration.current().clear().apply();

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // run all Annotators on this text
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        List<String> result = new ArrayList<>();
        for (CoreMap sentence : sentences) {
            String sentenceString = sentence.get(CoreAnnotations.TextAnnotation.class);
            result.add(sentenceString);

            // see tokenize(String) method
            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
            for (CoreLabel token : tokens) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
            }
        }

        return result;
    }

}
