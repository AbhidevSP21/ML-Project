package com.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class CAC2 {
    private static final int COLUMN_COUNT = 5; // Set according to the actual number of columns in your dataset
    private static final int HEAD_COUNT = 5; // Number of rows to display in the head

    public static void main(String[] args) {
        List<List<String>> data = new ArrayList<>();

        try {
            File file = new File("src\\main\\java\\com\\example\\indiancrop_dataset.csv");
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] values = line.split(",");
                    data.add(Arrays.asList(values));
                }
            }

            if (!data.isEmpty()) {
                printHead(data);
            }

            describeData(data);
            checkNullValues(data);

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
    }

    private static void printHead(List<List<String>> data) {
        System.out.println("Data Head:");
        for (int i = 0; i < Math.min(HEAD_COUNT, data.size()); i++) {
            System.out.println(data.get(i));
        }
        System.out.println(); // Add a blank line for better readability
    }

    private static void describeData(List<List<String>> data) {
        System.out.println("Summary Statistics:");
        for (int i = 0; i < COLUMN_COUNT; i++) {
            List<Double> numericalValues = new ArrayList<>();
            for (List<String> row : data) {
                String value = row.get(i).trim();
                if (!value.isEmpty() && value.matches("[-+]?\\d*\\.?\\d+")) {
                    numericalValues.add(Double.parseDouble(value));
                }
            }
            if (!numericalValues.isEmpty()) {
                System.out.printf("Column %d: Mean = %.2f, Min = %.2f, Max = %.2f, Std Dev = %.2f\n",
                        i, calculateMean(numericalValues), Collections.min(numericalValues),
                        Collections.max(numericalValues), calculateStdDev(numericalValues, calculateMean(numericalValues)));
            }
        }
        System.out.println(); // Add a blank line for better readability
    }

    private static double calculateMean(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);
    }

    private static double calculateStdDev(List<Double> values, double mean) {
        double sum = values.stream().mapToDouble(v -> Math.pow(v - mean, 2)).sum();
        return Math.sqrt(sum / values.size());
    }

    private static void checkNullValues(List<List<String>> data) {
        System.out.println("Null Values Count:");
        int[] nullCounts = new int[COLUMN_COUNT];
        for (List<String> row : data) {
            for (int i = 0; i < COLUMN_COUNT; i++) {
                if (row.get(i).trim().isEmpty()) {
                    nullCounts[i]++;
                }
            }
        }
        for (int i = 0; i < nullCounts.length; i++) {
            System.out.printf("Column %d: %d nulls\n", i, nullCounts[i]);
        }
        System.out.println(); // Add a blank line for better readability
    }
}
