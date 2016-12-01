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
	private String url;
	
	private TClient() {
		mediaType = MediaType.APPLICATION_JSON;
		url = "http://localhost:5900/sdelab";
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
	 * 
	 * Comment: since we need to use the PUT method we need to fetch the person data before,
	 * since put overrides the whole person if we do not send all the data together
	 * 
	 * @throws Exception 
	 */
	public void Request3() throws Exception {
		System.out.println("-- Executing Request #3. PUT update person with ID " + user_created);
		WebTarget resourceWebTarget = service.path("person/" + user_created);
		
		// Get the user data
		Response r1 = resourceWebTarget.request().accept(mediaType).get(Response.class);
		
		JSONObject payload = new JSONObject();
		if(r1.getStatus() == 200) {
			String output = r1.readEntity(String.class);
			payload = new JSONObject(output);
			
			System.out.println(output);
			System.out.println("-- OK -- User fetched successfully.");
		} else {
			System.out.println(r1.getStatus());
			throw new Exception("No person with id " + user_created + " found.");
		}
		
		resourceWebTarget = service.path("person");
		// Send the new user data		
		payload.put("username", "modositottam");
		//payload.put("birthdate", "1990-01-01 00:00:00");
		payload.remove("lifeStatus");
		System.out.println(payload.toString());
		Response r2 = resourceWebTarget.request().accept(mediaType).put(Entity.entity(payload.toString(), mediaType), Response.class);
		
		// Check the HTTP result, return codes 200 and 201 means OK
		if(r2.getStatus() == 200 || r2.getStatus() == 201) {
			String output = r2.readEntity(String.class);
			System.out.println(output);
			System.out.println("-- OK -- User modified successfully.");
		} else {
			System.out.println(r2.getStatus());
		}
	}
	
	// done -> no, still need to add measure type add if json contains it
	/**
	 * POST /person should create a new person and return the newly created person with its assigned id (if a health profile is included, create also those measurements for the new person).
	 */
	public void Request4() {
		System.out.println("-- Executing Request #4. POST add new person to the DB");
		WebTarget resourceWebTarget = service.path("person");
		
		String json_payload = "{\"firstname\":\"Elso\",\"lastname\":\"Probam212\",\"username\":\"userneveitt\",\"birthdate\":\"1982-06-08 18:00:00\",\"email\":\"teszt@teszt.hu\"}";
		Response r = resourceWebTarget.request().accept(mediaType).post(Entity.entity(json_payload, mediaType), Response.class);
		
		if(r.getStatus() == 200) {
			String output = r.readEntity(String.class);
			JSONObject jsonObj = new JSONObject(output);
			if (jsonObj.get("idPerson") != null ){
				user_created = String.valueOf(jsonObj.getInt("idPerson"));
			}
			System.out.println(output);
			System.out.println("-- OK -- User created successfully with ID " + user_created);
		} else {
			System.out.println(r.getStatus());
		}
	}
	
	// done -> yes
	/**
	 * DELETE /person/{id} should delete the person identified by {id} from the system
	 */
	public void Request5() {
		System.out.println("-- Executing Request #5. DELETE delete person with ID " + user_created);
		WebTarget resourceWebTarget = service.path("person/" + user_created);
		Response r = resourceWebTarget.request().accept(mediaType).delete(Response.class);
		
		if(r.getStatus() == 204) {
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
	
	// done -> yes
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
	
	// done -> yes
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
	
	// done -> yes
	/**
	 * GET /measureTypes should return the list of measures your model supports in the following formats:
	 */
	public void Request9() {
		System.out.println("-- Executing Request #9. GET measureTypes");
		WebTarget resourceWebTarget = service.path("measureTypes");
		Response r = resourceWebTarget.request().accept(mediaType).get(Response.class);
		
		if(r.getStatus() == 200) {
			String output = r.readEntity(String.class);
			System.out.println(output);
			System.out.println("-- OK -- measureTypes fetched successfully.");
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
	
	public void ResponseTemplate(Integer num, String method, String url, String accept, String content, String result, String status, String body) {
		System.out.println("Request #"+num+": "+method+" "+this.url+" Accept: "+accept+" Content-type: "+content+" ");
		System.out.println("=> Result: "+result);
		System.out.println("=> HTTP Status: "+status+"");
		System.out.println(body);
	}
	
	public void Client1() {
		ResponseTemplate(1, "GET", "", "JSON", "", "", "", "");
	}
	
	public static void main(String[] args) throws Exception
    {
    	System.out.println("Client is called");
    	TClient tc = new TClient();
//    	tc.Request1(); // fetch all people
//    	tc.Request2(); // fetch specified person
//    	tc.Request4(); // create a test person
//    	tc.Request3(); // modify the created person
//    	tc.Request5(); // delete the created person
//    	//tc.Request6();
//    	//tc.Request7();
//    	//tc.Request8();
//    	tc.Request9(); // get measureTypes
    	
    	// Client tasks
    	// Implement a client that can send all of these requests and print the responses.
    	// Implement ANT target (ant execute.client), which does all the calls sequentially and save the requests/responses information into a file
    	// (e.g. client-server-json.log and client-server-xml.log, push this file to a Github repository).
    	
    	//print url that you are calling:
    	System.out.println("URL: http://localhost:5900/sdelab");
    	
    	tc.Client1();
//    	tc.Client2();
//    	tc.Client3();
//    	tc.Client4();
//    	tc.Client5();
//    	tc.Client6();
//    	tc.Client7();
//    	tc.Client8();
//    	tc.Client9();
//    	tc.Client10();
//    	tc.Client11();
//    	tc.Client12();

    }
}
