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
import java.io.PrintWriter;
import java.util.UUID;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import hashcode.mdb.ProcessMDB;
import hashcode.model.ConvertModel;
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
 * A simple servlet 3 as client that sends several messages to a queue or a topic.
 * </p>
 *
 * <p>
 * The servlet is registered and mapped to /HelloWorldMDBServletClient using the {@linkplain WebServlet
 * @HttpServlet}.
 * </p>
 *
 * @author Serge Pagop (spagop@redhat.com)
 *
 */
@Path("/process")
public class Process {

    private static final long serialVersionUID = -8314035702649252239L;

    private static final int MSG_COUNT = 2;

    private Logger LOGGER = Logger.getLogger(this.getClass().getName());
    
    @Inject
    private JMSContext context;

    @Resource(lookup = "java:/queue/HELLOWORLD")
    private Queue queue;

    @EJB
    private ConvertModel convert;
    
    @POST
    @Path("/start")
    @Produces({"application/xml","application/json"})
    public void start(Structure structure) throws JAXBException {
    	
		for(int i=0; i<MSG_COUNT; i++) {
			context.createProducer()
				.setJMSCorrelationID(UUID.randomUUID().toString())
				.send(queue, convert.toString(structure));
		}
		
    }

}
