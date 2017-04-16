package boot.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

/**
 * Created by sanketg on 4/15/2017.
 */
@XmlRootElement(name = "response")
public class PageDetails {

    private String title;
    private String htmlVersion;
    private Map<String, Integer> headingCount;
    private Map<String, Integer> linkCount;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHtmlVersion() {
        return htmlVersion;
    }

    public void setHtmlVersion(String htmlVersion) {
        this.htmlVersion = htmlVersion;
    }

    public Map<String, Integer> getHeadingCount() {
        return headingCount;
    }

    public void setHeadingCount(Map<String, Integer> headingCount) {
        this.headingCount = headingCount;
    }

    public Map<String, Integer> getLinkCount() {
        return linkCount;
    }

    public void setLinkCount(Map<String, Integer> linkCount) {
        this.linkCount = linkCount;
    }

    public boolean isContainsLogin() {
        return containsLogin;
    }

    public void setContainsLogin(boolean containsLogin) {
        this.containsLogin = containsLogin;
    }

    private boolean containsLogin;
}
