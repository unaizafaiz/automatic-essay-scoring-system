package com.nlp.autoscoring.coefAnalysis;

import java.util.HashMap;

public class CoefAnalysis {
    public String analyisCoef(HashMap<String, Float> lengthMarks, HashMap<String, Float> spellingMarks, HashMap<String, Float> agreementMarks, HashMap<String, Float> verbMissing, HashMap<String, Float> finalScoresNormalised){
        double lengthCoef = 1.0639055;
        double spellingCoef = -1.9360945;
        double agreementCoef = 0.0639049;
        double verbCoef = 0.0639049;
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

    private float computeResult(double lengthCoef, Float aFloat, double spellingCoef, Float aFloat1, double agreementCoef, Float aFloat2, double verbCoef, Float aFloat3) {
        return (float) (lengthCoef*aFloat + spellingCoef*aFloat1 + agreementCoef * aFloat2 + verbCoef * aFloat3);
    }

}
