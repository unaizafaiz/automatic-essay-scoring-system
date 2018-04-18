package com.nlp.autoscoring.coefAnalysis;

import java.util.HashMap;

public class CoefAnalysis {
    public String analyisCoef(HashMap<String, Float> lengthMarks, HashMap<String, Float> spellingMarks, HashMap<String, Float> agreementMarks, HashMap<String, Float> verbMissing, HashMap<String, Float> finalScoresNormalised){
        float lengthCoef = 2;
        float spellingCoef = -1;
        float agreementCoef = 1;
        float verbCoef = 1;
        for(String file: lengthMarks.keySet()){
            float iniResult = computeResult(lengthCoef,lengthMarks.get(file),spellingCoef,spellingMarks.get(file),agreementCoef,agreementMarks.get(file),verbCoef,verbMissing.get(file));
            if(iniResult > 5){
                iniResult -= 5;
                iniResult /= 40;
                lengthCoef -= iniResult;
                spellingCoef -= iniResult;
                agreementCoef -= iniResult;
                verbCoef -= iniResult;
            } else if(iniResult < 1) {
                iniResult += 1;
                iniResult /= 40;
                lengthCoef -= iniResult;
                spellingCoef -= iniResult;
                agreementCoef -= iniResult;
                verbCoef -= iniResult;
            }
        }
        return lengthCoef+" "+spellingCoef+" "+agreementCoef+" "+verbCoef;
    }

    private float computeResult(float lengthCoef, Float aFloat, float spellingCoef, Float aFloat1, float agreementCoef, Float aFloat2, float verbCoef, Float aFloat3) {
        return lengthCoef*aFloat + spellingCoef*aFloat1 + agreementCoef * aFloat2 + verbCoef * aFloat3;
    }

}
