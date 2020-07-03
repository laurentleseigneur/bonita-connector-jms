package org.bonitasoft.connectors.jms;

import org.bonitasoft.connectors.jms.JmsConnector;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.bpm.document.Document;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.bonitasoft.engine.exception.BonitaException;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JmsConnectorTest {

	/**
	 * Used to assert Exceptions
	 */
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/**
	 * Initialize the tested values
	 */
	@BeforeClass
	public static final void initValues() {

	}


	JmsConnector connector;

	@BeforeEach
	void setUp() {
		connector = new JmsConnector();
	}


	/**
	 * Execute a connector call
	 *
	 * @param parameters The parameters of the connector call
	 * @return The outputs of the connector
	 * @throws BonitaException exception
	 */
	private Map<String, Object> executeConnector(final Map<String, Object> parameters) throws BonitaException {
		JMSConnector jmsConnector = new JMSConnector();
		jmsConnector.setExecutionContext(getEngineExecutionContext());
		jmsConnector.setAPIAccessor(getApiAccessor());
		jmsConnector.setInputParameters(parameters);
		jmsConnector.validateInputParameters();
		return jmsConnector.execute();
	}

	/**
	 * Test send document
	 *
	 * @throws BonitaException      exception
	 * @throws InterruptedException exception
	 */
	@Test
	public void sendDocument() throws BonitaException, InterruptedException, IOException
	{

		ProcessAPI processAPI = getApiAccessor().getProcessAPI();
		Document document = Mockito.mock(Document.class);
		Path path = Paths.get("/Users/lionel/Bonita/Adoption/demo/esb_integration/Claims-letter.docx");
		byte[] content = Files.readAllBytes(path);
		Mockito.when(document.getContentStorageId()).thenReturn("1");
		Mockito.when(getEngineExecutionContext().getProcessInstanceId()).thenReturn(1L);
		Mockito.when(processAPI.getLastDocument(1L, "testDocument")).thenReturn(document);
		Mockito.when(processAPI.getDocumentContent("1")).thenReturn(content);

		List<List> properties = new ArrayList<List>();
		List<String> camelFileNameProp = new ArrayList<String>();
		camelFileNameProp.add(0, "CamelFileName");
		camelFileNameProp.add(1, "testDocument.doc");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("uri", "vm://localhost:61616");
		parameters.put("queueName", "bonitaQueue");
		parameters.put("message", "testDocument");
		parameters.put("isBonitaDocument", true);
		parameters.put("properties", properties);
		parameters.put("anonymous", false);
		parameters.put("username", "user");
		parameters.put("password", "bpm");
		executeConnector(parameters);

	}


}