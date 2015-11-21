package pl.edu.agh.ki.bd.htmlIndexer.application;

import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.agh.ki.bd.htmlIndexer.model.ProcessedUrl;
import pl.edu.agh.ki.bd.htmlIndexer.model.Sentence;
import pl.edu.agh.ki.bd.htmlIndexer.model.Word;
import pl.edu.agh.ki.bd.htmlIndexer.persistence.SessionProvider;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class PageIndexer {

    private static final String WORD_BOUNDARY_REGEX = "\\s+";


    private final SessionProvider sessionProvider;

    @Autowired
    public PageIndexer(SessionProvider sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

    public void indexWebPage(String url) throws IOException {

        url = toUrlFormat(url);
        Session session = sessionProvider.getSession();

        Map<String, Word> words = getAllIndexedWords(session);

        Transaction transaction;
        transaction = session.beginTransaction();
        ProcessedUrl processedUrl = new ProcessedUrl(url);

        List<String> stringSentences = getListOfSentencesFromWebpage(url);
        List<Sentence> entitySentences = createEntitiesFromStrings(processedUrl, stringSentences);
        addNewWords(words, stringSentences);

        entitySentences.stream()
                .forEach(processedUrl.getSentences()::add);

        addWordsToSentences(words, entitySentences);

        session.persist(processedUrl);
        transaction.commit();
        session.close();
    }

    private void addWordsToSentences(Map<String, Word> words, List<Sentence> entitySentences) {
        Session session = sessionProvider.getSession();
        Transaction transaction = session.beginTransaction();
        entitySentences.stream()
                .flatMap(sentence -> Arrays.asList(sentence.getContent().split(WORD_BOUNDARY_REGEX)).stream()
                        .map(w -> new Pair<>(sentence, w.toLowerCase().replaceAll("[^\\w]", ""))))
                .forEach(p -> {
                    p.getFirst().getWords().add(words.get(p.getSecond()));
                    words.get(p.getSecond()).getSentences().add(p.getFirst());
                });
        transaction.commit();
        session.close();
    }

    private void addNewWords(Map<String, Word> words, List<String> stringSentences) {
        stringSentences.stream()
                .map(stringSentence -> stringSentence.split(WORD_BOUNDARY_REGEX))
                .map(Arrays::asList)
                .flatMap(Collection::stream)
                .map(String::toLowerCase)
                .map(w -> w.replaceAll("[^\\w]", ""))
                .filter(w -> words.get(w) == null)
                .forEach(w -> words.put(w, new Word(w)));
    }

    private List<Sentence> createEntitiesFromStrings(ProcessedUrl processedUrl, List<String> stringSentences) {
        return stringSentences.stream()
                    .map(s -> new Sentence(s, processedUrl))
                    .collect(Collectors.toList());
    }

    private List<String> getListOfSentencesFromWebpage(String url) throws IOException {
        Elements webPageElements = getWebPageElements(url);
        String sentenceEndRegex = "[\\.!?]+ ";
        return webPageElements.stream()
                .map(Element::ownText)
                .map(String::trim)
                .filter(elementText -> elementText.length() > 1)
                .map(sentenceText -> sentenceText.split(sentenceEndRegex))
                .map(Arrays::asList)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private Map<String, Word> getAllIndexedWords(Session session) {

        Map<String, Word> words = new HashMap<>();
        ((List<Word>) session.createQuery("select w from Word w").list()).stream()
                .forEach(w -> words.put(w.getContent(), w));
        return words;
    }

    private Elements getWebPageElements(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        return doc.body().select("*");
    }

    private String toUrlFormat(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://".concat(url);
        }
        return url;
    }

    private static final class Pair<R,T> {
        @Getter R first;
        @Getter T second;

        public Pair(R first, T second) {
            this.first = first;
            this.second = second;
        }
    }

}
