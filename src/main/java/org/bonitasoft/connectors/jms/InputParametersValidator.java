package org.bonitasoft.connectors.jms;

import org.bonitasoft.engine.connector.ConnectorValidationException;

import java.util.Map;

public class InputParametersValidator {
	private Map<String, Object> inputParameters;

	public InputParametersValidator(Map<String, Object> inputParameters) {
		this.inputParameters = inputParameters;
	}

	public void validateInputParameters() throws ConnectorValidationException {
		validateMessageTextInput();
	}

	private void validateMessageTextInput() throws ConnectorValidationException {
		final Object sourceDocumentName = inputParameters.get(JmsConnector.MESSAGE_TEXT);
		if (sourceDocumentName == null) {
			throw new ConnectorValidationException(
					String.format("Input parameter %s cannot be null.", JmsConnector.MESSAGE_TEXT));
		}

	}
}
