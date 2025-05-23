package shakespeareanalysis;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Maryam
 */

import java.util.*;
import java.util.stream.Collectors;

public class PlayComparer {
    
    //  creating two instances of the play analysis calss
    private final PlayAnalysis play1;
    private final PlayAnalysis play2;

    public PlayComparer(PlayAnalysis play1, PlayAnalysis play2) {
        this.play1 = play1;
        this.play2 = play2;
    }

    public String comparePlays() {
        StringBuilder report = new StringBuilder();
        
        // basic play info
        report.append("PLAY COMPARISON REPORT\n");
        report.append("======================\n\n");
        report.append("Comparing:\n");
        report.append("- ").append(play1.getTitle()).append("\n");
        report.append("- ").append(play2.getTitle()).append("\n\n");
        
        // protagonist comparison
        report.append("PROTAGONIST COMPARISON\n");
        report.append("----------------------\n");
        report.append("Total protagonists:\n");
        report.append("- ").append(play1.getTitle()).append(": ").append(play1.getProtagonists().size()).append("\n");
        report.append("- ").append(play2.getTitle()).append(": ").append(play2.getProtagonists().size()).append("\n\n");
        
        // top protagonists by speech
        report.append("Top Protagonists by Speech Length:\n");
        report.append(String.format("%-20s %-15s %-15s\n", "Character", play1.getTitle(), play2.getTitle()));
        report.append("------------------------------------------------\n");
        
        play1.getSpeechLengthByProtagonists().entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .limit(5)
            .forEach(entry -> {
                String character = entry.getKey();
                int count1 = entry.getValue();
                int count2 = play2.getSpeechLengthByProtagonists().getOrDefault(character, 0);
                report.append(String.format("%-20s %-15d %-15d\n", character, count1, count2));
            });
        
        // word frequency comparison
        report.append("\nWORD FREQUENCY COMPARISON\n");
        report.append("------------------------\n");
        report.append("Top 10 Unique Words in Each Play:\n");
        
        List<String> topWords1 = play1.getUniqueWords(10).stream()
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        
        List<String> topWords2 = play2.getUniqueWords(10).stream()
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        
        report.append(String.format("%-20s %-20s\n", play1.getTitle(), play2.getTitle()));
        report.append("----------------------------------------\n");
        
        for (int i = 0; i < 10; i++) {
            String word1 = i < topWords1.size() ? topWords1.get(i) : "";
            String word2 = i < topWords2.size() ? topWords2.get(i) : "";
            report.append(String.format("%-20s %-20s\n", word1, word2));
        }
        
        // jaccard Similarity
        report.append("\nJACCARD SIMILARITY\n");
        report.append("------------------\n");
        report.append("Similarity between plays: ").append(calculateJaccardSimilarity()).append("\n");
        
        return report.toString();
    }

    // mthod to calculate jaccard similarity
    private double calculateJaccardSimilarity() {
        Set<String> words1 = play1.getUniqueWords(100).stream()
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
        
        Set<String> words2 = play2.getUniqueWords(100).stream()
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
        
        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);
        
        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);
        
        return union.isEmpty() ? 0 : (double) intersection.size() / union.size();
    }
}