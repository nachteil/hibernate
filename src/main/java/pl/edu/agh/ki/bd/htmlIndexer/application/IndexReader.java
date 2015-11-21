package pl.edu.agh.ki.bd.htmlIndexer.application;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.agh.ki.bd.htmlIndexer.model.Sentence;
import pl.edu.agh.ki.bd.htmlIndexer.persistence.SessionProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class IndexReader {

    private final SessionProvider sessionProvider;

    @Autowired
    public IndexReader(SessionProvider sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

    public List<String> findSentencesLongerThan(int length) {

        Session session = sessionProvider.getSession();
        Transaction transaction = session.beginTransaction();

        List<String> result = session.createCriteria(Sentence.class)
                .add(Restrictions.gt("contentLength", length))
                .setProjection(Projections.property("content"))
                .list();

        transaction.commit();
        return result;
    }

    public List<String> findSentencesByWords(String words) {

        Session session = sessionProvider.getSession();
        Transaction transaction = session.beginTransaction();

        List<String> wordsToFind = getWordsToFindLowercased(words);

        Criteria criteria = createByWordCritieria(session, wordsToFind);
        List<Object[]> criteriaQueryResult = criteria.list();
        transaction.commit();

        return retrieveSentencesFromCriteriaResult(criteriaQueryResult);
    }

    public List<String> getAllUrlsWithSentencesCount() {

        Session session = sessionProvider.getSession();
        Transaction transaction = session.beginTransaction();
        List<Object[]> queryResult = session.createQuery("select url, count(s) as c from Sentence s join s.sourceUrl url group by url.id order by c").list();
        transaction.commit();

        return convertToUrlAndSentenceCountList(queryResult);
    }

    private List<String> convertToUrlAndSentenceCountList(List<Object[]> queryResult) {
        return queryResult.stream().map(resultArray -> resultArray[0] + " " + resultArray[1]).collect(Collectors.toList());
    }

    private ArrayList<String> retrieveSentencesFromCriteriaResult(List<Object[]> result) {
        return result.stream().map(arr -> (String) arr[0]).collect(Collectors.toCollection(ArrayList<String>::new));
    }

    private Criteria createByWordCritieria(Session session, List<String> wordsToFind) {
        return session.createCriteria(Sentence.class, "s")
                    .createAlias("s.words", "wordsInSentence")
                    .add(Restrictions.in("wordsInSentence.content", wordsToFind))
                    .setProjection(Projections.projectionList()
                                    .add(Projections.groupProperty("s.content"))
                                    .add(Projections.count("wordsInSentence.content").as("wordCount"))
                    )
                    .addOrder(Order.asc("wordCount"));
    }

    private List<String> getWordsToFindLowercased(String words) {
        return Arrays.stream(words.split("\\s+"))
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
    }

    public int getNumberOfWordOccurences(String wordToFind) {
        Session session = sessionProvider.getSession();
        Transaction transaction = session.beginTransaction();
        List<Sentence> queryResult = session.createCriteria(Sentence.class)
                .add(Restrictions.like("content", "%" + wordToFind + "%").ignoreCase())
                .list();
        transaction.commit();
        session.close();

        int result = (int) queryResult.stream()
                .map(Sentence::getContent)
                .map(String::toLowerCase)
                .map(s -> s.replaceAll("[^\\w ]", ""))
                .map(s -> s.split("\\s+"))
                .map(Arrays::asList)
                .flatMap(Collection::stream)
                .filter(s -> s.equals(wordToFind))
                .count();

        return result;
    }
}
