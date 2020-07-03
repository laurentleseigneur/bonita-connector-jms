package org.bonitasoft.connectors.jms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.bpm.document.Document;
import org.bonitasoft.engine.bpm.document.DocumentNotFoundException;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.slf4j.LoggerFactory;

import javax.jms.*;

public class JmsConnector extends AbstractJmcConnectorImpl {

	private static final Logger LOGGER = Logger.getLogger(JmsConnector.class.getName());

	static final String DEFAULT_INPUT = "defaultInput";
	static final String DEFAULT_OUTPUT = "defaultOutput";


	private Message loadProperties(Message message) throws JMSException {
		for (List entry : (List<List>) getProperties()) {
			message.setObjectProperty(entry.get(0) + "", entry.get(1));
		}
		return message;
	}

	/**
	 * Perform validation on the inputs defined on the connector definition (src/main/resources/bonita-connector-jms.def)
	 * You should:
	 * - validate that mandatory inputs are presents
	 * - validate that the content of the inputs is coherent with your use case (e.g: validate that a date is / isn't in the past ...)
	 */
	@Override
	public void validateInputParameters() throws ConnectorValidationException {
		new InputParametersValidator(copyInputParameters()).validateInputParameters();
	}

	private Document loadDocument(final ProcessAPI processAPI) throws DocumentNotFoundException {
		final long processInstanceId = getExecutionContext().getProcessInstanceId();
		return processAPI.getLastDocument(processInstanceId, getMessageText());
	}

	private Map<String, Object> copyInputParameters() {
		final Map<String, Object> inputParameters = new HashMap<>();
		inputParameters.put(QUEUE_NAME, getInputParameter(QUEUE_NAME));
		inputParameters.put(IS_BONITA_DOCUMENT, getInputParameter(IS_BONITA_DOCUMENT));
		inputParameters.put(MESSAGE_TEXT, getInputParameter(MESSAGE_TEXT));
		inputParameters.put(URI_INPUT_PARAMETER, getInputParameter(URI_INPUT_PARAMETER));
		inputParameters.put(PROPERTIES, getInputParameter(PROPERTIES));
		return inputParameters;
	}


	/**
	 * Core method:
	 * - Execute all the business logic of your connector using the inputs (connect to an external service, compute some values ...).
	 * - Set the output of the connector execution. If outputs are not set, connector fails.
	 */
	@Override
	protected void executeBusinessLogic() throws ConnectorException {
		final org.slf4j.Logger logger = LoggerFactory.getLogger(JmsConnector.class);
		final ProcessAPI processAPI = getAPIAccessor().getProcessAPI();
		Document document;
		try {
			// Create a ConnectionFactory
			ActiveMQConnectionFactory connectionFactory;
			if (!isAnonymous()) {
				connectionFactory = new ActiveMQConnectionFactory(getUsername(), getPassword(), getUri());
			} else {
				connectionFactory = new ActiveMQConnectionFactory(getUri());
			}


			// Create a Connection
			QueueConnection connection = connectionFactory.createQueueConnection();
			connection.start();

			// Create a Session
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// Create the destination (Queue)
			Destination destination = session.createQueue(getQueueName());

			// Create a MessageProducer from the Session to the Topic or Queue
			MessageProducer producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			Message message;
			if (isBonitaDocument()) {
				document = loadDocument(processAPI);
				byte[] content = processAPI.getDocumentContent(document.getContentStorageId());
				message = session.createBytesMessage();
				((BytesMessage) message).writeBytes(content);
			} else {
				message = session.createTextMessage();
				((TextMessage) message).setText(getMessageText());
			}
			message = loadProperties(message);

			// Tell the producer to send the message
			logger.info("Sent message: " + message.hashCode() + " : " + Thread.currentThread().getName());
			producer.send(message);

			// Clean up
			session.close();
			connection.close();

			// setOutputParameter(OUTPUT_DOCUMENT_VALUE,
			// createDocumentValue(newContent,
			// MimeTypeUtil.forFormat(ConverterTypeTo.PDF.name()),
			// FilenameUtil.toOutputFileName((String)
			// getInputParameter(OUTPUT_FILE_NAME),
			// document.getContentFileName(), ConverterTypeTo.PDF.name())));
		} catch (final Exception e) {
			throw new ConnectorException(e);
		}
	}

	/**
	 * [Optional] Open a connection to remote server
	 */
	@Override
	public void connect() throws ConnectorException {
	}

	/**
	 * [Optional] Close connection to remote server
	 */
	@Override
	public void disconnect() throws ConnectorException {
	}
}
