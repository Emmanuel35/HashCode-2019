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

<<<<<<< Updated upstream
=======
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;

import hashcode.model.ConvertModel;
import hashcode.model.Photo;
import hashcode.model.Score;
import hashcode.model.Slide;
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
		@JMSDestinationDefinition(name = "java:/queue/HELLOWORLD", interfaceName = "javax.jms.Queue", destinationName = "HelloWorldQueue") })

@MessageDriven(name = "ProcessMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/HELLOWORLD"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
		@ActivationConfigProperty(propertyName = "maxSession", propertyValue = "100") })
public class ProcessMDB implements MessageListener {

	private Logger LOGGER = Logger.getLogger(this.getClass().getName());

	@Resource(lookup = "java:/queue/HELLOWORLD")
	private Queue process;

	private ConvertModel convert = new ConvertModel();

	@Resource(lookup = "java:/queue/RESULT")
	private Queue result;

	@Inject
	private JMSContext context;

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(Message rcvMessage) {
		TextMessage msg = null;
		try {

			if (rcvMessage instanceof TextMessage) {
				msg = (TextMessage) rcvMessage;
				LOGGER.info("Received Message: " + msg.getJMSCorrelationID());

				Structure struct = convert.toObject(msg.getText(), Structure.class);
				
				// Construit le slide de référence
				Photo photoCourante = struct.getSlideCourant().get(0);
				Slide slideCourant = new Slide();
				slideCourant.setPremierePhoto(photoCourante);
				
				// Stocke tous les résultats
				int max = 0;
				HashMap<Integer, Slide> comparaison = new HashMap<Integer, Slide>();
				
				// parcours les photos restantes
				for(Photo photo: struct.getPhotos()) {
							
					Slide slide = new Slide();
					slide.setPremierePhoto(photo);
					// calcul du score
					Integer result = Score.computeScore(null, null);
					comparaison.put(result, slide);
				}
				
				// Publie nbValeurs en messages 
				int nbValeurs = 5;
				for(Integer index: comparaison.keySet().stream().sorted().collect(Collectors.toList())) {
					if (nbValeurs-- >0) {
						
						Structure structure = new Structure();
						structure.setPhotos(reste);
						
					}
				}
				
					// tous sauf la photo courante
						List<Photo> reste = new ArrayList<Photo>();
						reste.addAll(struct.getPhotos());
						reste.remove(photo);
						
						
						
						Slide slideCourant = new Slide();
						slideCourant.setPremierePhoto(photo);
						
						
						structure.setScore(
								structure.getScore() + Score.computeScore(null, null)
								);
						structure.getSlideCourant().add(photo);
						structure.getSlides().add(""+photo.getId());
						
						context.createProducer()
							.setJMSCorrelationID(UUID.randomUUID().toString())
							.send(process,convert.toString(structure));						
					}
				
				if (struct.getPhotos().size() > 0)
					context.createProducer()
						.setJMSCorrelationID(msg.getJMSCorrelationID())
						.send(process,convert.toString(struct));
				else
					context.createProducer()
						.setJMSCorrelationID(msg.getJMSCorrelationID())
						.send(result,convert.toString(struct));
			} else {
				LOGGER.warning("Message of wrong type: " + rcvMessage.getClass().getName());
			}
		} catch (JMSException | JAXBException e) {
			LOGGER.severe(e.getMessage());
			throw new RuntimeException(e);
		}

		try {
			Thread.sleep(Math.round((Math.random() * 10000)));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
