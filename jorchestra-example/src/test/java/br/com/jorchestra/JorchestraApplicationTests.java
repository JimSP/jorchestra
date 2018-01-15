package br.com.jorchestra;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.JOrchestraAutoConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JOrchestraAutoConfiguration.class)
@Ignore
public class JorchestraApplicationTests {

	@Test
	public void contextLoads() {
	}

}
