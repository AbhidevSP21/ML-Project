package com.example;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.trees.J48;
import weka.classifiers.Evaluation;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.Randomize;
import weka.filters.unsupervised.instance.RemovePercentage;

import java.util.Random;

public class ClassifierEvaluation {

    public static Instances loadData(String path) throws Exception {
        DataSource source = new DataSource(path);
        Instances data = source.getDataSet();
        if (data.classIndex() == -1)
            data.setClassIndex(data.numAttributes() - 1);
        return data;
    }

    public static Instances[] splitData(Instances data, double trainSize, long seed) throws Exception {
        Randomize rand = new Randomize();
        rand.setRandomSeed((int) seed);
        rand.setInputFormat(data);
        data = Filter.useFilter(data, rand);

        RemovePercentage removePercentage = new RemovePercentage();
        removePercentage.setPercentage(100 - trainSize);
        removePercentage.setInputFormat(data);
        Instances train = Filter.useFilter(data, removePercentage);

        removePercentage.setInvertSelection(true);
        Instances test = Filter.useFilter(data, removePercentage);

        return new Instances[]{train, test};
    }

    public static J48 trainDecisionTree(Instances trainData) throws Exception {
        J48 model = new J48(); // new instance of tree
        model.buildClassifier(trainData); // build classifier
        return model;
    }

    public static void evaluateClassifier(J48 model, Instances trainData, Instances testData) throws Exception {
        Evaluation eval = new Evaluation(trainData);
        eval.evaluateModel(model, testData);

        System.out.println(eval.toSummaryString("\nResults\n======\n", false));
        System.out.println(eval.toClassDetailsString());
        System.out.println("=== Confusion Matrix ===");
        double[][] cmMatrix = eval.confusionMatrix();
        for (double[] row : cmMatrix) {
            for (double element : row) {
                System.out.print((int) element + "\t");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        try {
            Instances data = loadData("src/main/java/com/example/indiancrop_dataset.csv");
            Instances[] split = splitData(data, 70.0, 1); // 70% training, 30% testing, seed = 1
            J48 tree = trainDecisionTree(split[0]); // Train with 70% of data
            evaluateClassifier(tree, split[0], split[1]); // Evaluate with 30% of data
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
