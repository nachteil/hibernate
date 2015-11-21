package pl.edu.agh.ki.bd.htmlIndexer.application;

import org.springframework.stereotype.Component;

@Component
public class RequestTypeDeducer {

    public RequestType determine(String line) {

        RequestType requestType;

        if (line.startsWith("x")) {
            requestType = RequestType.EXIT;
        } else if (line.startsWith("i")) {
            requestType = RequestType.INDEX_PAGE;
        } else if (line.startsWith("l")) {
            requestType = RequestType.FIND_SENTENCES_LONGER_THAN;
        } else if (line.startsWith("f")) {
            requestType = RequestType.FIND_SENTENCES_CONTAING_WORDS;
        } else if (line.startsWith("c")) {
            requestType = RequestType.COUNT_SENTENCES_PER_URL;
        } else if(line.startsWith("o")) {
            requestType = RequestType.COUNT_WORD_OCCURENCES;
        } else {
            requestType = RequestType.PRINT_HELP;
        }
        return requestType;
    }

}
