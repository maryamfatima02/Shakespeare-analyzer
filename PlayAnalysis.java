package shakespeareanalysis;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Maryam
 */


import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class PlayAnalysis implements TextAnalyzer{
    
    // setting the variables to be private, consistent with encapsulation
    private String title;
    private Map<String, Integer> protagonistSpeechTurn;
    private Map<String, Integer> protagonistSpeechLength;
    private List<String> protagonists;
    private Set<String> locations;
    private Map<String, Integer> letterFrequency;
    private Map<String, Integer> wordCount;
    private Set<String> stopWords;

    // override method for our interface
    @Override
    public void analyzeText(String filePath) throws IOException {
        shakespearePlayAnalyze(filePath); // calling our existing method
    }
    
    // constructor
    public PlayAnalysis(String filePath, String stopWordsPath) throws IOException {
        this.protagonists = new ArrayList<>();
        this.letterFrequency = new HashMap<>();
        this.stopWords = new HashSet<>(Files.readAllLines(Paths.get(stopWordsPath)));
        this.protagonistSpeechTurn = new HashMap<>();
        this.protagonistSpeechLength = new HashMap<>();
        this.wordCount = new HashMap<>();
        this.locations = new HashSet<>();
        
        shakespearePlayAnalyze(filePath);
    }

    // generates the actual report
    public String generateAnalysisReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== SHAKESPEARE PLAY ANALYSIS ===\n\n");
        
        // title section with null check
        report.append("Title: ").append(title != null ? title : "Unknown").append("\n\n");
        
        // main Characters/protagonists section
        report.append("=== MAIN CHARACTERS ===\n");
        if (protagonists.isEmpty()) {
            report.append("No protagonists identified\n");
        } else {
            protagonists.forEach(character -> 
                report.append("- ").append(character)
                     .append(" (Speeches: ").append(protagonistSpeechTurn.getOrDefault(character, 0))
                     .append(", Words: ").append(protagonistSpeechLength.getOrDefault(character, 0))
                     .append(")\n"));
        }
        
        // locations section with fallback message in case it isnt detected
        report.append("\n=== LOCATIONS ===\n");
        if (locations.isEmpty()) {
            report.append("No locations identified in the text\n");
        } else {
            locations.forEach(location -> report.append("- ").append(location).append("\n"));
        }
        
        // word frequency 
        report.append("\n=== TOP 10 WORDS ===\n");
        List<Map.Entry<String, Integer>> topWords = getTopWords(10);
        if (topWords.isEmpty()) {
            report.append("No words analyzed\n");
        } else {
            topWords.forEach(entry -> 
                report.append("- ").append(entry.getKey())
                     .append(": ").append(entry.getValue()).append("\n"));
        }
        
        // letter frequency 
        report.append("\n=== LETTER FREQUENCY ===\n");
        letterFrequency.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .limit(10)
            .forEach(entry -> report.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n"));
        
        return report.toString();
    }

    private void shakespearePlayAnalyze(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        
        // title extraction, finds first meaningful line
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty() && !trimmed.matches("^[\\W\\d_]+$")) {
                title = trimmed;
                break;
            }
        }

        String speaker = "";
        Set<String> protagonistList = new HashSet<>();

        for (String playLine : lines) {
            playLine = playLine.trim();
            if (playLine.isEmpty()) {
                continue;
            }

            // speaker detection (all uppercase words on their own line)
            if (playLine.matches("^[A-Z]+$")) {
                speaker = playLine;
                if (!protagonistList.contains(speaker)) {
                    protagonists.add(speaker);
                    protagonistList.add(speaker);
                }
                protagonistSpeechTurn.put(speaker, protagonistSpeechTurn.getOrDefault(speaker, 0) + 1);
            } 
            // only count words if we have an active speaker
            else if (!speaker.isEmpty()) {
                int words = playLine.split("\\s+").length;
                protagonistSpeechLength.put(speaker, protagonistSpeechLength.getOrDefault(speaker, 0) + words);
                updateWordCount(playLine, protagonistList);
            }

            // location detection
            if (playLine.matches("^SCENE\\s+[IVXLCDM]+\\.\\s+.*")) {
                String[] parts = playLine.split("\\.\\s+", 2);
                if (parts.length > 1) {
                    // takes first part before comma if exists
                    String location = parts[1].split(",")[0].trim();
                    if (!location.isEmpty()) {
                        locations.add(location);
                    }
                }
            }
        }

        sortProtagonistsByWordCount();
        keepTop5Speakers();
        calculateLetterFrequency(lines);
    }

    // method to update word count
    private void updateWordCount(String playLine, Set<String> protagonistSet) {
        Arrays.stream(playLine.toLowerCase().split("[^a-zA-Z']+"))
              .filter(word -> !word.isEmpty())
              .filter(word -> !stopWords.contains(word))
              .filter(word -> !protagonistSet.contains(word.toUpperCase()))
              .forEach(word -> wordCount.merge(word, 1, Integer::sum));
    }

    // method to sort protagonists by word count
    private void sortProtagonistsByWordCount() {
        protagonists.sort((a, b) -> 
            Integer.compare(
                protagonistSpeechLength.getOrDefault(b, 0),
                protagonistSpeechLength.getOrDefault(a, 0)
            ));
    }

    // extracting top 5 speakers
    private void keepTop5Speakers() {
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(protagonistSpeechLength.entrySet());
        sorted.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        Map<String, Integer> top5 = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : sorted.subList(0, Math.min(5, sorted.size()))) {
            top5.put(entry.getKey(), entry.getValue());
        }
        protagonistSpeechLength = top5;
    }

    private void calculateLetterFrequency(List<String> lines) {
        lines.forEach(line -> line.toLowerCase().chars()
            .filter(Character::isLetter)
            .forEach(c -> letterFrequency.merge(
                String.valueOf((char)c), 1, Integer::sum)));
    }

    // getter methods, follwoing encapsulation
    public String getTitle() { return title; }
    public List<String> getProtagonists() { return protagonists; }
    public Map<String, Integer> getSpeechLengthByProtagonists() { return protagonistSpeechLength; }
    public Set<String> getLocations() { return locations; }
    public Map<String, Integer> getWordCount() { return wordCount; }

    // getting most used words
    public List<Map.Entry<String, Integer>> getTopWords(int limitValue) {
        return wordCount.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .limit(limitValue)
            .collect(Collectors.toList());
    }

    // getting unique words
    public List<Map.Entry<String, Integer>> getUniqueWords(int limitValue) {
        Set<String> excludedWords = new HashSet<>(stopWords);
        excludedWords.addAll(protagonists.stream()
            .map(String::toLowerCase)
            .collect(Collectors.toSet()));

        return wordCount.entrySet().stream()
            .filter(entry -> !excludedWords.contains(entry.getKey().toLowerCase()))
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .limit(limitValue)
            .collect(Collectors.toList());
    }
}