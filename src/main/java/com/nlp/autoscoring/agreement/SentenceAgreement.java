package com.nlp.autoscoring.agreement;


import com.google.common.collect.Sets;
import com.nlp.autoscoring.parser.StanfordParser;
import java.util.List;
import java.util.Set;

public class SentenceAgreement {

    private static final Set<String> singularPOS = Sets.newHashSet("NN", "NNP", "VBZ");

    private static final Set<String> pluralPOS = Sets.newHashSet("NNS", "NNPS", "VBP");

    private static final Set<String> verbTags = Sets.newHashSet("VB", "VBZ", "VBP", "VBD", "VBN", "VBG");

    public String countAgreementFailures(String file){
        int count = 0;
        int vCount = 0;
        List<String> sentences = StanfordParser.sentenceSplit(file);
        for(String sentence : sentences) {
            Set<String> posTag = Sets.newHashSet(StanfordParser.posTagging(sentence));
            boolean isVerb = Sets.intersection(verbTags, posTag).isEmpty();
            if(isVerb){
                vCount++;
            }
            boolean isSingular = !Sets.intersection(singularPOS, posTag).isEmpty() || sentence.contains("was");
            boolean isPlural = !Sets.intersection(pluralPOS, posTag).isEmpty() || sentence.contains("were");
            if (isPlural && isSingular){
                count++;
            }
        }
        /*System.out.println(count);
        System.out.println(vCount);
        System.out.println(sentences.size());*/

        return count+" "+vCount+" "+sentences.size();
    }
}
