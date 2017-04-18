package boot;

import boot.controller.PageAnalyserController;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by sanketg on 4/18/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SmokeTest {

    @Autowired
    PageAnalyserController pageAnalyserController;

    @Test
    public void contextLoads() {
        Assertions.assertThat(pageAnalyserController).isNotNull();
    }
}
