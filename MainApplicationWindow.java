package shakespeareanalysis;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Maryam
 */

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class MainApplicationWindow extends JFrame {
    private JTextArea textArea; // will be where the output is displayed
    private JButton analyzeButton, compareButton, saveButton, play1Button, play2Button; // initializing the buttons for the gui
    private File file1, file2; // creating variables where we will later store the files selected by the user
    private final String STOPWORDS_PATH = "C:\\Users\\Maryam\\Documents\\NetBeansProjects\\ShakespearePlayAnalyzer\\src\\shakespeareanalysis\\StopWords"; // for extracting report

    public MainApplicationWindow() {
        
        // setting the main window title size etc.
        setTitle("Shakespeare Play Analysis");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // text area with monospaced font
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        // button panel, adding all the buttons for display
        JPanel buttonPanel = new JPanel(new FlowLayout());
        play1Button = new JButton("Select Play 1");
        play2Button = new JButton("Select Play 2");
        analyzeButton = new JButton("Analyze Play");
        compareButton = new JButton("Compare Plays");
        saveButton = new JButton("Save Report");

        // disabling buttons initially
        analyzeButton.setEnabled(false);
        compareButton.setEnabled(false);
        saveButton.setEnabled(false);

        buttonPanel.add(play1Button);
        buttonPanel.add(play2Button);
        buttonPanel.add(analyzeButton);
        buttonPanel.add(compareButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.NORTH);

        // action listeners, giving the buttons the functionality
        play1Button.addActionListener(e -> {
            file1 = selectFile();
            if (file1 != null) {
                textArea.append("Loaded Play 1: " + file1.getName() + "\n");
                updateButtonStates();
            }
        });

        play2Button.addActionListener(e -> {
            file2 = selectFile();
            if (file2 != null) {
                textArea.append("Loaded Play 2: " + file2.getName() + "\n");
                updateButtonStates();
            }
        });

        analyzeButton.addActionListener(e -> {
            try {
                PlayAnalysis analysis = new PlayAnalysis(file1.getAbsolutePath(), STOPWORDS_PATH);
                textArea.setText(analysis.generateAnalysisReport());
                saveButton.setEnabled(true);
            } catch (IOException ex) {
                textArea.setText("Error analyzing play: " + ex.getMessage());
            }
        });

        compareButton.addActionListener(e -> {
            try {
                PlayAnalysis analysis1 = new PlayAnalysis(file1.getAbsolutePath(), STOPWORDS_PATH);
                PlayAnalysis analysis2 = new PlayAnalysis(file2.getAbsolutePath(), STOPWORDS_PATH);
                PlayComparer comparator = new PlayComparer(analysis1, analysis2);
                textArea.setText(comparator.comparePlays());
                saveButton.setEnabled(true);
            } catch (IOException ex) {
                textArea.setText("Error comparing plays: " + ex.getMessage());
            }
        });

        saveButton.addActionListener(e -> saveReportToFile());
    }

    // allowing the user to select files
    private File selectFile() {
        JFileChooser chooser = new JFileChooser();
        return chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION 
            ? chooser.getSelectedFile() 
            : null;
    }

    // updating the buttons only if the user selects a file
    private void updateButtonStates() {
        analyzeButton.setEnabled(file1 != null);
        compareButton.setEnabled(file1 != null && file2 != null);
    }

    // allowing user to save the report that is generated
    private void saveReportToFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Play_Analysis_Report.txt"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(chooser.getSelectedFile())) {
                writer.print(textArea.getText());
                JOptionPane.showMessageDialog(this, "Report saved successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // main method, allows the gui to run
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApplicationWindow().setVisible(true));
    }
}