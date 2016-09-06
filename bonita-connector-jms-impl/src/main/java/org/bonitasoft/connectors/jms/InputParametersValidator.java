package org.bonitasoft.connectors.jms;

import java.util.Map;

import org.bonitasoft.engine.connector.ConnectorValidationException;

public class InputParametersValidator {

	 private final Map<String, Object> inputParameters;

	public InputParametersValidator(final Map<String, Object> inputParameters) {
		this.inputParameters = inputParameters;
	}
	
	 public void validateInputParameters() throws ConnectorValidationException {
		 validateMessageTextInput();
	 }
	 
	private void validateMessageTextInput() throws ConnectorValidationException {
		final Object sourceDocumentName = inputParameters.get(JMSConnector.MESSAGE_TEXT);
		if (sourceDocumentName == null) {
			throw new ConnectorValidationException(
					String.format("Input parameter %s cannot be null.", JMSConnector.MESSAGE_TEXT));
		}
		
	}
}
