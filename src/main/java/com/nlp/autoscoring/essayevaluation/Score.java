package com.nlp.autoscoring.essayevaluation;

import com.nlp.autoscoring.agreement.SentenceAgreement;
import com.nlp.autoscoring.coefAnalysis.CoefAnalysis;
import com.nlp.autoscoring.length.LengthOfEssay;
import com.nlp.autoscoring.preprocessing.Preprocessing;
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

        HashMap<String, Float> lengthMarks = new HashMap<>();
        HashMap<String, Float> spellingMarks = new HashMap<>();
        HashMap<String, Float> agreementMarks = new HashMap<>();
        HashMap<String, Float> verbMissing = new HashMap<>();
        HashMap<String, Float> finalScores = new HashMap<>();
        HashMap<String, Float> finalScoresNormalised = new HashMap<>();
        HashMap<String, String> grade = new HashMap<>();

        for(File file: files) {
            Preprocessing preprocessing = new Preprocessing();
            String fileContents = preprocessing.cleanFile(file);
            lengthMarks.put(file.getName(), (float) lengthOfEssay.findLengthOfEssay(fileContents)); // 1
           // scoreLength = scoreLength(length,averageHigh,averageLow);
            String[] mistake = spellingChecker.countSpellingMistakes(fileContents).split(" ");
            spellingMarks.put(file.getName(), (float) Integer.parseInt(mistake[0])); // 2
           // wordCount = Integer.parseInt(mistake[1]);
            String verbCounts = sentenceAgreement.countAgreementFailures(fileContents);
            String[] temp = verbCounts.split(" ");
            agreementMarks.put(file.getName(), (float) Integer.parseInt(temp[0])); // 3.1
            verbMissing.put(file.getName(), (float) Integer.parseInt(temp[1])); //3.2
        }

        String[] marksLength = minMaxFinder(lengthMarks).split(" ");
        String[] marksSpelling = minMaxFinder(spellingMarks).split(" ");
        String[] marksAgree = minMaxFinder(agreementMarks).split(" ");
        String[] marksVerb = minMaxFinder(verbMissing).split(" ");

        for(File file:files){
            lengthMarks.put(file.getName(), (4 * ((lengthMarks.get(file.getName()) - Float.parseFloat(marksLength[0]))/(Float.parseFloat(marksLength[1]) - Float.parseFloat(marksLength[0]))))+1);
            spellingMarks.put(file.getName(), 4 * ((spellingMarks.get(file.getName()) - Float.parseFloat(marksSpelling[0]))/(Float.parseFloat(marksSpelling[1]) - Float.parseFloat(marksSpelling[0]))));
            agreementMarks.put(file.getName(), 5 -(4 * ((agreementMarks.get(file.getName()) - Float.parseFloat(marksAgree[0]))/(Float.parseFloat(marksAgree[1]) - Float.parseFloat(marksAgree[0])))));
            verbMissing.put(file.getName(), 5 -(4 * ((verbMissing.get(file.getName()) - Float.parseFloat(marksVerb[0]))/(Float.parseFloat(marksVerb[1]) - Float.parseFloat(marksVerb[0])))));
            finalScores.put(file.getName(), (float) finalScoreCalculation(lengthMarks.get(file.getName()), spellingMarks.get(file.getName()), agreementMarks.get(file.getName()), verbMissing.get(file.getName())));
        }

         String[] minMaxScore = minMaxFinder(finalScores).split(" ");

         grade = finalGradCalculation(finalScoresNormalised, finalScores, minMaxScore, grade);

        System.out.println(grade);
        CoefAnalysis coefAnalysis = new CoefAnalysis();
        String newCoef = coefAnalysis.analyisCoef(lengthMarks, spellingMarks, agreementMarks, verbMissing, finalScoresNormalised);
        System.out.println(newCoef);




       /* Scanner scanner = null;
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
        scanner.close();*/

        try {
            PrintWriter writer = new PrintWriter("./output/result.txt","UTF-8");
           // PrintWriter scoreFile = new PrintWriter("./scores3.csv","UTF-8");
            for (File file: files){
                writer.println(file.getName()+"; "+lengthMarks.get(file.getName())+"; "+spellingMarks.get(file.getName())+"; "+agreementMarks.get(file.getName())+"; "+verbMissing.get(file.getName())+"; 0; 0; "+finalScores.get(file.getName())+"; "+grade.get(file.getName()));//+";"+fileGrades.get(file.getName()));
                //scoreFile.println(lengthMarks.get(file.getName())+"; "+spellingMarks.get(file.getName())+"; "+agreementMarks.get(file.getName())+"; "+verbMissing.get(file.getName())+"; 0; 0; "+finalScores.get(file.getName())+"; "+grade.get(file.getName()));

            }
            writer.close();
            //scoreFile.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private double finalScoreCalculation(Float aFloat, Float aFloat1, Float aFloat2, Float aFloat3) {
        return (2 * aFloat) - (aFloat1) + (aFloat2) + (aFloat3);
        // return (float) (0.16628 * aFloat - 0.16468 * aFloat1 - 0.31210 * (aFloat2) + 0.02660 * aFloat3);
        // return (float) (0.21941 * aFloat - -0.16694 * aFloat1 -0.24901 * (aFloat2) + 0.03138  * aFloat3);
        // return (1.0639055 * aFloat) - (1.9360945 * aFloat1) + (0.0639049 * aFloat2) + (0.0639049 * aFloat3);
    }

    private HashMap<String, String> finalGradCalculation(HashMap<String, Float> finalScoresNormalised, HashMap<String, Float> finalScores, String[] minMaxScore, HashMap<String, String> grade){
        float mean = 0;
        for(String file: finalScores.keySet()){
            finalScoresNormalised.put(file, 5 * ((finalScores.get(file) - Float.parseFloat(minMaxScore[0]))/(Float.parseFloat(minMaxScore[1]) - Float.parseFloat(minMaxScore[0]))));
            // finalScores.put(file.getName(), 5 * ((finalScores.get(file.getName()) - Float.parseFloat(minMaxScore[0]))/(Float.parseFloat(minMaxScore[1]) - Float.parseFloat(minMaxScore[0]))));
            mean += finalScoresNormalised.get(file);
        }
        mean /= finalScores.size();
        for(String file : finalScores.keySet()){
            if(finalScoresNormalised.get(file) < mean){
                grade.put(file, "LOW");
            } else {
                grade.put(file, "HIGH");
            }
        }
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

   /* private float scoreLength(float length, float averageHigh, float averageLow) {
        float score=1;

        if(length<10)
            score = 1;
        else {
            if (length>=averageHigh)
                score = 5;
            else if(length<averageHigh && length >= (averageHigh+averageLow)/2)
                score = 4;
            else if(length>=averageLow && length<(averageHigh+averageLow)/2)
                score = 3;
            else if(length<averageLow)
                score = 2;
        }
        return score;
    }*/



}