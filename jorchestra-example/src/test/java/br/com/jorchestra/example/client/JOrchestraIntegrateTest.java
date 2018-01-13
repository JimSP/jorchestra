package br.com.jorchestra.example.client;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.websocket.DeploymentException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.jorchestra.client.JOrchestraConnector;
import br.com.jorchestra.client.exception.JOrchestraConnectionCloseException;
import br.com.jorchestra.client.exception.JOrchestraConnectionErrorException;
import br.com.jorchestra.dto.JOrchestraBeanResponse;
import br.com.jorchestra.example.JorchestraApplication;

public class JOrchestraIntegrateTest {

	private static final String END_POINT = "ws://localhost:8080/jOrchestra-beans";

	@BeforeClass
	public static void initClass() {
		JorchestraApplication.main(new String[] {});
	}

	private final static String EXPECTED_JSON = "[{\"jOrchestraBeanName\":\"electronicTransferOfFundsExample\",\"jOrchestraPath\":\"/account-transfer\",\"requestTemplate\":\"{\\\"transferIdentification\\\":\\\"cbcc1576-0655-462b-aec1-5597fb2ddbdb\\\",\\\"from\\\":{\\\"accountNumber\\\":9223372036854775807},\\\"to\\\":{\\\"accountNumber\\\":9223372036854775807},\\\"value\\\":9223372036854775807}\",\"responseTemplate\":\"{\\\"transferIdentification\\\":\\\"7fffffff-ffff-ffff-7fff-ffffffffffff\\\",\\\"statusWithdraw\\\":\\\"ERROR\\\",\\\"statusTransfer\\\":\\\"ERROR\\\",\\\"transferRequest\\\":{\\\"transferIdentification\\\":\\\"1cfe82a3-8e8e-4f1d-8e02-0f4c6c5e79e5\\\",\\\"from\\\":{\\\"accountNumber\\\":9223372036854775807},\\\"to\\\":{\\\"accountNumber\\\":9223372036854775807},\\\"value\\\":9223372036854775807}}\",\"message\":null},{\"jOrchestraBeanName\":\"JOrchestraBeans\",\"jOrchestraPath\":\"/jOrchestra-beans\",\"requestTemplate\":null,\"responseTemplate\":\"[]\",\"message\":null},{\"jOrchestraBeanName\":\"JOrchestraHelloWordSystemEvent\",\"jOrchestraPath\":\"/events-accept\",\"requestTemplate\":null,\"responseTemplate\":\"\\\"\\\"\",\"message\":null},{\"jOrchestraBeanName\":\"JOrchestraNotificationEletronicTransferAccount\",\"jOrchestraPath\":\"/notification-account\",\"requestTemplate\":\"{\\\"transferIdentification\\\":\\\"7fffffff-ffff-ffff-7fff-ffffffffffff\\\",\\\"statusWithdraw\\\":\\\"ERROR\\\",\\\"statusTransfer\\\":\\\"ERROR\\\",\\\"transferRequest\\\":{\\\"transferIdentification\\\":\\\"442fad66-a261-44d2-b312-a7d1ae75c28e\\\",\\\"from\\\":{\\\"accountNumber\\\":9223372036854775807},\\\"to\\\":{\\\"accountNumber\\\":9223372036854775807},\\\"value\\\":9223372036854775807}}\",\"responseTemplate\":\"\\\"ERROR\\\"\",\"message\":null}]";

	private List<JOrchestraBeanResponse> expectedResult;

	@Before
	public void init() throws JsonParseException, JsonMappingException, IOException {
		final ObjectMapper objectMapper = new ObjectMapper();
		expectedResult = Arrays.asList(objectMapper.readValue(EXPECTED_JSON, JOrchestraBeanResponse[].class));
	}

	@Test
	public void testJOrchestraBeans() throws InterruptedException, ExecutionException, IOException,
			JOrchestraConnectionCloseException, JOrchestraConnectionErrorException, DeploymentException {
		
		try (final JOrchestraConnector jOrchestraConnector = JOrchestraConnector.create(END_POINT)) {	
			Assert.assertTrue(jOrchestraConnector.send().size() == expectedResult.size());
		}
	}
}
