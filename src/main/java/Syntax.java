import edu.stanford.nlp.trees.Tree;
import javafx.util.Pair;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Syntax {

    public Syntax(){

    }

    public HashMap<Pair<String,String>, Integer> countTagTransitions(List<String> posTags, List<String> tokens){

        int countOfStartTags = 0;

            HashMap<Pair<String,String>, Integer> tagTransSet = new HashMap<>();
            for(String currentTag : posTags){
                Boolean firstIndex = true;
                for(String prevTag: posTags){
                    if(firstIndex){ //If the first word in corpus, marking beginning of sentence
                        tagTransSet.put(new Pair(currentTag,"<s>"),0);
                        firstIndex=false;
                    }
                    Pair<String, String> temp = new Pair<>(currentTag,prevTag);
                    if(currentTag.isEmpty()) {
                        continue;
                    }
                    if(!tagTransSet.containsKey(temp)) {
                        tagTransSet.put(temp,0);
                    }
                }
            }


            for(int index = 0; index < tokens.size(); index++) {
                String token = tokens.get(index);
                String currentTag = posTags.get(index);
                String prevTag = "";
                if(index!=0)
                    prevTag = posTags.get(index-1);

                //beginning of sentence tag; if first tag or
                // if tag is followed by a period or colon then consider as new sentence
                if (index==0 || prevTag.equals(".") || prevTag.equals(";")){
                    Pair<String, String> temp = new Pair<>(currentTag,"<s>");
                    tagTransSet.put(temp,tagTransSet.get(temp)+1);
                    countOfStartTags++;
                } else {
                    //update count for Tag-PreviousTag pair
                    Pair<String, String> temp = new Pair<>(currentTag,prevTag);
                    tagTransSet.put(temp,tagTransSet.get(temp)+1);

                }

            }

            return tagTransSet;

    }

    public void findMissingVerb(String fileContents){
            StanfordParser stp = new StanfordParser();
            List<String> tokens = stp.tokenize(fileContents);
            List<String> posTags = stp.posTagging(fileContents);
            List<Tree> trees = stp.parse(fileContents);

            /*for(Tree tree: trees){
                System.out.println(tree);
            }*/

            for(String posTag: posTags){
               // System.out.print("["+posTag+"] ");

            }
            HashMap<Pair<String,String>, Integer> countTagTrans = countTagTransitions(posTags,tokens);

        for (Map.Entry<Pair<String, String>, Integer> tagTrans : countTagTrans.entrySet()) {
            // Double observationProbability = (double) pair.getValue()/posTagSet.get(pair.getKey());
            System.out.println("["+tagTrans.getKey().getKey()+" | "+tagTrans.getKey().getValue()+"]"+"="+tagTrans.getValue());
        }


    }
}
