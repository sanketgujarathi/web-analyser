package boot.service;

import boot.model.PageDetails;

import java.util.Map;

/**
 * Created by sanketg on 4/18/2017.
 */
public interface PageAnalyserService {

    /**
     * Performs analysis of HTML and returns summary
     * @param url
     * @return
     */
    PageDetails getPageDetails(String url);

    /**
     * Performs analysis of hyperlinks on a webpage to check if secure connection is available
     * @param url
     * @return
     */
    Map<String, Boolean> getLinkDetails(String url);
}
