package boot.controller;

import boot.model.PageDetails;
import boot.service.PageDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Map;

/**
 * Created by sanketg on 4/14/2017.
 */
@RestController
@Validated
public class PageDetailsController {

    @Autowired
    private PageDetailService pageDetailService;


    @RequestMapping(value="/pageDetails", method= RequestMethod.GET,
            produces="application/json")
    public PageDetails getPageDetail(@RequestParam @NotNull String url) throws IOException {
            return  pageDetailService.getPageDetails(url);
    }

    @RequestMapping(value="/linkDetails", method= RequestMethod.GET,
            produces="application/json")
    public Map<String, Boolean> getLinkDetail(@RequestParam @NotNull String url) throws IOException {
            return pageDetailService.getLinkDetails(url);

    }




}
