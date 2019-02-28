/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hashcode.mdb;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;

import hashcode.model.ConvertModel;
import hashcode.model.Structure;

/**
 * <p>
 * A simple Message Driven Bean that asynchronously receives and processes the
 * messages that are sent to the queue.
 * </p>
 *
 * @author Serge Pagop (spagop@redhat.com)
 */
@JMSDestinationDefinitions(value = {
		@JMSDestinationDefinition(name = "java:/queue/RESULT", interfaceName = "javax.jms.Queue", destinationName = "ResultQueue") })

@MessageDriven(name = "ResultMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/RESULT"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
		@ActivationConfigProperty(propertyName = "maxSession", propertyValue = "10") })
public class ResultMDB implements MessageListener {

	private Logger LOGGER = Logger.getLogger(this.getClass().getName());

	private ConvertModel convert = new ConvertModel();

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(Message rcvMessage) {
		TextMessage msg = null;
		try {
			if (rcvMessage instanceof TextMessage) {
				msg = (TextMessage) rcvMessage;
				String correlationId = msg.getJMSCorrelationID();
				LOGGER.info("Received Message: " + correlationId);
				String inputTest = msg.getText();
				LOGGER.info("FIN pour: " + inputTest);
				Structure struct = convert.toObject(inputTest, Structure.class);
				writeStructure("c:/Hascode", correlationId, struct);
			} else {
				LOGGER.warning("Message of wrong type: " + rcvMessage.getClass().getName());
			}
		} catch (JMSException | JAXBException | IOException e) {
			throw new RuntimeException(e);
		}

		try {
			Thread.sleep(Math.round((Math.random() * 10000)));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void writeStructure(String path, String correlationId, Structure struct) throws IOException, FileNotFoundException {
		try (BufferedOutputStream out = IOUtils.buffer(new FileOutputStream(new File(
				path + "/result_" + struct.getScore() + "-" + correlationId + ".txt")))) {
			IOUtils.write(struct.getSlides().size() + "\r\n", out, Charset.defaultCharset());
			IOUtils.writeLines(struct.getSlides(), "\r\n", out, Charset.defaultCharset());
		}
	}
}
