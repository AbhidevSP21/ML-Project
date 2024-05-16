package com.example;

import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.RemovePercentage;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;

import javax.swing.*;
import java.awt.*;

public class DecisionTree {

    public static Instances loadData(String path) throws Exception {
        DataSource source = new DataSource(path);
        Instances data = source.getDataSet();
        // Set the class index to the last column
        if (data.classIndex() == -1)
            data.setClassIndex(data.numAttributes() - 1);
        return data;
    }

    public static Instances[] splitData(Instances data, double trainSize) throws Exception {
        data.randomize(new java.util.Random());  // Randomize data before splitting

        // Use WEKA filters to split the data
        RemovePercentage removePercentage = new RemovePercentage();
        removePercentage.setPercentage(100 - trainSize);
        removePercentage.setInputFormat(data);
        Instances train = Filter.useFilter(data, removePercentage);

        removePercentage.setPercentage(trainSize);
        removePercentage.setInvertSelection(true);
        Instances test = Filter.useFilter(data, removePercentage);

        return new Instances[]{train, test};
    }

    public static J48 trainDecisionTree(Instances trainData) throws Exception {
        J48 tree = new J48();
        tree.setUnpruned(true); // Set unpruned to grow the tree to full depth
        tree.setMinNumObj(10); // Set minimum number of instances per leaf
        tree.buildClassifier(trainData);
        return tree;
    }

    public static void visualizeTree(J48 tree) throws Exception {
        // Visualize the tree
        TreeVisualizer tv = new TreeVisualizer(null, tree.graph(), new PlaceNode2());
        JFrame frame = new JFrame("Decision Tree Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.getContentPane().add(tv);
        frame.setVisible(true);

        // Adjust font size for better clarity
        tv.fitToScreen();
    }

    public static void main(String[] args) {
        try {
            Instances data = loadData("src/main/java/com/example/indiancrop_dataset.csv");
            Instances[] split = splitData(data, 70.0); // 70% for training, 30% for testing
            Instances trainData = split[0];
            Instances testData = split[1];

            J48 tree = trainDecisionTree(trainData);
            System.out.println(tree); // Outputs the learned decision tree

            // Evaluate model
            weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(trainData);
            eval.evaluateModel(tree, testData);
            System.out.println(eval.toSummaryString("\nResults\n======\n", false));

            // Print accuracy
            double accuracy = eval.pctCorrect();
            System.out.println("Accuracy: " + accuracy + "%");

            // Visualize tree
            visualizeTree(tree);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
