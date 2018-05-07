package com.nlp.autoscoring.essayevaluation;

import com.nlp.autoscoring.agreement.SentenceAgreement;
import com.nlp.autoscoring.coherence.TopicCoherence;
import com.nlp.autoscoring.coherence.TextCoherence;
import com.nlp.autoscoring.length.LengthOfEssay;
import com.nlp.autoscoring.parser.StanfordParser;
import com.nlp.autoscoring.preprocessing.Preprocessing;
import com.nlp.autoscoring.sentenceformation.SentenceFormation;
import com.nlp.autoscoring.spelling.SpellingChecker;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.trees.Tree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class Score {


    /**
     * Finding scores for all the feature values for each essay and saving the grades into a result.txt file
     * @param files
     */
    public void findCriteriaAndScore(File[] files){
        LengthOfEssay lengthOfEssay = new LengthOfEssay();
        SpellingChecker spellingChecker = new SpellingChecker();
        SentenceAgreement sentenceAgreement = new SentenceAgreement();
        SentenceFormation sentenceFormation =  new SentenceFormation();
        TextCoherence textCoherence = new TextCoherence();
        TopicCoherence topicCoher = new TopicCoherence();

        //Declaring HashMap for all the feature vectors
        HashMap<String, Float> lengthMarks = new HashMap<>();
        HashMap<String, Float> spellingMarks = new HashMap<>();
        HashMap<String, Float> agreementMarks = new HashMap<>();
        HashMap<String, Float> verbMissing = new HashMap<>();
        HashMap<String, Float> sentenceForming = new HashMap<>();
        HashMap<String, Float> coherencyMarks = new HashMap<>();
        HashMap<String, Float> topicCoherence = new HashMap<>();
        HashMap<String, Float> finalScores = new HashMap<>();
        HashMap<String, Float> finalScoresNormalised = new HashMap<>();
        HashMap<String, String> grade = new HashMap<>();

        for(File file: files) {
            //Cleaning the files
            String fileContents="";
            Preprocessing preprocessing = new Preprocessing();
            fileContents = preprocessing.cleanFile(file);

            //Calling parser to get tokens, parse tree, POS tags, lemmas and sentences
            List<String> tokens = StanfordParser.tokenize(fileContents);
            List<String> posTags = StanfordParser.posTagging(fileContents);
            List<String> sentences = StanfordParser.sentenceSplit(fileContents);
            List<String> lemma = StanfordParser.lemmatize(fileContents);
            List<Tree> trees = StanfordParser.parse(fileContents);
            Map<Integer, CorefChain> corefText = StanfordParser.coreferenceResolution(fileContents);



            //Getting the feature values for each essay
            lengthMarks.put(file.getName(), (float) lengthOfEssay.findLengthOfEssay(fileContents, trees)); // 1
            String[] mistake = spellingChecker.countSpellingMistakes(fileContents,tokens,posTags).split(" ");
            spellingMarks.put(file.getName(), (float) Integer.parseInt(mistake[0])); // 2
            String verbCounts = sentenceAgreement.countAgreementFailures(fileContents, sentences);
            String[] temp = verbCounts.split(" ");
            agreementMarks.put(file.getName(), (float) Integer.parseInt(temp[0])); // 3.1
            verbMissing.put(file.getName(), (float) Integer.parseInt(temp[1])); //3.2
            sentenceForming.put(file.getName(), sentenceFormation.countOfFragments(fileContents,sentences)); //3.3
            coherencyMarks.put(file.getName(), textCoherence.checkCoherency(fileContents, corefText)); //4.1
            topicCoherence.put(file.getName(),topicCoher.checkforcoherence(file,fileContents, posTags, lemma));//4.2
        }

        //Finding minumum and maximum of the feature values
        String[] marksLength = minMaxFinder(lengthMarks).split(" ");
        String[] marksSpelling = minMaxFinder(spellingMarks).split(" ");
        String[] marksAgree = minMaxFinder(agreementMarks).split(" ");
        String[] marksVerb = minMaxFinder(verbMissing).split(" ");
        String[] marksSentence = minMaxFinder(sentenceForming).split(" ");
        String[] marksCoherency = minMaxFinder(coherencyMarks).split(" ");
        String[] marksTopic = minMaxFinder(topicCoherence).split(" ");

        //normalising the feature values and calculating final score
        for(File file:files){
            lengthMarks.put(file.getName(), (4 * ((lengthMarks.get(file.getName()) - Float.parseFloat(marksLength[0]))/(Float.parseFloat(marksLength[1]) - Float.parseFloat(marksLength[0]))))+1);
            spellingMarks.put(file.getName(), 4 * ((spellingMarks.get(file.getName()) - Float.parseFloat(marksSpelling[0]))/(Float.parseFloat(marksSpelling[1]) - Float.parseFloat(marksSpelling[0]))));
            agreementMarks.put(file.getName(), (4 * ((agreementMarks.get(file.getName()) - Float.parseFloat(marksAgree[0]))/(Float.parseFloat(marksAgree[1]) - Float.parseFloat(marksAgree[0]))))+1);
            verbMissing.put(file.getName(),  5 - (4 * ((verbMissing.get(file.getName()) - Float.parseFloat(marksVerb[0]))/(Float.parseFloat(marksVerb[1]) - Float.parseFloat(marksVerb[0])))));
            sentenceForming.put(file.getName(), 5 - (4 * ((sentenceForming.get(file.getName()) - Float.parseFloat(marksVerb[0]))/(Float.parseFloat(marksSentence[1]) - Float.parseFloat(marksSentence[0])))));
            coherencyMarks.put(file.getName(),  5 - (4 * ((coherencyMarks.get(file.getName()) - Float.parseFloat(marksCoherency[0]))/(Float.parseFloat(marksCoherency[1]) - Float.parseFloat(marksCoherency[0])))));
            topicCoherence.put(file.getName(), (4 * ((topicCoherence.get(file.getName()) - Float.parseFloat(marksTopic[0]))/(Float.parseFloat(marksTopic[1]) - Float.parseFloat(marksTopic[0]))))+1);
            finalScores.put(file.getName(), (float) finalScoreCalculation(lengthMarks.get(file.getName()), spellingMarks.get(file.getName()), agreementMarks.get(file.getName()), verbMissing.get(file.getName()), sentenceForming.get(file.getName()), coherencyMarks.get(file.getName()), topicCoherence.get(file.getName())));
        }


         String[] minMaxScore = minMaxFinder(finalScores).split(" ");

        // Assigning HIGH or LOW for the essay
         grade = finalGradCalculation(finalScoresNormalised, finalScores, minMaxScore, grade);

      /* System.out.println(grade);
        CoefAnalysis coefAnalysis = new CoefAnalysis();
        String newCoef = coefAnalysis.analyisCoef(lengthMarks, spellingMarks, agreementMarks, verbMissing, finalScoresNormalised);
        System.out.println(newCoef);*/


       HashMap<String, String> fileGrades = getExpectedGrades();

       // Writing output in result.txt file
        try {
            PrintWriter writer = new PrintWriter("./output/result.txt","UTF-8");
            PrintWriter scoreEssay = new PrintWriter("./essayscores.csv","UTF-8");
            for (File file: files){
                writer.println(file.getName()+";"+lengthMarks.get(file.getName())+";"+spellingMarks.get(file.getName())+";"+agreementMarks.get(file.getName())+";"+verbMissing.get(file.getName())+";"+sentenceForming.get(file.getName())+";"+coherencyMarks.get(file.getName()) +";"+topicCoherence.get(file.getName())+";"+finalScores.get(file.getName())+";"+grade.get(file.getName()));
                scoreEssay.println(file.getName()+"; "+lengthMarks.get(file.getName())+"; "+spellingMarks.get(file.getName())+"; "+agreementMarks.get(file.getName())+"; "+verbMissing.get(file.getName())+";"+sentenceForming.get(file.getName())+"; "+coherencyMarks.get(file.getName()) +"; "+topicCoherence.get(file.getName())+"; "+finalScores.get(file.getName())+"; "+finalScoresNormalised.get(file.getName())+"; "+grade.get(file.getName())+";"+fileGrades.get(file.getName()));

                //scoreFile.println(lengthMarks.get(file.getName())+"; "+spellingMarks.get(file.getName())+"; "+agreementMarks.get(file.getName())+"; "+verbMissing.get(file.getName())+"; 0; 0; "+finalScores.get(file.getName())+"; "+grade.get(file.getName()));
            }
            writer.close();
            scoreEssay.close();
            //scoreFile.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    /**
     * Gets expected grade from index.csv file
     * @return HashMap<String, String>
     */
    private HashMap<String, String> getExpectedGrades() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("./input/testing/index.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        HashMap<String, String> fileGrades = new HashMap<>();

        assert scanner != null;
        int count=0;
        while (scanner.hasNext()) {
            if(count==0) {
                count++;
            }else {
                String newLine = scanner.nextLine();
                String[] fileDetails = newLine.split(";");
                fileGrades.put(fileDetails[0], fileDetails[2]);
            }
        }
        scanner.close();
        return fileGrades;
    }

    /**
     * Calculates final score
     *
     * @param aFloat Length of the file
     * @param aFloat1 Spelling mistake score
     * @param aFloat2 Agreement
     * @param aFloat3 Missing verb
     * @param aFloat4 Sentence formation
     * @param aFloat5 Text coherence
     * @param aFloat6 Topic coherence
     * @return final score
     */
    private double finalScoreCalculation(Float aFloat, Float aFloat1, Float aFloat2, Float aFloat3, Float aFloat4, Float aFloat5, Float aFloat6) {
        // return (2 * aFloat) - (aFloat1) + (aFloat2) + (0*aFloat3) + (2 * aFloat4) + (2 * aFloat5) + (3 * aFloat6);
        // return (1.0639055 * aFloat) - (1.9360945 * aFloat1) + (0.0639049 * aFloat2) + (0.0639049 * aFloat3);
         return ((  0.27720  * aFloat) - (0.09944 * aFloat1) + (0.17453 * aFloat2) - (0.01569  * aFloat3) + (0.03670 *aFloat4) - (0.08948 * aFloat5) + (0.11589 * aFloat6));
        // return ((  0.21431  * aFloat) - (0.04117 * aFloat1) + (0.19750 * aFloat2) - (0.03012  * aFloat3) + (0.04554 *aFloat4) - (0.08718 * aFloat5) + (0.20009 * aFloat6));
    }

    /**
     * Assign grade low or high by calculating the mean
     */
    private HashMap<String, String> finalGradCalculation(HashMap<String, Float> finalScoresNormalised, HashMap<String, Float> finalScores, String[] minMaxScore, HashMap<String, String> grade){
        float mean = 0;
        for(String file: finalScores.keySet()){
            finalScoresNormalised.put(file, 5 * ((finalScores.get(file) - Float.parseFloat(minMaxScore[0]))/(Float.parseFloat(minMaxScore[1]) - Float.parseFloat(minMaxScore[0]))));
            /*if(finalScoresNormalised.get(file) <= 3){
                grade.put(file, "LOW");
            } else {
                grade.put(file, "HIGH");
            }*/

            mean += finalScoresNormalised.get(file);
        }
            mean /= finalScores.size();
            for(String file : finalScores.keySet()){
                if(finalScoresNormalised.get(file) <= mean){
                    grade.put(file, "LOW");
                } else {
                    grade.put(file, "HIGH");
                }
            }
        return grade;
    }


    /**
     * Finding min or max value to normalise the scores
     * @param score
     * @return
     */
    private String minMaxFinder(HashMap<String, Float> score) {
        float min = 10000, max = 0;
        for(String a:score.keySet()){
            if(min > score.get(a)){
                min = score.get(a);
            }
            if(max < score.get(a)){
                max = score.get(a);
            }
        }
        return min+" "+max;
    }


}