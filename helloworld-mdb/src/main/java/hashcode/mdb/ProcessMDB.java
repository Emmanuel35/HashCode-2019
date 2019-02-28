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
				
				// Il reste des photos à comparer
				if (struct.getPhotos().size() >0) {
					// Construit le slide de référence
					Photo photoCourante = struct.getSlideCourant().get(0);
					Slide slideCourant = new Slide();
					slideCourant.setPremierePhoto(photoCourante);
					
					// Stocke tous les résultats
					int max = 0;
					HashMap<Integer, Slide> comparaison = new HashMap<Integer, Slide>();
					
					// parcours les photos restantes
					for(Photo photo: struct.getPhotos()) {
						
						// Test si horizontale
						if (photo.getHorizontal()) {
							Slide slide = new Slide();
							slide.setPremierePhoto(photo);
							LOGGER.info("Calcul entre "+slideCourant.getPremierePhoto().getId()+" et "+slide.getPremierePhoto().getId());
							// calcul du score
							Integer result = Score.computeScore(slideCourant, slide);
							comparaison.put(result, slide);
						}
						
						// TODO pour 2 verticales
					}
					
					// Publie nbValeurs en messages 
					int nbValeurs = 5;
					for(Integer index: comparaison.keySet().stream().sorted().collect(Collectors.toList())) {
						if (nbValeurs-- >0) {
							
							Slide slideAsuivre = comparaison.get(index);
							ArrayList<Photo> photosAgarder = new ArrayList<Photo>();
							photosAgarder.add(slideAsuivre.getPremierePhoto());
							
							// recopie la structure en enlevant la photo courante
							Structure newStructure = new Structure();
							newStructure.setPhotos(struct.getPhotos());
							newStructure.getPhotos().removeAll(photosAgarder);
							newStructure.setScore(struct.getScore()+index);
							
							newStructure.setSlideCourant(photosAgarder);
							
							// TODO pour 2 photos
							newStructure.getSlides().add(""+photosAgarder.get(0).getId());
							
							// publication 
							context.createProducer()
								.setJMSCorrelationID(msg.getJMSCorrelationID())
								.send(process,convert.toString(newStructure));		
						}
					}
					
					if (comparaison.isEmpty()) {
						LOGGER.info("Plus rien. Envoi pour "+rcvMessage.getJMSCorrelationID());
						// publication 
						context.createProducer()
							.setJMSCorrelationID(msg.getJMSCorrelationID())
							.send(result,convert.toString(struct));		
					}
				} else {
					LOGGER.info("Resultat pour "+rcvMessage.getJMSCorrelationID());
					// publication 
					context.createProducer()
						.setJMSCorrelationID(msg.getJMSCorrelationID())
						.send(result,convert.toString(struct));		
				}
					
			} else {
				LOGGER.warning("Message of wrong type: " + rcvMessage.getClass().getName());
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}

	}
}
