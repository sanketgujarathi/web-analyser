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

    @Autowired
    private PageAnalyserService pageAnalyserService;


    @RequestMapping(value="/pageDetails", method= RequestMethod.GET,
            produces="application/json")
    @ResponseStatus(HttpStatus.OK)
    public PageDetails getPageDetails(@RequestParam @Size(max = 2000) String url) {
         if(!new UrlValidator().isValid(url)) {
             throw new PageAnalyserException("Invalid URL!");
         };
        return  pageAnalyserService.getPageDetails(url);

    }

    @RequestMapping(value="/linkDetails", method= RequestMethod.GET,
            produces="application/json")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Boolean> getLinkDetails(@RequestParam @Size(max = 2000)String url) {
        if(!new UrlValidator().isValid(url)) {
            throw new PageAnalyserException("Invalid URL!");
        }
        return pageAnalyserService.getLinkDetails(url);

    }




}
