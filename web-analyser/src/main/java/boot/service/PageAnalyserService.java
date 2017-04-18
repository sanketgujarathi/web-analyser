package boot.service;

import boot.model.PageDetails;

import java.util.Map;

/**
 * Created by sanketg on 4/18/2017.
 */
public interface PageAnalyserService {

    PageDetails getPageDetails(String url);

    Map<String, Boolean> getLinkDetails(String url);
}
