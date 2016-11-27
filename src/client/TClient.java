package client;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.json.JSONObject;

public class TClient {
	
	private WebTarget service = null;
	private ClientConfig clientConfig;
	private Client client;
	private String mediaType;
	
	private String user_created;
	
	private TClient() {
		mediaType = MediaType.APPLICATION_JSON;
		String url = "http://localhost:5900/sdelab";
		clientConfig = new ClientConfig();
		client = ClientBuilder.newClient(clientConfig);
		service = client.target(getBaseURI(url));
	}
	
	private static URI getBaseURI(String uriServer) {
		return UriBuilder.fromUri(uriServer).build();
	}
	
	// done -> yes
	/**
	 * GET /person should list all the people (see above Person model to know what data to return here) in your database (wrapped under the root element "people")
	 */
	public void Request1() {
		System.out.println("-- Executing Request #1. GET all people");
		WebTarget resourceWebTarget = service.path("person");
		Response r = resourceWebTarget.request().accept(mediaType).get(Response.class);
		
		if(r.getStatus() == 200) {
			String output = r.readEntity(String.class);
			System.out.println(output);
			System.out.println("-- OK -- Users fetched successfully.");
		} else {
			System.out.println(r.getStatus());
		}
	}
	
	// done -> yes
	/**
	 * GET /person/{id} should give all the personal information plus current measures of person identified by {id} (e.g., current measures means current health profile)
	 */
	public void Request2() {
		System.out.println("-- Executing Request #2. GET people with ID 1");
		WebTarget resourceWebTarget = service.path("person/1");
		Response r = resourceWebTarget.request().accept(mediaType).get(Response.class);
		
		if(r.getStatus() == 200) {
			String output = r.readEntity(String.class);
			System.out.println(output);
			System.out.println("-- OK -- User fetched successfully.");
		} else {
			System.out.println(r.getStatus());
		}
	}
	
	// done -> yes
	/**
	 * PUT /person/{id} should update the personal information of the person identified by {id} (e.g., only the person's information, not the measures of the health profile)
	 */
	public void Request3() {
		System.out.println("-- Executing Request #3. PUT update person with ID #");
		WebTarget resourceWebTarget = service.path("person");
		
		String json_payload = "{\"name\":\"Elso\",\"lastname\":\"Probam212\",\"username\":\"userneveitt\",\"birthdate\":\"1945-01-01\",\"email\":\"teszt@teszt.hu\"}";
		Response r = resourceWebTarget.request().accept(mediaType).get(Response.class);
		
		if(r.getStatus() == 200) {
			String output = r.readEntity(String.class);
			System.out.println(output);
			System.out.println("-- OK -- User modified successfully.");
		} else {
			System.out.println(r.getStatus());
		}
	}
	
	// done -> no, still need to add measure type add if json contains it
	/**
	 * POST /person should create a new person and return the newly created person with its assigned id (if a health profile is included, create also those measurements for the new person).
	 */
	public void Request4() {
		System.out.println("-- Executing Request #4. POST add new person to the DB");
		WebTarget resourceWebTarget = service.path("person");
		
		String json_payload = "{\"name\":\"Elso\",\"lastname\":\"Probam212\",\"username\":\"userneveitt\",\"birthdate\":\"1945-01-01\",\"email\":\"teszt@teszt.hu\"}";
		Response r = resourceWebTarget.request().accept(mediaType).post(Entity.entity(json_payload, mediaType), Response.class);
		
		if(r.getStatus() == 200) {
			String output = r.readEntity(String.class);
			JSONObject jsonObj = new JSONObject(output);
			if (jsonObj.get("idPerson") != null ){
				user_created = String.valueOf(jsonObj.getInt("idPerson"));
			}
			System.out.println(output);
			System.out.println("-- OK -- User created successfully.");
		} else {
			System.out.println(r.getStatus());
		}
	}
	
	// done -> yes
	/**
	 * DELETE /person/{id} should delete the person identified by {id} from the system
	 */
	public void Request5() {
		System.out.println("-- Executing Request #5. DELETE delete person with ID #");
		WebTarget resourceWebTarget = service.path("person/" + user_created);
		Response r = resourceWebTarget.request().accept(mediaType).delete(Response.class);
		
		if(r.getStatus() == 200) {
			String output = r.readEntity(String.class);
			System.out.println(output);
			System.out.println("-- OK -- User deleted successfully.");
		} else {
			System.out.println(r.getStatus());
		}
	}
	
	// done -> yes
	/**
	 * GET /person/{id}/{measureType} should return the list of values (the history) of {measureType} (e.g. weight) for person identified by {id}
	 */
	public void Request6() {
		System.out.println("-- Executing Request #1. GET all people");
		WebTarget resourceWebTarget = service.path("person");
		Response r = resourceWebTarget.request().accept(mediaType).get(Response.class);
		
		if(r.getStatus() == 200) {
			String output = r.readEntity(String.class);
			System.out.println(output);
			System.out.println("-- OK -- Users fetched successfully.");
		} else {
			System.out.println(r.getStatus());
		}
	}
	
	// todo: implement
	/**
	 * GET /person/{id}/{measureType}/{mid} should return the value of {measureType} (e.g. weight) identified by {mid} for person identified by {id}
	 */
	public void Request7() {
		System.out.println("-- Executing Request #1. GET all people");
		WebTarget resourceWebTarget = service.path("person");
		Response r = resourceWebTarget.request().accept(mediaType).get(Response.class);
		
		if(r.getStatus() == 200) {
			String output = r.readEntity(String.class);
			System.out.println(output);
			System.out.println("-- OK -- Users fetched successfully.");
		} else {
			System.out.println(r.getStatus());
		}
	}
	
	// todo: implement
	/**
	 * POST /person/{id}/{measureType} should save a new value for the {measureType} (e.g. weight) of person identified by {id} and archive the old value in the history
	 */
	public void Request8() {
		System.out.println("-- Executing Request #1. GET all people");
		WebTarget resourceWebTarget = service.path("person");
		Response r = resourceWebTarget.request().accept(mediaType).get(Response.class);
		
		if(r.getStatus() == 200) {
			String output = r.readEntity(String.class);
			System.out.println(output);
			System.out.println("-- OK -- Users fetched successfully.");
		} else {
			System.out.println(r.getStatus());
		}
	}
	
	// todo: implement
	/**
	 * GET /measureTypes should return the list of measures your model supports in the following formats:
	 */
	public void Request9() {
		System.out.println("-- Executing Request #1. GET all people");
		WebTarget resourceWebTarget = service.path("person");
		Response r = resourceWebTarget.request().accept(mediaType).get(Response.class);
		
		if(r.getStatus() == 200) {
			String output = r.readEntity(String.class);
			System.out.println(output);
			System.out.println("-- OK -- Users fetched successfully.");
		} else {
			System.out.println(r.getStatus());
		}
	}
	
	// todo: implement
	/**
	 * PUT /person/{id}/{measureType}/{mid} should update the value for the {measureType} (e.g., weight) identified by {mid}, related to the person identified by {id}
	 */
	public void Request10() {
		System.out.println("-- Executing Request #1. GET all people");
		WebTarget resourceWebTarget = service.path("person");
		Response r = resourceWebTarget.request().accept(mediaType).get(Response.class);
		
		if(r.getStatus() == 200) {
			String output = r.readEntity(String.class);
			System.out.println(output);
			System.out.println("-- OK -- Users fetched successfully.");
		} else {
			System.out.println(r.getStatus());
		}
	}
	
	// todo: implement
	/**
	 * GET /person/{id}/{measureType}?before={beforeDate}&after={afterDate} should return the history of {measureType} (e.g., weight) for person {id} in the specified range of date
	 */
	public void Request11() {
		System.out.println("-- Executing Request #1. GET all people");
		WebTarget resourceWebTarget = service.path("person");
		Response r = resourceWebTarget.request().accept(mediaType).get(Response.class);
		
		if(r.getStatus() == 200) {
			String output = r.readEntity(String.class);
			System.out.println(output);
			System.out.println("-- OK -- Users fetched successfully.");
		} else {
			System.out.println(r.getStatus());
		}
	}
	
	// todo: implement
	/**
	 * GET /person?measureType={measureType}&max={max}&min={min} retrieves people whose {measureType} (e.g., weight) value is in the [{min},{max}] range (if only one for the query parameters is provided, use only that)
	 */
	public void Request12() {
		System.out.println("-- Executing Request #1. GET all people");
		WebTarget resourceWebTarget = service.path("person");
		Response r = resourceWebTarget.request().accept(mediaType).get(Response.class);
		
		if(r.getStatus() == 200) {
			String output = r.readEntity(String.class);
			System.out.println(output);
			System.out.println("-- OK -- Users fetched successfully.");
		} else {
			System.out.println(r.getStatus());
		}
	}
	
	public static void main(String[] args)
    {
    	System.out.println("Client is called");
    	TClient tc = new TClient();
    	tc.Request1();
    	tc.Request2();
    	tc.Request3();
    	tc.Request4();
    	//tc.Request5();
    }
}
