package boot.controller;

import boot.model.PageDetails;
import boot.model.PageDetailsResponse;
import boot.service.PageDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * Created by sanketg on 4/14/2017.
 */
@RestController
public class PageDetailsController {

    @Autowired
    private PageDetailService pageDetailService;


    @RequestMapping(value="/pageDetails", method= RequestMethod.GET,
            produces="application/json")
    public PageDetails getPageDetail(@RequestParam @NotNull String url) {
        try {
            final PageDetails htmlPageDetails = pageDetailService.getPageDetails(url);
            return htmlPageDetails;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new PageDetails();
    }


}
