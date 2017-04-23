package boot.controller;

import boot.excpetion.PageAnalyserException;
import boot.model.PageDetails;
import boot.service.PageAnalyserService;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Size;
import java.util.Map;

/**
 * Created by sanketg on 4/14/2017.
 */
@RestController
@Validated
public class PageAnalyserController {

    public static final String[] ALLOWED_SCHEMES = {"http", "https"};
    @Autowired
    private PageAnalyserService pageAnalyserService;

    /**
     * Returns a json string with gives a summary of web page analysed
     * @param url - url of the web page to be analysed
     * @return json representation of page details object
     */

    @RequestMapping(value="/pageDetails", method= RequestMethod.GET,
            produces="application/json")
    @ResponseStatus(HttpStatus.OK)
    public PageDetails getPageDetails(@RequestParam @Size(max = 2000) String url) {
         if(!new UrlValidator(ALLOWED_SCHEMES).isValid(url)) {
             throw new PageAnalyserException("Invalid URL!");
         };
        return  pageAnalyserService.getPageDetails(url);

    }

    /**
     * Returns map of all hyperlinks in web page and a corresponding flag indicating if secure connection is available or not
     * @param url - url of the webpage whose hyperlinks are to be analysed
     * @return json representation of url string and boolean flag
     */
    @RequestMapping(value="/linkDetails", method= RequestMethod.GET,
            produces="application/json")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Boolean> getLinkDetails(@RequestParam @Size(max = 2000)String url) {
        if(!new UrlValidator(ALLOWED_SCHEMES).isValid(url)) {
            throw new PageAnalyserException("Invalid URL!");
        }
        return pageAnalyserService.getLinkDetails(url);

    }




}
