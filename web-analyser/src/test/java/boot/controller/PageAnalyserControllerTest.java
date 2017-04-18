package boot.controller;

import boot.excpetion.PageAnalyserException;
import boot.model.PageDetails;
import boot.service.PageAnalyserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by sanketg on 4/18/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PageAnalyserControllerTest {

    public static final String VALID_PAGE_DETAILS_REQUEST_URL = "/pageDetails?url=https://example.com";
    public static final String VALID_lINK_DETAILS_REQUEST_URL = "/pageDetails?url=https://example.com";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    PageAnalyserService pageAnalyserService;

    @Test
    public void returnEmptyWhenRequestParamMissingForPageDetailsRequest() throws Exception{
        this.mockMvc
                .perform(get("/pageDetails?"))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(content().string(containsString("")));
    }

    @Test
    public void returnErrorMessageWhenEmptyUrlRequestParamForPageDetailsRequest() throws Exception{
        this.mockMvc
                .perform(get("/pageDetails?url="))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(content().string(containsString("Unable to process request: Invalid URL!")));
    }

    @Test
    public void returnErrorMessageWhenMalformedUrlRequestParamForPageDetailsRequest() throws Exception{
        List<String> malformedUrls = Arrays.asList("www.example.com"
                ,"example.com"
                ,"http://www.$$$$$.com"
                ,"http://www.<>?,.com"
                ,"https ://"
                ,"http://www.examp le.com");
        malformedUrls
                .forEach(url -> {
                    try {
                        this.mockMvc
                                .perform(get("/pageDetails?url="+url))
                                .andExpect(status().isBadRequest())
                                .andDo(print())
                                .andExpect(content().string(containsString("Unable to process request: Invalid URL!")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    @Test
    public void returnSuccessWhenValidUrlParamForPageDetailsRequest() throws Exception{
        PageDetails pageDetails = new PageDetailsBuilder()
                .withTitle()
                .withHtmlVersion()
                .withHeadingCount()
                .withLinkCount()
                .withContainsLogin()
                .build();
        Mockito.when(pageAnalyserService.getPageDetails(anyString())).thenReturn(pageDetails);
        ObjectMapper mapper = new ObjectMapper();
        this.mockMvc
                .perform(get(VALID_PAGE_DETAILS_REQUEST_URL))
                .andExpect(status().isOk())

                .andDo(print())
                .andExpect(content().string(containsString(mapper.writeValueAsString(pageDetails))));
    }

    @Test
    public void returnErrorMessageWhenServiceThrowsExceptionForPageDetailsRequest() throws Exception{

        Mockito.when(pageAnalyserService.getPageDetails(anyString())).thenThrow(new PageAnalyserException("Connectivity Issues"));
        this.mockMvc
                    .perform(get(VALID_PAGE_DETAILS_REQUEST_URL))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andExpect(content().string(containsString("Unable to process request: Connectivity Issues")));
    }
    @Test
    public void returnEmptyWhenRequestParamMissingForLinkDetailsRequest() throws Exception{
        this.mockMvc
                .perform(get("/linkDetails?"))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(content().string(containsString("")));
    }

    @Test
    public void returnErrorMessageWhenEmptyUrlRequestParamForLinkDetailsRequest() throws Exception{
        this.mockMvc
                .perform(get("/linkDetails?url="))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(content().string(containsString("Unable to process request: Invalid URL!")));
    }

    @Test
    public void returnErrorMessageWhenMalformedUrlRequestParamForLinkDetailsRequest() throws Exception{
        List<String> malformedUrls = Arrays.asList("www.example.com"
                ,"example.com"
                ,"http://www.$$$$$.com"
                ,"http://www.<>?,.com"
                ,"https ://"
                ,"http://www.examp le.com");
        malformedUrls
                .forEach(url -> {
                    try {
                        this.mockMvc
                                .perform(get("/pageDetails?url="+url))
                                .andExpect(status().isBadRequest())
                                .andDo(print())
                                .andExpect(content().string(containsString("Unable to process request: Invalid URL!")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    @Test
    public void returnSuccessWhenValidUrlParamForLinkDetailsRequest() throws Exception{
        PageDetails pageDetails = new PageDetailsBuilder()
                .withTitle()
                .withHtmlVersion()
                .withHeadingCount()
                .withLinkCount()
                .withContainsLogin()
                .build();
        Mockito.when(pageAnalyserService.getPageDetails(anyString())).thenReturn(pageDetails);
        ObjectMapper mapper = new ObjectMapper();
        this.mockMvc
                .perform(get(VALID_PAGE_DETAILS_REQUEST_URL))
                .andExpect(status().isOk())

                .andDo(print())
                .andExpect(content().string(containsString(mapper.writeValueAsString(pageDetails))));
    }

    @Test
    public void returnErrorMessageWhenServiceThrowsExceptionForLinkDetailsRequest() throws Exception{

        Mockito.when(pageAnalyserService.getPageDetails(anyString())).thenThrow(new PageAnalyserException("Connectivity Issues"));
        this.mockMvc
                    .perform(get(VALID_lINK_DETAILS_REQUEST_URL))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andExpect(content().string(containsString("Unable to process request: Connectivity Issues")));
    }

    private class PageDetailsBuilder {
        private String title;
        private String htmlVersion;
        private Map<String, Integer> headingCount;
        private Map<String, Integer> linkCount;
        private boolean containsLogin;

        PageDetailsBuilder() {

        }

        public PageDetailsBuilder withTitle(){
            this.title = "Test Page";
            return this;
        }

        public PageDetailsBuilder withHtmlVersion(){
            this.htmlVersion = "HTML5";
            return this;
        }

        public PageDetailsBuilder withHeadingCount(){
            this.headingCount = new HashMap<>();
            headingCount.put("h1",1);
            headingCount.put("h2",2);
            headingCount.put("h3",3);
            headingCount.put("h4",4);
            headingCount.put("h5",5);
            return this;
        }

        public PageDetailsBuilder withLinkCount(){
            this.linkCount = new HashMap<>();
            headingCount.put("Internal",10);
            headingCount.put("External",20);
            return this;
        }

        public PageDetailsBuilder withContainsLogin(){
            this.containsLogin = true;
            return this;
        }

        PageDetails build() {
            PageDetails pageDetails = new PageDetails();
            pageDetails.setTitle(this.title);
            pageDetails.setHtmlVersion(this.title);
            pageDetails.setHeadingCount(this.headingCount);
            pageDetails.setLinkCount(this.linkCount);
            return pageDetails;
        }

    }

}
