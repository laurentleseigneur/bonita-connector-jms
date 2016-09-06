/**
 * Copyright (C) 2014 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/

package org.bonitasoft.connectors.jms;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.bpm.document.Document;
import org.bonitasoft.engine.exception.BonitaException;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

/**
 * The class for the UTs of the REST Connector
 */
public class JMSConnectorTest extends AcceptanceTestBase {

   

  

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

   
    
   

    /**
     * Execute a connector call
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
     * @throws BonitaException exception
     * @throws InterruptedException exception
     */
    @Test
    public void sendDocument() throws BonitaException, InterruptedException, IOException {
    	
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
