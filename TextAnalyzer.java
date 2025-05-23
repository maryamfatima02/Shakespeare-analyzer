package shakespeareanalysis;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Maryam
 */

import java.io.IOException;

// interface, allowing us to generate analyzer classes in the future

public interface TextAnalyzer {
    String generateAnalysisReport();
    void analyzeText(String filePath) throws IOException;
}