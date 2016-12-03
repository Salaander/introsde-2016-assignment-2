package introsde.rest.ehealth.resources;

import introsde.rest.ehealth.model.HealthMeasureHistory;
import introsde.rest.ehealth.model.MeasureDefinition;
import introsde.rest.ehealth.model.Person;
import introsde.rest.ehealth.model.LifeStatus;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Stateless // only used if the the application is deployed in a Java EE container
@LocalBean // only used if the the application is deployed in a Java EE container
public class PersonResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    int id;

    EntityManager entityManager; // only used if the application is deployed in a Java EE container

    public PersonResource(UriInfo uriInfo, Request request,int id, EntityManager em) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;
        this.entityManager = em;
    }

    public PersonResource(UriInfo uriInfo, Request request,int id) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;
    }

    // Application integration
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Person getPerson() {
        Person person = this.getPersonById(id);
        if (person == null)
            throw new RuntimeException("Get: Person with " + id + " not found");
        return person;
    }

    // for the browser
    @GET
    @Produces(MediaType.TEXT_XML)
    //@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Person getPersonHTML() {
        Person person = this.getPersonById(id);
        if (person == null)
            throw new RuntimeException("Get: Person with " + id + " not found");
        System.out.println("Returning person... " + person.getIdPerson());
        return person;
    }

    @PUT
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response putPerson(Person person) {
        System.out.println("--> Updating Person... " +this.id);
        System.out.println("--> "+person.toString());
        //Person.updatePerson(person);
        Response res;
        Person existing = getPersonById(this.id);

        if (existing == null) {
            res = Response.noContent().build();
        } else {
            res = Response.created(uriInfo.getAbsolutePath()).build();
            person.setIdPerson(this.id);
            Person.updatePerson(person);
        }
        return res;
    }
    
    /*@POST
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response postPerson(Person person) {
        System.out.println("--> Creating Person... " +this.id);
        System.out.println("--> "+person.toString());
        Person.savePerson(person);
        Response res;
        Person existing = getPersonById(this.id);

        if (existing == null) {
            res = Response.noContent().build();
        } else {
            res = Response.created(uriInfo.getAbsolutePath()).build();
            person.setIdPerson(this.id);
            Person.updatePerson(person);
        }
        return res;
    }*/

    @DELETE
    public void deletePerson() {
        Person c = getPersonById(id);
        if (c == null)
            throw new RuntimeException("Delete: Person with " + id
                    + " not found");
        Person.removePerson(c);
        System.out.println("--> Person with id " + id + " has been deleted.");
    }

    public Person getPersonById(int personId) {
        System.out.println("--> Reading person from DB with id: " + personId);

        // this will work within a Java EE container, where not DAO will be needed
        //Person person = entityManager.find(Person.class, personId); 

        Person person = Person.getPersonById(personId);
        //System.out.println("Person: "+person.toString());
        return person;
    }
    
    @GET
    @Path("{measureType}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public List<HealthMeasureHistory> getPersonHistory(@PathParam("measureType") String measureName) {
    	System.out.println("--> GET Person (ID: " + id + ");  measuref for MeasuryTypeName = " + measureName + ";");
    	List<HealthMeasureHistory> allHealthMeasureHistory = HealthMeasureHistory.getAll();
    	List<HealthMeasureHistory> result = new ArrayList<HealthMeasureHistory>();
    	for(HealthMeasureHistory hmh : allHealthMeasureHistory) {
    		System.out.println(hmh.getPerson().getIdPerson() +";"+ id +";"+ hmh.getMeasureDefinition().getMeasureName() +";"+ measureName);
    		if (hmh.getPerson().getIdPerson() == id && hmh.getMeasureDefinition().getMeasureName().equals(measureName)) {
    			result.add(hmh);
    		}
    	}
    	return result;
    }
    
    // Request #8: POST /person/{id}/{measureType} should save a new value for the {measureType}
    // (e.g. weight) of person identified by {id} and archive the old value in the history
    @POST
    @Path("{measureType}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public LifeStatus newMeasureValue(HealthMeasureHistory hmh, @PathParam("measureType") String measureName){
    	Person person = this.getPersonById(id);
    	
    	//searches the measure definition associated with the name of the measure
		MeasureDefinition measureDefinition = new MeasureDefinition();
		measureDefinition = MeasureDefinition.getMeasureDefinitionByName(measureName);
		
		//remove actual 'lifestatus' for measureName
		LifeStatus lifeStatus = LifeStatus.getLifeStatusByMeasureDefPerson(measureDefinition,person);
		if(lifeStatus != null)
			LifeStatus.removeLifeStatus(lifeStatus);
		
		//save new 'lifestatus' for measureName
		LifeStatus newLifeStatus = new LifeStatus(person, measureDefinition, hmh.getValue());
		newLifeStatus = LifeStatus.saveLifeStatus(newLifeStatus);
		
		//insert the new measure value in the history
		hmh.setPerson(person);
		hmh.setMeasureDefinition(measureDefinition);
		HealthMeasureHistory.saveHealthMeasureHistory(hmh);
		
    	return LifeStatus.getLifeStatusById(newLifeStatus.getIdMeasure());
    }
    
    @GET
    @Path("{measureType}/{mid}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public HealthMeasureHistory getPersonHistory(@PathParam("measureType") String measureName, @PathParam("mid") String mid) {
    	System.out.println("--> GET Person (ID: " + id + ");  measuref for MeasuryTypeName = " + measureName + "; where mid = " + mid);
    	HealthMeasureHistory result = HealthMeasureHistory.getHealthMeasureHistoryById(Integer.parseInt(mid));
    	return result;
    }
}