package boot.dao;

import org.jsoup.nodes.Document;

/**
 * Created by sanketg on 4/18/2017.
 */
public interface PageDao {
    /**
     * Retrieves web page of given url and return a object representation of parsed HTML
     * @param url
     * @return
     */
    Document getPage(String url);
}
