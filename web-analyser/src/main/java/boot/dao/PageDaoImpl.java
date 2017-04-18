package boot.dao;

import boot.excpetion.PageAnalyserException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository
public class PageDaoImpl implements PageDao {

    @Override
    public Document getPage(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new PageAnalyserException("Connectivity Issues");
        }
    }
}