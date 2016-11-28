package introsde.rest.ehealth.wrapper;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

import introsde.rest.ehealth.model.*;
/**
 * Wrapper used when are listened the measureName of all the MeasureDefinition
 *
 */
@XmlRootElement(name="measureTypes")
public class MeasureDefinitionWrapper {
	
	@XmlElement(name="measureType")
	@JsonProperty("measureTypes")
	public List<String> measureTypes = new ArrayList<String>();
	
	public void setMeasureTypes(List<String> measureTypes) {
		this.measureTypes = measureTypes;
	}
	
	
}