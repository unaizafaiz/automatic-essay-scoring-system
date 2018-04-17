package com.nlp.autoscoring.criteria;

import com.nlp.autoscoring.agreement.SentenceAgreement;
import com.nlp.autoscoring.length.LengthOfEssay;
import com.nlp.autoscoring.length.Preprocessing;
import com.nlp.autoscoring.spelling.SpellingChecker;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class Criteria {

    private PrintWriter trainingSet;
    float averageLow, averageHigh;
    float maxSpellingMistake = 25, minSpellingMistake = 0, mistakeTotal;
    float averageSpellingMistake;
    int maxagreement=28, minagreement=1, agreementTotal;
    float averageAgreement;
    int maxmissingbverb=3, minmissingbverb=0, missingbverbTotal;
    float averagemissingbverb;


//    public Criteria(){
//        try {
//            trainingSet = new PrintWriter("trainingSet.csv", "UTF-8");
//        } catch (FileNotFoundException | UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }

//    public float evaluatingTrainingSet(HashMap<String, String> fileGrades, String classType){
//
//        Preprocessing preprocessing = new Preprocessing();
//        LengthOfEssay lengthOfEssay = new LengthOfEssay();
//        SpellingChecker spellingChecker = new SpellingChecker();
//        SentenceAgreement sentenceAgreement = new SentenceAgreement();
//        float average,  total = 0;
//        float sum = 0;
//        float length;
//        String tempVerb;
//        int tSpellingMistake, tAgreementCount, tMissingVerb;
//
//
//        for (Map.Entry<String, String> fileGrade: fileGrades.entrySet()) {
//            if(fileGrade.getValue().equals(classType)){
//                String fileContents = preprocessing.cleanFile(new File("./src/main/resources/essays_dataset/essays/"+fileGrade.getKey()));
//                length = lengthOfEssay.findLengthOfEssay(fileContents);
//                sum += length;
//                total++;
//                String[] mistake = spellingChecker.countSpellingMistakes(fileContents).split(" ");
//                tSpellingMistake= Integer.parseInt(mistake[0]);
//                int wordCount = Integer.parseInt(mistake[1]);
//                String[] temp = sentenceAgreement.countAgreementFailures(fileContents).split(" ");
//                tAgreementCount = Integer.parseInt(temp[0]);
//                tMissingVerb = Integer.parseInt(temp[1]);
//                int sentenceCount = Integer.parseInt(temp[2]);
//              /*  scoreMistakes = findScore(spellingmistake,wordCount,"b");
//                scoreAgreement = findScore(agreement,sentenceCount, "c1");
//                scoreMissingVerb = findScore(missingVerb,sentenceCount,"c2");*/
//                trainingSet.println(fileGrade.getKey()+";"+length+";"+tSpellingMistake+";"+tAgreementCount+";"+tMissingVerb);
//            }
//        }
//        if(total!=0)
//            average = sum/total;
//        else
//            average = 0;
//
//        return average;
//    }


    public void findCriteriaAndScore(File[] files){
        LengthOfEssay lengthOfEssay = new LengthOfEssay();
        SpellingChecker spellingChecker = new SpellingChecker();
        SentenceAgreement sentenceAgreement = new SentenceAgreement();

        /*Scanner scanner = null;
        HashMap<String, String> fileGrades = new HashMap<>();

        //reading essay file name and grade from the csv file and saving in a hash map
        try {
            scanner = new Scanner(new File("./src/main/resources/essays_dataset/index.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (scanner.hasNext()) {
            String newLine = scanner.nextLine();
            String[] fileDetails = newLine.split(";");
            fileGrades.put(fileDetails[0],fileDetails[2]);
        }
        scanner.close();*/

        //getting average scores for all low and high essays in the training corpus
      //  averageLow = Math.round(evaluatingTrainingSet(fileGrades, "low"));
       // averageHigh = Math.round(evaluatingTrainingSet(fileGrades, "high"));
       // trainingSet.close();

        float length;
        float scoreLength, scoreMistakes,scoreAgreement,scoreMissingVerb;
        int spellingmistake;
        int agreement, missingVerb;
        float finalScore=0;
        int wordCount,sentenceCount;

       // System.out.println("Average - low: "+averageLow+" | high - "+averageHigh);
        /*PrintWriter writer = null;
        try {
            writer = new PrintWriter("essaygrades.csv", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/

        HashMap<String, Float> lengthMarks = new HashMap<>();
        HashMap<String, Float> spellingMarks = new HashMap<>();
        HashMap<String, Float> agreementMarks = new HashMap<>();
        HashMap<String, Float> verbMissing = new HashMap<>();
        HashMap<String, Float> finalScores = new HashMap<>();
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
            //sentenceCount = Integer.parseInt(temp[2]);
            //scoreMistakes = findScore(spellingmistake,wordCount,"b");
           // scoreAgreement = findScore(agreement,sentenceCount, "c1");
            //scoreMissingVerb = findScore(missingVerb,sentenceCount,"c2");
            //String finalGrade = "L|H";
           // writer.println(file.getName()+";"+scoreLength+";"+scoreMistakes+";"+scoreAgreement+";"+scoreMissingVerb+";0;0;"+finalScore+";"+finalGrade);
        }
        // writer.close();

        String[] marksLength = minMaxFinder(lengthMarks).split(" ");
        String[] marksSpelling = minMaxFinder(spellingMarks).split(" ");
        String[] marksAgree = minMaxFinder(agreementMarks).split(" ");
        String[] marksVerb = minMaxFinder(verbMissing).split(" ");

        for(File file:files){
            lengthMarks.put(file.getName(), 5 * ((lengthMarks.get(file.getName()) - Float.parseFloat(marksLength[0]))/(Float.parseFloat(marksLength[1]) - Float.parseFloat(marksLength[0]))));
            spellingMarks.put(file.getName(), 4 * ((spellingMarks.get(file.getName()) - Float.parseFloat(marksSpelling[0]))/(Float.parseFloat(marksSpelling[1]) - Float.parseFloat(marksSpelling[0]))));
            agreementMarks.put(file.getName(), 5 -(5 * ((agreementMarks.get(file.getName()) - Float.parseFloat(marksAgree[0]))/(Float.parseFloat(marksAgree[1]) - Float.parseFloat(marksAgree[0])))));
            verbMissing.put(file.getName(), 5 -(5 * ((verbMissing.get(file.getName()) - Float.parseFloat(marksVerb[0]))/(Float.parseFloat(marksVerb[1]) - Float.parseFloat(marksVerb[0])))));
            finalScores.put(file.getName(),finalScoreCalculation(lengthMarks.get(file.getName()), spellingMarks.get(file.getName()), agreementMarks.get(file.getName()), verbMissing.get(file.getName())));
        }

        String[] minMaxScore = minMaxFinder(finalScores).split(" ");

        for(File file:files){
            finalScores.put(file.getName(), 5 * ((finalScores.get(file.getName()) - Float.parseFloat(minMaxScore[0]))/(Float.parseFloat(minMaxScore[1]) - Float.parseFloat(minMaxScore[0]))));
            if(finalScores.get(file.getName()) < 3){
                grade.put(file.getName(), "LOW");
            } else {
                grade.put(file.getName(), "HIGH");
            }
        }
        System.out.println(grade);
    }

    private Float finalScoreCalculation(Float aFloat, Float aFloat1, Float aFloat2, Float aFloat3) {
        return (2 * aFloat) - (aFloat1) + (aFloat2) + (aFloat3);
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

//    private float scoreLength(float length, float averageHigh, float averageLow) {
//        float score=1;
//
//        if(length<10)
//            score = 1;
//        else {
//            if (length>=averageHigh)
//                score = 5;
//            else if(length<averageHigh && length >= (averageHigh+averageLow)/2)
//                score = 4;
//            else if(length>=averageLow && length<(averageHigh+averageLow)/2)
//                score = 3;
//            else if(length<averageLow)
//                score = 2;
//        }
//        return score;
//    }

    public float findScore(int value, int min, int max, String criteria){
        float score = (value - min)/ (float) (max-min);
       /* if(criteria.equals("b")){

        }
        else{
            temp = (temp * 4)+1;
        }*/

        return score;
    }


}