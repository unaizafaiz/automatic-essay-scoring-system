package com.nlp.autoscoring.parser;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

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

    public static Map<Integer,CorefChain> coreferenceResolution(String text) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, mention, coref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // run all Annotators on this text
        pipeline.annotate(document);

        return document.get(CorefCoreAnnotations.CorefChainAnnotation.class);
    }

    public static List<String> lemmatize(String text) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // run all Annotators on this text
        pipeline.annotate(document);
        List<CoreLabel> tokens = document.get(CoreAnnotations.TokensAnnotation.class);

        List<String> result = new ArrayList<String>();
        for (CoreLabel token : tokens) {
            // this is the text of the token
            String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
            result.add(lemma);
        }

        return result;
    }

    public static List<String> ner(String text) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // run all Annotators on this text
        pipeline.annotate(document);
        List<CoreLabel> tokens = document.get(CoreAnnotations.TokensAnnotation.class);

        List<String> result = new ArrayList<String>();
        for (CoreLabel token : tokens) {
            // this is the text of the token
            String nerTag = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
            result.add(nerTag);
        }

        return result;
    }


    public static HashMap<String,List<String>> coreferenceMentions(String text) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, mention, coref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // run all Annotators on this text
        pipeline.annotate(document);


        HashMap<String,List<String>> resultBySentence = new HashMap<String,List<String>>();
        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            List<String> result = resultBySentence.get(sentence);
            if(result == null) {
                result = new ArrayList<String>();
            }

            for (Mention m : sentence.get(CorefCoreAnnotations.CorefMentionsAnnotation.class)) {
                result.add(m.toString());
            }

            resultBySentence.put(sentence.toString(),result);
        }

        return resultBySentence;
    }


    public  List<Tree> parse(String text) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // run all Annotators on this text
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        List<Tree> result = new ArrayList<Tree>();
        for (CoreMap sentence : sentences) {
            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
            result.add(tree);
        }

        return result;
    }


}
