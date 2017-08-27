package boot.dao;

import boot.dao.impl.PageDaoImpl;
import boot.excpetion.PageAnalyserException;
import org.jsoup.Jsoup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

/**
 * Created by sanketg on 4/18/2017.
 */
//@PrepareForTest(Jsoup.class)
//@SpringBootTest
public class PageAnalyserDaoTest {

    @Autowired
    private PageDao pageDao;

    @Test(expected = PageAnalyserException.class)
    public void testGetPageWhenIOException(){
        PowerMockito.mockStatic(Jsoup.class);
        Mockito.when(Jsoup.connect(anyString())).thenThrow(new IOException());
        pageDao.getPage("Dummy");
        verifyStatic();
    }
}
