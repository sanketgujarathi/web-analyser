package boot.dao;

import org.jsoup.nodes.Document;

/**
 * Created by sanketg on 4/18/2017.
 */
public interface PageDao {
    Document getPage(String url);
}
