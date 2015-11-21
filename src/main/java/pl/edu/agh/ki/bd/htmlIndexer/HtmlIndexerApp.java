package pl.edu.agh.ki.bd.htmlIndexer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.edu.agh.ki.bd.htmlIndexer.application.IndexOperations;
import pl.edu.agh.ki.bd.htmlIndexer.application.RequestTypeDeducer;
import pl.edu.agh.ki.bd.htmlIndexer.persistence.SessionProvider;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class HtmlIndexerApp implements CommandLineRunner {

    private RequestTypeDeducer deducer;
    private SessionProvider sessionProvider;
    private IndexOperations indexOperations;

    @Autowired
    public HtmlIndexerApp(SessionProvider sessionProvider, RequestTypeDeducer deducer, IndexOperations indexOperations) {
        this.deducer = deducer;
        this.sessionProvider = sessionProvider;
        this.indexOperations = indexOperations;
    }

    @Override
    public void run(String... args) throws Exception {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {

            printPrompt();
            String command = bufferedReader.readLine();

            long startAt = System.currentTimeMillis();
            executeCommand(command);
            System.out.println("took " + (System.currentTimeMillis() - startAt) + " ms");

        }

    }

    private void printPrompt() {
        System.out.println("\nHtmlIndexer [? for help] > : ");
    }

    private void executeCommand(String command) {

        switch (deducer.determine(command)) {

            case PRINT_HELP:
                printHelp();
                break;

            case INDEX_PAGE:
                indexOperations.indexWebPage(command);
                break;

            case FIND_SENTENCES_LONGER_THAN:
                indexOperations.printSentencesLongerThan(command);
                break;

            case FIND_SENTENCES_CONTAING_WORDS:
                indexOperations.printSentencesContainingWords(command);
                break;

            case COUNT_SENTENCES_PER_URL:
                indexOperations.printUrlsWithSentenceCount();
                break;

            case COUNT_WORD_OCCURENCES:
                indexOperations.printCountOfWordOccurrences(command);
                break;

            case EXIT:
                System.out.println("HtmlIndexer terminated.");
                sessionProvider.shutdown();
                return;
        }
    }

    private void printHelp() {
        System.out.println("'?'      	- print this help");
        System.out.println("'x'      	- exit HtmlIndexer");
        System.out.println("'i URLs'  	- index URLs, space separated");
        System.out.println("'f WORDS'	- find sentences containing all WORDs, space separated");
        System.out.println("'c'         - print indexed URLs and number of sentences per URL");
        System.out.println("'o WORD'    - prints number of occurrences of WORD in index");
        System.out.println("'l n        - print indexed sentences longer than n characters (n is integer)' ");
    }
}
