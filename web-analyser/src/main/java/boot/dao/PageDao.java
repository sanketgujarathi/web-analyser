package boot.dao;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository
public class PageDao {

    public Document getPage(String url) throws IOException {
        return Jsoup.connect(url).get();
    }
}