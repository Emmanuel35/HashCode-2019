package hashcode.model;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

@Stateless
public class ConvertModel 
{
	private HashMap<Class, Marshaller> marshallers = new HashMap<Class, Marshaller>();
	private HashMap<Class, Unmarshaller> unmarshallers = new HashMap<Class, Unmarshaller>();
	
	/**
	 * JAXB Object to XML/JSON
	 * @param object
	 * @return
	 * @throws JAXBException
	 */
	public <T> String toString(T object) throws JAXBException {
		Marshaller marshallObj = null;
		
		if (!marshallers.containsKey(object.getClass())) {
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
}
