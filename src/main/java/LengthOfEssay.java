import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class LengthOfEssay {
    public float averageLength(HashMap<String, String> fileGrades, String classType){

        float average,  total = 0;
        float sum = 0;
        for (Map.Entry<String, String> fileGrade: fileGrades.entrySet()) {
            if(fileGrade.getValue().equals(classType)){
                sum += findLengthOfEssay(new Preprocessing().cleanFile(new File("./essays/"+fileGrade.getKey())));
                total++;
            }
        }
        if(total!=0)
            average = sum/total;
        else
            average = 0;

       return average;
    }



    public int findLengthOfEssay(String fileContents){
        StanfordParser stp = new StanfordParser();
        int countSentences = 0;
        List<Tree> trees = stp.parse(fileContents);

        /*String str = trees.toString();
        System.out.println(str);
        String[] splitStr = str.split(" ");
        for (int i = 0; i<splitStr.length;i++) {
            if (splitStr[i].equals("(S") && !splitStr[i+1].equals("(S")) {
               countSentences++;
            } else if(splitStr[i].equals("(SINV")||splitStr[i].equals("(SBARQ"))
                countSentences++;
        }*/

        for (Tree tree : trees) {
                String strTree = tree.toString();
                strTree = strTree.replace('(', '[');
                strTree = strTree.replace(')', ']');
                //System.out.println(strTree);
               // countSentences += findOccurence(strTree);

          //  int count=0;

            if(strTree.contains("[S")) {
                countSentences++;

                String[] splitStr = strTree.split(" ");

                Map<String, Integer> tagCount = new HashMap<>();
                tagCount.put("[VBP", 0);
                tagCount.put("[CC", 0);
                for (int i = 0; i < splitStr.length; i++) {
                    if (splitStr[i].equals("[VBP") ) {
                        if (tagCount.containsKey(splitStr[i])) {
                            // Map already contains the word key. Just increment it's count by 1
                            tagCount.put(splitStr[i], tagCount.get(splitStr[i]) + 1);
                        } else {
                            // Map doesn't have mapping for word. Add one with count = 1
                            tagCount.put(splitStr[i], 1);
                        }
                    }
                    if(i<splitStr.length-2){
                        if(splitStr[i].equals("[CC") && splitStr[i+2].equals("[S")){
                            tagCount.put(splitStr[i], tagCount.get(splitStr[i]) + 1);
                        }
                    }
                }


                  if (tagCount.get("[VBP") > tagCount.get("[CC")) {

                        if (tagCount.containsKey("[VBP"))
                            if (tagCount.get("[VBP") > 1) {
                                countSentences += tagCount.get("[VBP") - 1;
                            }
                    } else {
                        if (tagCount.containsKey("[CC"))
                            countSentences += tagCount.get("[CC");
                    }
            }

        }


       // System.out.println(countSentences);
        return countSentences;
    }

    private int findOccurence(String strTree) {
        int count=0;

        if(strTree.contains("[S")) {
            count++;

            String[] splitStr = strTree.split(" ");

            Map<String, Integer> tagCount = new HashMap<>();
            for (int i = 0; i < splitStr.length; i++) {
                if (splitStr[i].equals("[VBP") || splitStr[i].equals("[CC")) {
                    if (tagCount.containsKey(splitStr[i])) {
                        // Map already contains the word key. Just increment it's count by 1
                        tagCount.put(splitStr[i], tagCount.get(splitStr[i]) + 1);
                    } else {
                        // Map doesn't have mapping for word. Add one with count = 1
                        tagCount.put(splitStr[i], 1);
                    }
                }
            }

            if (tagCount.containsKey("[VBP") || tagCount.containsKey("[CC"))
                if(tagCount.get("[VBP")>1) {
                    count += tagCount.get("[VBP") - 1;
                }
                count +=tagCount.get("[CC");
        }

        return count;
    }

    public static void main(String[] args){
        LengthOfEssay lengthOfEssay = new LengthOfEssay();
        //String cleanedContent = lengthOfEssay.cleanFile(new File("./essays/38209.txt"));
        //System.out.println(lengthOfEssay.findLengthOfEssay(new File("./essays/1004355.txt")));
       // System.out.println(lengthOfEssay.findLengthOfEssay(new File("./0test.txt")));
    }
}


