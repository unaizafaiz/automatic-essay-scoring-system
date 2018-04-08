package main;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Standford Parser that parses the input string and gives the list of tokens and POS Tags.
 */

public class StanfordParser {

    public List<String> tokenize(String text) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

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

    public List<String> posTagging(String text) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

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
}
