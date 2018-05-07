scoreTable <- read.csv(file = "/Users/unaizafaiz/Documents/UIC/Spring2017/NLP/Project/Automatic-Scoring-System/essayscores.csv", header = FALSE , sep = ";", col.names = c("Filename","L","Sp","Agr","MV","C3","C4","FS", "NormalisedScore", "Grade","ActualGrade"));
attach(scoreTable)
str(scoreTable$ActualGrade)
gradeScore <- as.numeric(ActualGrade == "high")+1
gradeScore
model <- lm(gradeScore ~ L+Sp+Agr+MV)
summary(model)
plot(model)


