package boot.service.impl;

import boot.dao.PageDao;
import boot.excpetion.PageAnalyserException;
import boot.model.PageDetails;
import boot.service.PageAnalyserService;
import com.google.common.net.InternetDomainName;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PageAnalyserServiceImpl implements PageAnalyserService {

    @Autowired
    private PageDao pageDao;

    public PageDetails getPageDetails(final String url) {
        final Document document = pageDao.getPage(url);
        final PageDetails htmlPageDetails = new PageDetails();
        htmlPageDetails.setTitle(document.title());
        htmlPageDetails.setHtmlVersion(getHtmlVersion(document));
        htmlPageDetails.setHeadingCount(findHeadingCount(document));
        htmlPageDetails.setLinkCount(findHyperLinkCount(document));
        htmlPageDetails.setContainsLogin(containsLoginForm(document));
        return htmlPageDetails;
    }


    /**
     * Extracts the domain from a given url string
     * @param location
     * @return
     */
    private String getDomain(final String location) {
        String host = getHost(location);
        return host != null ? InternetDomainName.from(host).topPrivateDomain().toString() : StringUtils.EMPTY;
    }

    /**
     * Extracts the base host of given url string
     * @param location
     * @return
     */
    private String getHost(final String location) {
        URI uri;
        try {
            uri = new URI(location);
        } catch (URISyntaxException e) {
            throw new PageAnalyserException(String.format("Unable to determine from: %s", location));
        }
        return uri.getHost();
    }

    /**
     * Cycles through all the hyperlinks in the web page and classifies them as internal or external based on their domain
     * @param document
     * @return
     */
    private Map<String, Integer> findHyperLinkCount(final Document document) {
        return document
                .body()
                .select("a[href],link[href]")
                .parallelStream()
                .map(e -> e.attr("href"))
                .filter(e -> !e.startsWith("#"))
                .collect(Collectors.groupingBy(e -> {
                    if (getDomain(e).isEmpty() || getDomain(e).equals(getDomain(document.location()))) {
                        return "Internal";
                    } else {
                        return "External";
                    }

                }, Collectors.summingInt(e -> 1)));
    }

    /**
     * Determins if web page contains a login form. Forms with exactly one password input field are assumed to be login forms
     * @param document
     * @return
     */
    private boolean containsLoginForm(final Document document) {
        Elements password = document.body().select("input").attr("type", "password");
        return password
                .parallelStream()
                .anyMatch(e -> {
                    Elements passwordsInForm = e.parent().select("input").attr("type", "password");
                    return passwordsInForm.size() == 1;
                });
    }

    /**
     * Finds the count of each heading tags(h1-h6) in the web page
     * @param document
     * @return
     */
    private Map<String, Integer> findHeadingCount(final Document document) {
        return document
                .body()
                .select("h1,h2,h3,h4,h5,h6")
                .parallelStream()
                .collect(Collectors.groupingBy(e -> e.tagName(), Collectors.summingInt(i -> 1)));
    }

    /**
     * Extracts the html version from DTD of the web page
     * @param document
     * @return
     */
    private String getHtmlVersion(final Document document) {
        List<Node> nodes = document.childNodes();
        return nodes
                .parallelStream()
                .filter(node -> node instanceof DocumentType)
                .map(this::generateHtmlVersion).collect(Collectors.joining());
    }

    private String generateHtmlVersion(final Node node) {
        DocumentType documentType = (DocumentType) node;
        String htmlVersion = documentType.attr("publicid");
        return "".equals(htmlVersion) ? "HTML5" : htmlVersion;
    }

    @Override
    public Map<String, Boolean> getLinkDetails(final String url) {
        final Document document = pageDao.getPage(url);
        return document
                .select("a[href],link[href]")
                .parallelStream()
                .map(e -> e.attr("href"))
                .filter(e -> !e.startsWith("#"))
                .map(p -> {
                    try {
                        return new URL(p.startsWith("/") ? url : p);
                    } catch (MalformedURLException e) {
                        return null;
                    }
                })
                .filter(p -> p != null)
                .collect(Collectors.toMap(
                        p -> p.toString(),
                        p -> {
                            try {
                                URLConnection urlConnection = p.openConnection();
                                    if (urlConnection instanceof HttpsURLConnection) {
                                    return true;
                                } else if (urlConnection instanceof HttpURLConnection) {
                                    return false;
                                }
                            } catch (IOException e) {
                                return false;
                            }
                            return false;
                        },
                        (ssl1, ssl2) -> ssl1 || ssl2


        ));

    }


}