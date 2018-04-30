package com.nlp.autoscoring.essayevaluation;

import com.nlp.autoscoring.agreement.SentenceAgreement;
import com.nlp.autoscoring.coherence.TopicCoherence;
import com.nlp.autoscoring.coherence.TextCoherence;
import com.nlp.autoscoring.length.LengthOfEssay;
import com.nlp.autoscoring.parser.StanfordParser;
import com.nlp.autoscoring.preprocessing.Preprocessing;
import com.nlp.autoscoring.sentenceformation.SentenceFormation;
import com.nlp.autoscoring.spelling.SpellingChecker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class Score {


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

            //Getting the feature values for each essay
            lengthMarks.put(file.getName(), (float) lengthOfEssay.findLengthOfEssay(fileContents)); // 1
            String[] mistake = spellingChecker.countSpellingMistakes(fileContents).split(" ");
            spellingMarks.put(file.getName(), (float) Integer.parseInt(mistake[0])); // 2
            String verbCounts = sentenceAgreement.countAgreementFailures(fileContents);
            String[] temp = verbCounts.split(" ");
            agreementMarks.put(file.getName(), (float) Integer.parseInt(temp[0])); // 3.1
            verbMissing.put(file.getName(), (float) Integer.parseInt(temp[1])); //3.2
            sentenceForming.put(file.getName(), sentenceFormation.countOfFragments(fileContents)); //3.3
            coherencyMarks.put(file.getName(), textCoherence.checkCoherency(fileContents, StanfordParser.coreferenceResolution(fileContents))); //4.1
            topicCoherence.put(file.getName(),topicCoher.checkforcoherence(file,fileContents));//4.2
        }

        //Finding minumum and maximum of the feature values
        String[] marksLength = minMaxFinder(lengthMarks).split(" ");
        String[] marksSpelling = minMaxFinder(spellingMarks).split(" ");
        String[] marksAgree = minMaxFinder(agreementMarks).split(" ");
        String[] marksVerb = minMaxFinder(verbMissing).split(" ");
        String[] marksSentence = minMaxFinder(sentenceForming).split(" ");
        String[] marksCoherency = minMaxFinder(coherencyMarks).split(" ");
        String[] marksTopic = minMaxFinder(topicCoherence).split(" ");

        //normalizing the feature values
        for(File file:files){
            lengthMarks.put(file.getName(), (4 * ((lengthMarks.get(file.getName()) - Float.parseFloat(marksLength[0]))/(Float.parseFloat(marksLength[1]) - Float.parseFloat(marksLength[0]))))+1);
            spellingMarks.put(file.getName(), 4 * ((spellingMarks.get(file.getName()) - Float.parseFloat(marksSpelling[0]))/(Float.parseFloat(marksSpelling[1]) - Float.parseFloat(marksSpelling[0]))));
            agreementMarks.put(file.getName(), (4 * ((agreementMarks.get(file.getName()) - Float.parseFloat(marksAgree[0]))/(Float.parseFloat(marksAgree[1]) - Float.parseFloat(marksAgree[0]))))+1);
            verbMissing.put(file.getName(), 5 - (4 * ((verbMissing.get(file.getName()) - Float.parseFloat(marksVerb[0]))/(Float.parseFloat(marksVerb[1]) - Float.parseFloat(marksVerb[0])))));
            sentenceForming.put(file.getName(), 5 - (4 * ((sentenceForming.get(file.getName()) - Float.parseFloat(marksVerb[0]))/(Float.parseFloat(marksSentence[1]) - Float.parseFloat(marksSentence[0])))));
            coherencyMarks.put(file.getName(), 5 - (4 * ((coherencyMarks.get(file.getName()) - Float.parseFloat(marksCoherency[0]))/(Float.parseFloat(marksCoherency[1]) - Float.parseFloat(marksCoherency[0])))));
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

    private HashMap<String, String> getExpectedGrades() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("./input/testing/index.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        HashMap<String, String> fileGrades = new HashMap<>();

        assert scanner != null;
        while (scanner.hasNext()) {
            String newLine = scanner.nextLine();
            String[] fileDetails = newLine.split(";");
            fileGrades.put(fileDetails[0],fileDetails[2]);
        }
        scanner.close();
        return fileGrades;
    }

    private double finalScoreCalculation(Float aFloat, Float aFloat1, Float aFloat2, Float aFloat3, Float aFloat4, Float aFloat5, Float aFloat6) {
         return (2 * aFloat) - (aFloat1) + (aFloat2) + (aFloat3) + (2 * aFloat4) + (2*aFloat5) + (3*aFloat6);
        // return (1.0639055 * aFloat) - (1.9360945 * aFloat1) + (0.0639049 * aFloat2) + (0.0639049 * aFloat3);
        // return (float) ( 0.27309  * aFloat -0.14183 * aFloat1 + 0.21198 * (aFloat2) + 0.02764  * aFloat3);
        // return (float) ( 0.22131  * aFloat -0.15951 * aFloat1 + 0.27341 * (aFloat2) + 0.01245  * aFloat3);
    }

    private HashMap<String, String> finalGradCalculation(HashMap<String, Float> finalScoresNormalised, HashMap<String, Float> finalScores, String[] minMaxScore, HashMap<String, String> grade){
        float mean = 0;
        for(String file: finalScores.keySet()){
            finalScoresNormalised.put(file, 5 * ((finalScores.get(file) - Float.parseFloat(minMaxScore[0]))/(Float.parseFloat(minMaxScore[1]) - Float.parseFloat(minMaxScore[0]))));
            if(finalScoresNormalised.get(file) <= 3.25){
                grade.put(file, "LOW");
            } else {
                grade.put(file, "HIGH");
            }

//            mean += finalScoresNormalised.get(file);
        }
//        mean /= finalScores.size();
//        for(String file : finalScores.keySet()){
//            if(finalScoresNormalised.get(file) <= mean){
//                grade.put(file, "LOW");
//            } else {
//                grade.put(file, "HIGH");
//            }
//        }
        return grade;
    }


    private String minMaxFinder(HashMap<String, Float> lengthMarks) {
        float min = 10000, max = 0;
        for(String a:lengthMarks.keySet()){
            if(min > lengthMarks.get(a)){
                min = lengthMarks.get(a);
            }
            if(max < lengthMarks.get(a)){
                max = lengthMarks.get(a);
            }
        }
        return min+" "+max;
    }


}