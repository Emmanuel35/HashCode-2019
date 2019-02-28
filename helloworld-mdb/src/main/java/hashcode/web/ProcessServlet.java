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
package hashcode.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import hashcode.model.ConvertModel;
import hashcode.model.Photo;
import hashcode.model.Score;
import hashcode.model.Slide;
import hashcode.model.Structure;

/**
 * Definition of the two JMS destinations used by the quickstart
 * (one queue and one topic).
 */
//@JMSDestinationDefinitions(
//    value = {
//        @JMSDestinationDefinition(
//            name = "java:/queue/HELLOWORLDe",
//            interfaceName = "javax.jms.Queue",
//            destinationName = "HelloWorldQueue"
//        ),
//        @JMSDestinationDefinition(
//            name = "java:/topic/HELLOWORLDMDBTopic",
//            interfaceName = "javax.jms.Topic",
//            destinationName = "HelloWorldTopic"
//        )
//    }
//)

/**
 * <p>
 * A simple servlet 3 as client that sends several messages to a queue or a
 * topic.
 * </p>
 *
 * <p>
 * The servlet is registered and mapped to /HelloWorldMDBServletClient using the
 * {@linkplain WebServlet
 * 
 * @HttpServlet}.
 *                </p>
 *
 * @author Serge Pagop (spagop@redhat.com)
 *
 */
@WebServlet("/ProcessServlet")
public class ProcessServlet extends HttpServlet {

	private static final long serialVersionUID = -8314035702649252239L;

	private static final int MSG_COUNT = 2;

	private Logger LOGGER = Logger.getLogger(this.getClass().getName());

	@Inject
	private JMSContext context;

	@Resource(lookup = "java:/queue/HELLOWORLD")
	private Queue queue;

	private ConvertModel convert = new ConvertModel();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Scanner scanner = new Scanner(req.getInputStream());
		int lineCount = scanner.nextInt();
		List<Photo> photos = new ArrayList<>();
		for (int i = 0; i < lineCount; i++) {
			String orientation = scanner.next();
			int tagCount = scanner.nextInt();
			List<String> tags = new ArrayList<>();
			for (int j = 0; j < tagCount; j++) {
				tags.add(scanner.next());
			}
			Photo photo = new Photo(i, "H".equals(orientation) ? Boolean.TRUE : Boolean.FALSE, tags);
			photos.add(photo);
		}
		scanner.close();

		// Tri des photos par taille de tags
		photos.sort( (Photo p1, Photo p2) -> Integer.compare(p1.getTags().size(), p2.getTags().size()) );
		
		for(Photo photo: photos)
			try {
				// tous sauf la photo courante
				List<Photo> reste = new ArrayList<Photo>();
				reste.addAll(photos);
				reste.remove(photo);
				
				Structure structure = new Structure();
				structure.setPhotos(reste);
				structure.setScore(0);
				structure.getSlideCourant().add(photo);
				structure.getSlides().add(""+photo.getId());
				
				// TODO ajouter verticales
				if (photo.getHorizontal()) {
					LOGGER.info("Publication Horizontale:"+photo.getId());
					context.createProducer()
						.setJMSCorrelationID(UUID.randomUUID().toString())
						.send(queue,convert.toString(structure));
				}
				
			} catch (JAXBException e) {
				LOGGER.severe(e.getMessage());
				throw new IOException("Can't produce JSON", e);
			}
		
		try {
			publishVerticalMessages(photos);
		} catch (JAXBException e) {
			LOGGER.severe(e.getMessage());
		}
	}

	/**
	 * Publie un message Vertical
	 * @param photos
	 * @throws JAXBException
	 */
	private void publishVerticalMessages(List<Photo> photos) throws JAXBException {
		ArrayList<Photo> aPublier = new ArrayList<Photo>();
		
		Slide slideVertical = new Slide();
		slideVertical = Score.getNextVerticalSlide(photos);
		
		if (slideVertical == null)
			return;
		
		ArrayList<Photo> photosSlide = new ArrayList<Photo>();
		photosSlide.add(slideVertical.getPremierePhoto());
		photosSlide.add(slideVertical.getSecondePhoto());
		
		aPublier.removeAll(photosSlide);
		
		Structure structure = new Structure();
		structure.setPhotos(aPublier);
		structure.setScore(0);
		structure.setSlideCourant(photosSlide);
		structure.getSlides().add(slideVertical.toString());
		
		LOGGER.info("Publication Verticale:"+slideVertical.toString());
		
		context.createProducer()
				.setJMSCorrelationID(UUID.randomUUID().toString())
				.send(queue, convert.toString(structure));
		
		// Envoie du reste
		if (!aPublier.isEmpty())
			publishVerticalMessages(aPublier);
	}

}
