package com.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Visualizations {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Soil Analysis and Crop Price Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(2, 2)); // Adjust grid layout depending on number of plots
        frame.getContentPane().setBackground(new Color(240, 240, 240)); // Set background color

        // Load dataset
        String filePath = "src\\main\\java\\com\\example\\indiancrop_dataset.csv";

        // Create each panel
        frame.add(createBoxPlotPanel(filePath));
        frame.add(createHistogramPanel(filePath));
        frame.add(createScatterPlotPanel(filePath, "TEMPERATURE", "CROP_PRICE"));

        frame.pack();
        frame.setVisible(true);
    }

    private static ChartPanel createBoxPlotPanel(String filePath) {
        DefaultBoxAndWhiskerCategoryDataset dataset = loadBoxPlotDataset(filePath);
        JFreeChart chart = ChartFactory.createBoxAndWhiskerChart(
                "Box Plot of Soil Nutrient Levels", "Soil Nutrients", "Levels",
                dataset, true);
        chart.setBackgroundPaint(new Color(240, 240, 240)); // Set plot background color
        chart.getCategoryPlot().setBackgroundPaint(Color.white); // Set plot area background color

        return new ChartPanel(chart);
    }

    private static DefaultBoxAndWhiskerCategoryDataset loadBoxPlotDataset(String filePath) {
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        try (Scanner scanner = new Scanner(new File(filePath))) {
            scanner.nextLine(); // Skip header
            List<Double> nSoilValues = new ArrayList<>();
            List<Double> pSoilValues = new ArrayList<>();
            List<Double> kSoilValues = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String[] values = scanner.nextLine().split(",");
                nSoilValues.add(Double.parseDouble(values[0]));
                pSoilValues.add(Double.parseDouble(values[1]));
                kSoilValues.add(Double.parseDouble(values[2]));
            }
            dataset.add(nSoilValues, "N_SOIL", "");
            dataset.add(pSoilValues, "P_SOIL", "");
            dataset.add(kSoilValues, "K_SOIL", "");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return dataset;
    }

    private static ChartPanel createHistogramPanel(String filePath) {
        HistogramDataset dataset = new HistogramDataset();
        double[] nSoil = loadDataForHistogram(filePath, 0);
        double[] pSoil = loadDataForHistogram(filePath, 1);
        double[] kSoil = loadDataForHistogram(filePath, 2);

        dataset.addSeries("N_SOIL", nSoil, 20);
        dataset.addSeries("P_SOIL", pSoil, 20);
        dataset.addSeries("K_SOIL", kSoil, 20);

        JFreeChart chart = ChartFactory.createHistogram(
                "Distribution of Soil Nutrient Levels", "Soil Nutrient Levels", "Frequency",
                dataset, PlotOrientation.VERTICAL, true, true, false);
        chart.setBackgroundPaint(new Color(240, 240, 240)); // Set plot background color
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.black); // Set plot area background color

        // Customize domain axis
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 12));
        domainAxis.setLabelFont(new Font("Arial", Font.BOLD, 14));
        domainAxis.setLowerMargin(0.02);
        domainAxis.setUpperMargin(0.02);

        // Customize range axis
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 12));
        rangeAxis.setLabelFont(new Font("Arial", Font.BOLD, 14));

        return new ChartPanel(chart);
    }

    private static double[] loadDataForHistogram(String filePath, int columnIndex) {
        List<Double> data = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(filePath))) {
            scanner.nextLine(); // Skip header
            while (scanner.hasNextLine()) {
                String[] values = scanner.nextLine().split(",");
                data.add(Double.parseDouble(values[columnIndex]));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data.stream().mapToDouble(Double::doubleValue).toArray();
    }

    private static ChartPanel createScatterPlotPanel(String filePath, String xAttribute, String yAttribute) {
        XYSeries series = new XYSeries("Temperature vs Crop Price");
        try (Scanner scanner = new Scanner(new File(filePath))) {
            scanner.nextLine(); // Skip header
            while (scanner.hasNextLine()) {
                String[] values = scanner.nextLine().split(",");
                series.add(Double.parseDouble(values[3]), Double.parseDouble(values[8]));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createScatterPlot(
                "Temperature vs Crop Price", "Temperature", "Crop Price",
                dataset, PlotOrientation.VERTICAL, true, true, false);
        chart.setBackgroundPaint(new Color(240, 240, 240)); // Set plot background color
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.black); // Set plot area background color

        // Customize domain axis
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 12));
        domainAxis.setLabelFont(new Font("Arial", Font.BOLD, 14));
        domainAxis.setLowerMargin(0.02);
        domainAxis.setUpperMargin(0.02);

        // Customize range axis
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 12));
        rangeAxis.setLabelFont(new Font("Arial", Font.BOLD, 14));

        return new ChartPanel(chart);
    }
}
