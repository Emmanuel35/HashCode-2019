package hashcode.model;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

@Stateless
@LocalBean
public class ConvertModel 
{
	private HashMap<Class, Marshaller> marshallers = new HashMap<Class, Marshaller>();
	private HashMap<Class, Unmarshaller> unmarshallers = new HashMap<Class, Unmarshaller>();
	
	private Logger LOGGER = Logger.getLogger(this.getClass().getName());
	
	/**
	 * JAXB Object to XML/JSON
	 * @param object
	 * @return
	 * @throws JAXBException
	 */
	public <T> String toString(T object) throws JAXBException {
		Marshaller marshallObj = null;
		
		if (!marshallers.containsKey(object.getClass())) {
			LOGGER.info("No Marshaller for "+object.getClass().getName());
			
			// evite les accès concurrents
			synchronized (marshallers) {
				//creating the JAXB context
			    JAXBContext jContext = JAXBContext.newInstance(object.getClass());
			    //creating the marshaller object
			    marshallObj = jContext.createMarshaller();
			    //setting the property to show xml format output
			    marshallObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			    
			    marshallers.put(object.getClass(), marshallObj);
			}
		}
		
		// get it
		marshallObj = marshallers.get(object.getClass());
		
	    //return it
		StringWriter sw = new StringWriter();
	    marshallObj.marshal(object, sw);
	    
	    return sw.toString();
	}
	
	/**
	 * JSON/XML to Object 
	 * @param textValue
	 * @return
	 * @throws JAXBException
	 */
	public <T> T toObject(String textValue, Class targetType) throws JAXBException {
		Unmarshaller unmarshallerObj = null;
		
		if (!unmarshallers.containsKey(targetType)) {
			LOGGER.info("No Unmarshaller for "+targetType.getName());
			
			// evite les accès concurrents
			synchronized (unmarshallers) {
				JAXBContext jContext = JAXBContext.newInstance(targetType);
			    //creating the unmarshall object
			    unmarshallerObj = jContext.createUnmarshaller();
			    
			    unmarshallers.put(targetType, unmarshallerObj);
			}
		}
		
		// get it
		unmarshallerObj = unmarshallers.get(targetType);
		
	    //return it	    
		StringReader sw = new StringReader(textValue);
	    return (T) unmarshallerObj.unmarshal(sw);
	}
	
	public <T> String toFile(T object, String fileName) throws JAXBException {
		Marshaller marshallObj = null;
		
		if (!marshallers.containsKey(object.getClass())) {
			LOGGER.info("No Marshaller for "+object.getClass().getName());
			
			// evite les accès concurrents
			synchronized (marshallers) {
				//creating the JAXB context
			    JAXBContext jContext = JAXBContext.newInstance(object.getClass());
			    //creating the marshaller object
			    marshallObj = jContext.createMarshaller();
			    //setting the property to show xml format output
			    marshallObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			    
			    marshallers.put(object.getClass(), marshallObj);
			}
		}
		
		// get it
		marshallObj = marshallers.get(object.getClass());
		
	    //return it
		StringWriter sw = new StringWriter();
	    marshallObj.marshal(object, new File(fileName));
	    
	    return sw.toString();
	}
}
