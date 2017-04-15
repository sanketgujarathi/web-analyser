package boot.service;

import boot.model.PageDetails;
import com.google.common.net.InternetDomainName;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PageDetailService {


    public PageDetails getPageDetails(String url) throws IOException {
        final Document document = Jsoup.connect(url).get();
        final PageDetails htmlPageDetails = new PageDetails();
        htmlPageDetails.setTitle(document.title());
        htmlPageDetails.setHtmlVersion(getHtmlVersion(document));
        htmlPageDetails.setHeadingCount(findHeadingCount(document));
        htmlPageDetails.setLinkCount(findHyperLinkCount(document));
        htmlPageDetails.setContainsLogin(containsLoginForm(document));
        return htmlPageDetails;
    }

    private String getDomain(final Document document) {
        URI uri = null;
        try {
            uri = new URI(document.location());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return InternetDomainName.from(uri.getHost()).topPrivateDomain().toString();
    }

    private Map<String, Integer> findHyperLinkCount(final Document document) {
        document.location();
        Pattern pattern = Pattern.compile(".*" + getDomain(document) + ".*");
        return document
                .body()
                .select("a[href]")
                .stream()
                .map(e -> e.attr("href"))
                .collect(Collectors.groupingBy(e -> pattern.matcher(e).matches() ? "Internal" : "External", Collectors.summingInt(e -> 1)));
    }

    private boolean containsLoginForm(final Document document) {
        Elements password = document.body().select("input").attr("type", "password");
        return password.size() > 0;
    }

    private Map<String, Integer> findHeadingCount(final Document document) {
        return document
                .body()
                .select("h1,h2,h3,h4,h5,h6")
                .stream()
                .collect(Collectors.groupingBy(e -> e.tagName(), Collectors.summingInt(i -> 1)));
    }

    private String getHtmlVersion(final Document document) {
        List<Node> nodes = document.childNodes();
        return nodes.stream().filter(node -> node instanceof DocumentType).map(this::generateHtmlVersion).collect(Collectors.joining());
    }

    private String generateHtmlVersion(Node node) {
        DocumentType documentType = (DocumentType) node;
        String htmlVersion = documentType.attr("publicid");
        return "".equals(htmlVersion) ? "HTML5" : htmlVersion;
    }
}