package pl.edu.agh.ki.bd.htmlIndexer.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IndexOperations {

    private static final int OPTION_START_INDEX = 2;

    private final PageIndexer indexer;
    private final IndexReader indexReader;

    @Autowired
    public IndexOperations(PageIndexer index, IndexReader indexReader) {
        this.indexer = index;
        this.indexReader = indexReader;
    }

    public void printSentencesLongerThan(String command) {
        String lengthString = command.substring(OPTION_START_INDEX);
        Integer length = Integer.valueOf(lengthString);
        for (String sentence : indexReader.findSentencesLongerThan(length)) {
            System.out.println("Long sentence: " + sentence);
        }
    }

    public void printSentencesContainingWords(String command) {
        String wordsToFind = command.substring(OPTION_START_INDEX);
        for (String sentence : indexReader.findSentencesByWords(wordsToFind)) {
            System.out.println("Found in sentence: \"" + sentence);
        }
    }

    public void indexWebPage(String command) {
        for (String url : command.substring(OPTION_START_INDEX).split("\\s")) {
            try {
                indexer.indexWebPage(url);
                System.out.println("Indexed: " + url);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error indexing: " + e.getMessage());
            }
        }
    }

    public void printUrlsWithSentenceCount() {
        indexReader.getAllUrlsWithSentencesCount().forEach(System.out::println);
    }

    public void printCountOfWordOccurrences(String command) {
        String wordToFind = command.substring(OPTION_START_INDEX);
        int numberOfOccurrences = indexReader.getNumberOfWordOccurences(wordToFind);
        System.out.println("Number of '" + wordToFind + "' occurrences: " + numberOfOccurrences);
    }
}
