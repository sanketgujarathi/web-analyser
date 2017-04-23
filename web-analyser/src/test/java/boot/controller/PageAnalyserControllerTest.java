package boot.controller;

import boot.excpetion.PageAnalyserException;
import boot.model.PageDetails;
import boot.service.PageAnalyserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
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
import static org.mockito.Mockito.*;
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

    private static final String VALID_PAGE_DETAILS_REQUEST_URL = "/pageDetails?url=https://example.com";
    private static final String VALID_lINK_DETAILS_REQUEST_URL = "/linkDetails?url=https://example.com";
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
        verify(pageAnalyserService, never()).getLinkDetails(anyString());
        verify(pageAnalyserService, never()).getPageDetails(anyString());
    }

    @Test
    public void returnErrorMessageWhenEmptyUrlRequestParamForPageDetailsRequest() throws Exception{
        this.mockMvc
                .perform(get("/pageDetails?url="))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(content().string(containsString("Unable to process request: Invalid URL!")));
        verify(pageAnalyserService, never()).getLinkDetails(anyString());
        verify(pageAnalyserService, never()).getPageDetails(anyString());
    }

    @Test
    public void returnErrorMessageWhenUrlExceedsSizeLimitForPageDetailsRequest() throws Exception{
        this.mockMvc
                .perform(get( StringUtils.rightPad("/pageDetails?url=https://example.com",2100,"/a")))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(content().string(containsString("Unable to process request: Invalid URL!")));
        verify(pageAnalyserService, never()).getLinkDetails(anyString());
        verify(pageAnalyserService, never()).getPageDetails(anyString());
    }

    @Test
    public void returnErrorMessageWhenMalformedUrlRequestParamForPageDetailsRequest() throws Exception{
        List<String> malformedUrls = Arrays.asList("www.example.com"
                ,"example.com"
                ,"http://www.$$$$$.com"
                ,"http://www.<>?,.com"
                ,"https://"
                ,"dummy://example.com"
                ,"http://www.examp le.com");
        malformedUrls
                .forEach(url -> {
                    try {
                        this.mockMvc
                                .perform(get("/pageDetails?url="+url))
                                .andExpect(status().isBadRequest())
                                .andDo(print())
                                .andExpect(content().string(containsString("Unable to process request: Invalid URL!")));
                        verify(pageAnalyserService, never()).getLinkDetails(anyString());
                        verify(pageAnalyserService, never()).getPageDetails(anyString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    @Test
    public void returnSuccessWhenValidUrlParamForPageDetailsRequest() throws Exception {
        PageDetails pageDetails = new PageDetailsBuilder()
                .withTitle()
                .withHtmlVersion()
                .withHeadingCount()
                .withLinkCount()
                .withContainsLogin()
                .build();
        Mockito.when(pageAnalyserService.getPageDetails(anyString())).thenReturn(pageDetails);
        ObjectMapper mapper = new ObjectMapper();
        List<String> validUrls = Arrays.asList("http://www.example.com:8800"
                                        ,"http://www.example.com/a/b/c/d/e/f/g/h/i.html"
                                        ,"http://www.example.com/do.html#A"
                                        ,"http://www.example.com?pageid=123&testid=1524");
        validUrls.forEach(url -> {
                    try {
                        this.mockMvc
                                .perform(get("/pageDetails?url=" + url))
                                .andExpect(status().isOk())
                                .andDo(print())
                                .andExpect(content().string(containsString(mapper.writeValueAsString(pageDetails))));
                        verify(pageAnalyserService, atMost(validUrls.size())).getPageDetails(anyString());
                        verify(pageAnalyserService, never()).getLinkDetails(anyString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    @Test
    public void returnErrorMessageWhenServiceThrowsExceptionForPageDetailsRequest() throws Exception{

        Mockito.when(pageAnalyserService.getPageDetails(anyString())).thenThrow(new PageAnalyserException("Connectivity Issues"));
        this.mockMvc
                    .perform(get(VALID_PAGE_DETAILS_REQUEST_URL))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andExpect(content().string(containsString("Unable to process request: Connectivity Issues")));
        verify(pageAnalyserService, times(1)).getPageDetails(anyString());
        verify(pageAnalyserService, never()).getLinkDetails(anyString());
    }

    @Test
    public void returnEmptyWhenRequestParamMissingForLinkDetailsRequest() throws Exception{
        this.mockMvc
                .perform(get("/linkDetails?"))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(content().string(containsString("")));
        verify(pageAnalyserService, never()).getLinkDetails(anyString());
        verify(pageAnalyserService, never()).getPageDetails(anyString());
    }

    @Test
    public void returnErrorMessageWhenEmptyUrlRequestParamForLinkDetailsRequest() throws Exception{
        this.mockMvc
                .perform(get("/linkDetails?url="))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(content().string(containsString("Unable to process request: Invalid URL!")));
        verify(pageAnalyserService, never()).getLinkDetails(anyString());
        verify(pageAnalyserService, never()).getPageDetails(anyString());
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
                        verify(pageAnalyserService, never()).getLinkDetails(anyString());
                        verify(pageAnalyserService, never()).getPageDetails(anyString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    @Test
    public void returnErrorMessageWhenUrlExceedsSizeLimitForLinkDetailsRequest() throws Exception{
        this.mockMvc
                .perform(get( StringUtils.rightPad("/linkDetails?url=https://example.com",2100,"/a")))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(content().string(containsString("Unable to process request: Invalid URL!")));
        verify(pageAnalyserService, never()).getLinkDetails(anyString());
        verify(pageAnalyserService, never()).getPageDetails(anyString());
    }
    @Test
    public void returnSuccessWhenValidUrlParamForLinkDetailsRequest() throws Exception{
        Map<String, Boolean> linkDetailMap = getLinkDetailMap();
        Mockito.when(pageAnalyserService.getLinkDetails(anyString())).thenReturn(linkDetailMap);
        ObjectMapper mapper = new ObjectMapper();
        List<String> validUrls = Arrays.asList("http://www.example.com:8800"
                ,"http://www.example.com/a/b/c/d/e/f/g/h/i.html"
                ,"http://www.example.com/do.html#A"
                ,"http://www.example.com?pageid=123&testid=1524");
        validUrls.forEach(url -> {
                    try {
                        this.mockMvc
                                .perform(get("/linkDetails?url=" + url))
                                .andExpect(status().isOk())
                                .andDo(print())
                                .andExpect(content().string(containsString(mapper.writeValueAsString(linkDetailMap))));
                        verify(pageAnalyserService, never()).getPageDetails(anyString());
                        verify(pageAnalyserService, atMost(validUrls.size())).getLinkDetails(anyString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    @Test
    public void returnErrorMessageWhenServiceThrowsExceptionForLinkDetailsRequest() throws Exception{

        Mockito.when(pageAnalyserService.getLinkDetails(anyString())).thenThrow(new PageAnalyserException("Connectivity Issues"));
        this.mockMvc
                    .perform(get(VALID_lINK_DETAILS_REQUEST_URL))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andExpect(content().string(containsString("Unable to process request: Connectivity Issues")));
        verify(pageAnalyserService, never()).getPageDetails(anyString());
        verify(pageAnalyserService, times(1)).getLinkDetails(anyString());
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

    private Map<String, Boolean> getLinkDetailMap(){
        Map<String, Boolean> mockMap = new HashMap<>();
        mockMap.put("http://www.example.com/a/b/c/d/e/f/g/h/i.html", true);
        mockMap.put("http://www.example.com/do.html#A", false);
        mockMap.put("http://www.example.com?pageid=123&testid=1524", true);
        return mockMap;
    }

}
