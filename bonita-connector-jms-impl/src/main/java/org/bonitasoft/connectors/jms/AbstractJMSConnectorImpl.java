package org.bonitasoft.connectors.jms;

import org.bonitasoft.engine.connector.AbstractConnector;
import org.bonitasoft.engine.connector.ConnectorValidationException;

public abstract class AbstractJMSConnectorImpl extends AbstractConnector {

	protected final static String QUEUE_NAME = "queueName";
	protected final static String URI_INPUT_PARAMETER = "uri";
	protected final static String IS_BONITA_DOCUMENT = "isBonitaDocument";
	protected final static String MESSAGE_TEXT = "message";
	protected final static String PROPERTIES = "properties";
	protected final static String USERNAME = "username";
	protected final static String PASSWORD = "password";
	protected final static String IS_ANONYMOUS = "anonymous";


	protected final java.lang.String getUri() {
		return (java.lang.String) getInputParameter(URI_INPUT_PARAMETER);
	}

	protected final java.lang.String getQueueName() {
		return (java.lang.String) getInputParameter(QUEUE_NAME);
	}

	protected final java.lang.Boolean isBonitaDocument() {
		return (java.lang.Boolean) getInputParameter(IS_BONITA_DOCUMENT);
	}

	protected final java.lang.String getMessageText() {
		return (java.lang.String) getInputParameter(MESSAGE_TEXT);
	}

	protected final java.util.List getProperties() {
		return (java.util.List) getInputParameter(PROPERTIES);
	}
	
	protected final java.lang.String getUsername() {
		return (java.lang.String) getInputParameter(USERNAME);
	}
	
	protected final java.lang.String getPassword() {
		return (java.lang.String) getInputParameter(PASSWORD);
	}
	
	protected final java.lang.Boolean isAnonymous() {
		return (java.lang.Boolean) getInputParameter(IS_ANONYMOUS);
	}

    
    @Override
    public void validateInputParameters() throws ConnectorValidationException {
        try {
            getUri();
        } catch (ClassCastException cce) {
            throw new ConnectorValidationException("uri type is invalid");
        }
        try {
        	getQueueName();
        } catch (ClassCastException cce) {
            throw new ConnectorValidationException("queueName type is invalid");
        }
        try {
        	isBonitaDocument();
        } catch (ClassCastException cce) {
            throw new ConnectorValidationException("isBonitaDocument type is invalid");
        }
        try {
        	getMessageText();
        } catch (ClassCastException cce) {
            throw new ConnectorValidationException("messageText type is invalid");
        }

    }

}
