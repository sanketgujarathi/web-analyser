package boot.service;

import boot.dao.PageDao;
import boot.model.PageDetails;
import org.apache.ecs.html.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by sanketg on 4/18/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PageAnalyserServiceTest {

    public static final String VALID_URL = "https://example.com";
    @MockBean
    private PageDao pageDao;

    @Autowired
    private PageAnalyserService pageAnalyserService;

    @Test
    public void testServiceReturnsTrueWhenLoginAndSignupFormPresent(){
        Html html = new Html()
                .addElement(new Head())
                .addElement(new Body()
                        .addElement(new Form()
                                .addElement(new Input().setType("password"))
                                .addElement(new Input().setType("password")))
                        .addElement(new Form()
                                .addElement(new Input().setType("password"))));
        Document document = Jsoup.parse(html.toString(), VALID_URL);
        when(pageDao.getPage(anyString())).thenReturn(document);
        PageDetails pageDetails = pageAnalyserService.getPageDetails(VALID_URL);
        assertThat(pageDetails.isContainsLogin()).isTrue();
        verify(pageDao,atMost(1)).getPage(anyString());

    }

    @Test
    public void testServiceReturnsFalseWhenOnlySignUpFormPresent(){
        Html html = new Html()
                .addElement(new Head())
                .addElement(new Body()
                        .addElement(new Form()
                                .addElement(new Input().setType("password"))
                                .addElement(new Input().setType("password"))));
        System.out.println(html);
        Document document = Jsoup.parse(html.toString(), VALID_URL);
        when(pageDao.getPage(anyString())).thenReturn(document);
        PageDetails pageDetails = pageAnalyserService.getPageDetails(VALID_URL);
        assertThat(pageDetails.isContainsLogin()).isFalse();
        verify(pageDao,atMost(1)).getPage(anyString());
    }

    @Test
    public void testServiceReturnsFalseWhenNoFormIsPresent(){
        Html html = new Html()
                .addElement(new Head())
                .addElement(new Body());
        Document document = Jsoup.parse(html.toString(), VALID_URL);
        when(pageDao.getPage(anyString())).thenReturn(document);
        PageDetails pageDetails = pageAnalyserService.getPageDetails(VALID_URL);
        assertThat(pageDetails.isContainsLogin()).isFalse();
        verify(pageDao, atMost(1)).getPage(anyString());
    }

    @Test
    public void testServiceReturnsEmptyTitleWhenTitleMissing(){
        Html html = new Html()
                .addElement(new Head())
                .addElement(new Body()
                        .addElement(new H1("Demo Header"))
                        .addElement(new H3("Sub Header:")));
        Document document = Jsoup.parse(html.toString(), VALID_URL);
        when(pageDao.getPage(anyString())).thenReturn(document);
        PageDetails pageDetails = pageAnalyserService.getPageDetails(VALID_URL);
        assertThat(pageDetails.getTitle()).isEmpty();
        verify(pageDao, atMost(1)).getPage(anyString());

    }

    @Test
    public void testServiceReturnsSingleTitleWhenMultipleTitlesPresent(){
        Html html = new Html()
                .addElement(new Head()
                        .addElement(new Title("Test Page1"))
                        .addElement(new Title("Test Page2")))
                .addElement(new Body()
                        .addElement(new H1("Demo Header"))
                        .addElement(new H3("Sub Header:")));
        Document document = Jsoup.parse(html.toString(), VALID_URL);
        when(pageDao.getPage(anyString())).thenReturn(document);
        PageDetails pageDetails = pageAnalyserService.getPageDetails(VALID_URL);
        assertThat(pageDetails.getTitle()).isEqualTo("Test Page1");
        verify(pageDao, atMost(1)).getPage(anyString());

    }

    @Test
    public void testServiceReturnsCorrectTitleWhenTitlePresent(){
        Html html = new Html()
                .addElement(new Head()
                        .addElement(new Title("Test Page")))
                .addElement(new Body()
                        .addElement(new H1("Demo Header")));
        Document document = Jsoup.parse(html.toString(), VALID_URL);
        when(pageDao.getPage(anyString())).thenReturn(document);
        PageDetails pageDetails = pageAnalyserService.getPageDetails(VALID_URL);
        assertThat(pageDetails.getTitle()).isEqualTo("Test Page");
        verify(pageDao, atMost(1)).getPage(anyString());

    }

    @Test
    public void testServiceReturnsEmptyVersionWhenDocTypeMissing(){
        Html html = new Html()
                .addElement(new Head()
                        .addElement(new Title("Test Page")))
                .addElement(new Body()
                        .addElement(new H1("Demo Header")));
        Document document = Jsoup.parse(html.toString(), VALID_URL);
        when(pageDao.getPage(anyString())).thenReturn(document);
        PageDetails pageDetails = pageAnalyserService.getPageDetails(VALID_URL);
        assertThat(pageDetails.getHtmlVersion()).isEmpty();
        verify(pageDao, atMost(1)).getPage(anyString());

    }

    @Test
        public void testServiceReturnsVersionHTML5WhenPublicAttributeNotPresentInDTD(){
        Html html = new Html()
                .addElement(new Head()
                        .addElement(new Title("Test Page")))
                .addElement(new Body()
                        .addElement(new H1("Demo Header")));
        Document document = Jsoup.parse("<!DOCTYPE html>"+html.toString(), VALID_URL);
        when(pageDao.getPage(anyString())).thenReturn(document);
        PageDetails pageDetails = pageAnalyserService.getPageDetails(VALID_URL);
        assertThat(pageDetails.getHtmlVersion()).isEqualTo("HTML5");
        verify(pageDao, atMost(1)).getPage(anyString());

    }

    @Test
    public void testServiceReturnsHTMLVersionWhenPublicAttributePresentInDTD(){
        Html html = new Html()
                .addElement(new Head()
                        .addElement(new Title("Test Page")))
                .addElement(new Body()
                        .addElement(new H1("Demo Header")));
        Document document = Jsoup.parse("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\"\n" +
                "\"http://www.w3.org/TR/html4/strict.dtd\">"+html.toString(), VALID_URL);
        when(pageDao.getPage(anyString())).thenReturn(document);
        PageDetails pageDetails = pageAnalyserService.getPageDetails(VALID_URL);
        assertThat(pageDetails.getHtmlVersion()).isEqualTo("-//W3C//DTD HTML 4.01//EN");
        verify(pageDao, atMost(1)).getPage(anyString());

    }

    @Test
    public void testHeadingCountWhenAllTypeOfHeadersPresent(){
        Html html = new Html()
                .addElement(new Head()
                        .addElement(new Title("Test Page")))
                .addElement(new Body()
                        .addElement(new H1("Demo Header1"))
                        .addElement(new H2("Demo Header2"))
                        .addElement(new H2("Demo Header2"))
                        .addElement(new H3("Demo Header3"))
                        .addElement(new H4("Demo Header4"))
                        .addElement(new H5("Demo Header5"))
                        .addElement(new H5("Demo Header5"))
                        .addElement(new H5("Demo Header5"))
                        .addElement(new H6("Demo Header6")));
        Document document = Jsoup.parse(html.toString(), VALID_URL);
        when(pageDao.getPage(anyString())).thenReturn(document);
        PageDetails pageDetails = pageAnalyserService.getPageDetails(VALID_URL);
        assertThat(pageDetails.getHeadingCount().get("h1")).isEqualTo(1);
        assertThat(pageDetails.getHeadingCount().get("h2")).isEqualTo(2);
        assertThat(pageDetails.getHeadingCount().get("h3")).isEqualTo(1);
        assertThat(pageDetails.getHeadingCount().get("h4")).isEqualTo(1);
        assertThat(pageDetails.getHeadingCount().get("h5")).isEqualTo(3);
        assertThat(pageDetails.getHeadingCount().get("h6")).isEqualTo(1);
        verify(pageDao, atMost(1)).getPage(anyString());

    }

    @Test
    public void testHeaderCountWhenOnlySomeTypeOfHeadersPresent(){

        Html html = new Html()
                .addElement(new Head()
                        .addElement(new Title("Test Page")))
                .addElement(new Body()
                        .addElement(new H1("Demo Header1"))
                        .addElement(new H3("Demo Header3"))
                        .addElement(new H6("Demo Header6")));
        Document document = Jsoup.parse(html.toString(), VALID_URL);
        when(pageDao.getPage(anyString())).thenReturn(document);
        PageDetails pageDetails = pageAnalyserService.getPageDetails(VALID_URL);
        assertThat(pageDetails.getHeadingCount().get("h1")).isEqualTo(1);
        assertThat(pageDetails.getHeadingCount().get("h3")).isEqualTo(1);
        assertThat(pageDetails.getHeadingCount().get("h6")).isEqualTo(1);
        assertThat(pageDetails.getHeadingCount().get("h2")).isNull();
        assertThat(pageDetails.getHeadingCount().get("h4")).isNull();
        assertThat(pageDetails.getHeadingCount().get("h5")).isNull();
        verify(pageDao, atMost(1)).getPage(anyString());

    }

    @Test
    public void testServiceReturnsEmptyResultWhenNoHeadersPresent(){

        Html html = new Html()
                .addElement(new Head()
                        .addElement(new Title("Test Page")))
                .addElement(new Body());
        Document document = Jsoup.parse(html.toString(), VALID_URL);
        when(pageDao.getPage(anyString())).thenReturn(document);
        PageDetails pageDetails = pageAnalyserService.getPageDetails(VALID_URL);
        assertThat(pageDetails.getHeadingCount()).isEmpty();
        verify(pageDao, atMost(1)).getPage(anyString());

    }

    @Test
    public void testServiceReturnsResultWhenAnchorTagWithHrefPresent(){

        Html html = new Html()
                .addElement(new Head()
                        .addElement(new Title("Test Page")))
                .addElement(new Body()
                        .addElement(new A().setHref("https://test.com"))
                        .addElement(new A().setHref("/"))
                        .addElement(new A().setHref("#target"))
                        .addElement(new A().setHref(VALID_URL+"/projects"))
                        .addElement(new A().setHref("/document")));
        Document document = Jsoup.parse(html.toString(), VALID_URL);
        when(pageDao.getPage(anyString())).thenReturn(document);
        PageDetails pageDetails = pageAnalyserService.getPageDetails(VALID_URL);
        assertThat(pageDetails.getLinkCount().get("Internal")).isEqualTo(3);
        assertThat(pageDetails.getLinkCount().get("External")).isEqualTo(1);
        verify(pageDao, atMost(1)).getPage(anyString());

    }

    @Test
    public void testServiceReturnsResultWhenLinkTagWithHrefPresent(){

        Html html = new Html()
                .addElement(new Head()
                        .addElement(new Title("Test Page")))
                .addElement(new Body()
                        .addElement(new Link().setHref("https://test.com"))
                        .addElement(new Link().setHref("/"))
                        .addElement(new Link().setHref("#target"))
                        .addElement(new Link().setHref(VALID_URL + "/projects"))
                        .addElement(new Link().setHref("/document")));
        Document document = Jsoup.parse(html.toString(), VALID_URL);
        when(pageDao.getPage(anyString())).thenReturn(document);
        PageDetails pageDetails = pageAnalyserService.getPageDetails(VALID_URL);
        assertThat(pageDetails.getLinkCount().get("Internal")).isEqualTo(3);
        assertThat(pageDetails.getLinkCount().get("External")).isEqualTo(1);
        verify(pageDao, atMost(1)).getPage(anyString());

    }

    @Test
    public void testServiceReturnsEmptyResultWhenAnchorTagIsWithoutHref(){
        Html html = new Html()
                .addElement(new Head()
                        .addElement(new Title("Test Page")))
                .addElement(new Body()
                        .addElement(new A())
                        .addElement(new A()));
        Document document = Jsoup.parse(html.toString(), VALID_URL);
        when(pageDao.getPage(anyString())).thenReturn(document);
        PageDetails pageDetails = pageAnalyserService.getPageDetails(VALID_URL);
        assertThat(pageDetails.getLinkCount()).isEmpty();
        verify(pageDao, atMost(1)).getPage(anyString());

    }
@Test
    public void testServiceReturnsEmptyResultWhenLinkTagIsWithoutHref(){
        Html html = new Html()
                .addElement(new Head()
                        .addElement(new Title("Test Page")))
                .addElement(new Body()
                        .addElement(new Link())
                        .addElement(new Link()));
        Document document = Jsoup.parse(html.toString(), VALID_URL);
        when(pageDao.getPage(anyString())).thenReturn(document);
        PageDetails pageDetails = pageAnalyserService.getPageDetails(VALID_URL);
        assertThat(pageDetails.getLinkCount()).isEmpty();
        verify(pageDao, atMost(1)).getPage(anyString());

    }

    // repeat for link details method

    @Test
    public void testLinkDetailsReturnsTrueHyperLinkIsSecure() throws Exception {

        Html html = new Html()
                .addElement(new Head()
                        .addElement(new Title("Test Page")))
                .addElement(new Body()
                        .addElement(new Link().setHref("https://test.com")));
        Document document = Jsoup.parse(html.toString(), VALID_URL);
        when(pageDao.getPage(anyString())).thenReturn(document);

        final URLStreamHandler handler = new URLStreamHandler() {

            @Override
            protected URLConnection openConnection(final URL arg0)
                    throws IOException {
                return mock(HttpsURLConnection.class);
            }
        };
        final URL url = new URL("https://test.com", "test.com", 80, "", handler);
        PowerMockito.whenNew(URL.class).withAnyArguments().thenReturn(url);

        Map<String, Boolean> linkDetails = pageAnalyserService.getLinkDetails(VALID_URL);
        assertThat(linkDetails.get("https://test.com")).isTrue();
        verify(pageDao, atMost(1)).getPage(anyString());

    }

    @Test
    public void testLinkDetailsReturnsTrueHyperLinkIsNotSecure() throws Exception {

        Html html = new Html()
                .addElement(new Head()
                        .addElement(new Title("Test Page")))
                .addElement(new Body()
                        .addElement(new Link().setHref("http://test.com")));
        Document document = Jsoup.parse(html.toString(), VALID_URL);
        when(pageDao.getPage(anyString())).thenReturn(document);

        final URLStreamHandler handler = new URLStreamHandler() {

            @Override
            protected URLConnection openConnection(final URL arg0)
                    throws IOException {
                return mock(HttpURLConnection.class);
            }
        };
        final URL url = new URL("https://test.com", "test.com", 80, "", handler);
        PowerMockito.whenNew(URL.class).withAnyArguments().thenReturn(url);

        Map<String, Boolean> linkDetails = pageAnalyserService.getLinkDetails(VALID_URL);
        assertThat(linkDetails.get("http://test.com")).isFalse();
        verify(pageDao, atMost(1)).getPage(anyString());

    }
}
