package client;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.glassfish.jersey.client.ClientConfig;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TClient {
	
	private WebTarget service = null;
	private ClientConfig clientConfig;
	private Client client;
	private String mediaType;
	
	private String user_created;
	private String url;
	
	private String first_person_id;
	private String last_person_id;
	private String person_to_delete;
	private ArrayList<String> measure_types = new ArrayList<>();
	private String measure_type;
	private String measure_id;
	
	private TClient() {
		mediaType = MediaType.APPLICATION_JSON;
		url = "https://sde-assignment2.herokuapp.com/sdelab/person";
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
	
	public void ResponseTemplate(Integer num, String method, String url, String accept, String content, String result, Integer status, String body) {
		System.out.println("Request #"+num+": "+method+" "+url+" Accept: "+accept+" Content-type: "+content+" ");
		System.out.println("=> Result: "+result);
		System.out.println("=> HTTP Status: "+status+"");
		System.out.println(body);
		System.out.println("--");
		System.out.println("");
	}
	
	private Element getRootElement(String xml) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(xml)));
		return doc.getDocumentElement();
	}
	
	/**
	 * Send R#1 (GET BASE_URL/person). Calculate how many people are in the response. If more than 2, result is OK, else is ERROR (less than 3 persons). Save into a variable id of the first person (first_person_id) and of the last person (last_person_id)
	 * @param mediaType
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void Client1(String mediaType) throws ParserConfigurationException, SAXException, IOException {
		WebTarget resourceWebTarget = service.path("person");
		Response response = resourceWebTarget.request().accept(mediaType).get(Response.class);
		
		String output = "";
		String result = "ERROR";
		if(response.getStatus() == 200) {
			output = response.readEntity(String.class);
			if(mediaType == MediaType.APPLICATION_JSON) {
				JSONObject jsonObj = new JSONObject("{\"people\":"+output+"}");
				JSONArray array = jsonObj.getJSONArray("people");
				if (array.length() > 2 )
					result = "OK";
				first_person_id = String.valueOf(array.getJSONObject(0).getInt("idPerson"));
				last_person_id = String.valueOf(array.getJSONObject(array.length()-1).getInt("idPerson"));
				//System.out.println("supposed id: " +first_person_id);
			} else if(mediaType == MediaType.APPLICATION_XML) {
				Element rootElement = getRootElement(output);
				//checks the number of person in the database
				if (rootElement.getElementsByTagName("person").getLength() > 2 )
					result = "OK";
				//first_person_id = 1
				NodeList list = rootElement.getFirstChild().getChildNodes();
				for (int i = 0; i < list.getLength(); i++) {
					if (list.item(i).getNodeName() == "idPerson") {
						first_person_id = list.item(i).getTextContent();
					}
				}
				last_person_id = rootElement.getLastChild().getFirstChild().getTextContent();
				//System.out.println("supposed id: " +first_person_id);
			}
			System.out.println("-- OK -- Users fetched successfully.");
		} else {
			System.out.println(response.getStatus());
		}
				
		ResponseTemplate(1, "GET", resourceWebTarget.getUri().toString(), mediaType, mediaType, result, response.getStatus(), output);

	}
	
	/**
	 * Send R#2 for first_person_id. If the responses for this is 200 or 202, the result is OK.
	 * @param mediaType
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void Client2(String mediaType) throws ParserConfigurationException, SAXException, IOException {
		WebTarget resourceWebTarget = service.path("person/" + first_person_id);
		Response response = resourceWebTarget.request().accept(mediaType).get(Response.class);
		//System.out.println("supposed id: " +first_person_id);
		
		String output = "";
		String result = "ERROR";
		if(response.getStatus() == 200 || response.getStatus() == 202) {
			
			output = response.readEntity(String.class);
			result = "OK";
		} else {
			System.out.println(response.getStatus());
		}
				
		ResponseTemplate(2, "GET", resourceWebTarget.getUri().toString(), mediaType, mediaType, result, response.getStatus(), output);
	}
	
	
	/**
	 * Send R#3 for first_person_id changing the firstname. If the responses has the name changed, the result is OK.
	 * @param mediaType
	 * @throws Exception
	 */
	public void Client3(String mediaType) throws Exception {
		WebTarget resourceWebTarget = service.path("person/" + first_person_id);
		
		String result = "ERROR";
		String data = null;
		if(mediaType == MediaType.APPLICATION_JSON) {
			// Get the user data
			Response r1 = resourceWebTarget.request().accept(MediaType.APPLICATION_JSON).get(Response.class);
			
			JSONObject payload = new JSONObject();
			if(r1.getStatus() == 200) {
				String output = r1.readEntity(String.class);
				payload = new JSONObject(output);
			} else {
				System.out.println(r1.getStatus());
				throw new Exception("No person with id " + user_created + " found.");
			}
			
			resourceWebTarget = service.path("person");
			// Send the new user data		
			payload.put("firstname", "changed_by_the_client");
			payload.remove("lifeStatus");
			data = payload.toString();
		} else if (mediaType == MediaType.APPLICATION_XML) {
			data = "<person><firstname>Modositottam XML</firstname></person>";
		}
		
		//System.out.println(payload.toString());
		Response r2 = resourceWebTarget.request().accept(mediaType).put(Entity.entity(data, mediaType), Response.class);
		
		// Check the HTTP result, return codes 200 and 201 means OK
		String output = null;
		if(r2.getStatus() == 200 || r2.getStatus() == 201) {
			output = r2.readEntity(String.class);
			result = "OK";
		} else {
			//System.out.println(r2.getStatus());
		}
				
		ResponseTemplate(3, "PUT", resourceWebTarget.getUri().toString(), mediaType, mediaType, result, r2.getStatus(), output);
	}
	
	/**
	 * Send R#4 to create the following person.
	 * Store the id of the new person.
	 * If the answer is 201 (200 or 202 are also applicable) with a person in the body who has an ID, the result is OK.
	 * 
	 * {
     *   "firstname"      : "Chuck",
     *    "lastname"      : "Norris",
     *    "birthdate"     : "1945-01-01",
     *    "healthProfile" : {
     *              "weight"  : 78.9,
     *              "height"  : 172
     *    }
     *	}
	 * 
	 * @param mediaType
	 * @throws Exception
	 */
	public void Client4(String mediaType) throws Exception {
		String result = "ERROR";
		WebTarget resourceWebTarget = service.path("person");
		
		String json_payload = "{\"firstname\":\"Elso\",\"lastname\":\"Probam212\",\"username\":\"userneveitt\",\"birthdate\":\"1982-06-08 18:00:00\",\"email\":\"teszt@teszt.hu\"}";
		//String xml_payload = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><person><firstname>Chuck</firstname><lastname>Norris</lastname><birthdate>1945-01-01</birthdate><healthProfile><weight>78.9</weight><height>172</height></healthProfile><person>";
		String xml_payload = "<person><firstname>Chuck</firstname><lastname>Norris</lastname><birthdate>1945-01-01</birthdate><healthProfile><weight>78.9</weight><height>172</height></healthProfile></person>";

		Response r = null;
		if(mediaType == MediaType.APPLICATION_JSON) {
			r = resourceWebTarget.request().accept(mediaType).post(Entity.entity(json_payload, mediaType), Response.class);
		} else if(mediaType == MediaType.APPLICATION_XML) {
			r = resourceWebTarget.request().accept(mediaType).post(Entity.entity(xml_payload, mediaType), Response.class);
		}
		
		
		String output = null;
		if(r.getStatus() == 200) {
			output = r.readEntity(String.class);
			if(mediaType == MediaType.APPLICATION_JSON) {
				JSONObject jsonObj = new JSONObject(output);
				if (jsonObj.get("idPerson") != null ){
					person_to_delete = String.valueOf(jsonObj.getInt("idPerson"));
				}
			}
			result = "OK";
		} else {
			//System.out.println(r.getStatus());
		}
				
		ResponseTemplate(4, "POST", resourceWebTarget.getUri().toString(), mediaType, mediaType, result, r.getStatus(), output);
	}
	
	public void Client5(String mediaType) throws Exception {
		WebTarget resourceWebTarget = service.path("person/" + person_to_delete);
		Response r = resourceWebTarget.request().accept(mediaType).delete(Response.class);
		
		String result = "ERROR";
		String output = null;
		if(r.getStatus() == 204) {
			output = r.readEntity(String.class);
			result = "OK";
		} else {
			//System.out.println(r.getStatus());
		}
				
		ResponseTemplate(5, "DELETE", resourceWebTarget.getUri().toString(), mediaType, mediaType, result, r.getStatus(), output);
	}
	
	/**
	 * Follow now with the R#9 (GET BASE_URL/measureTypes). If response contains more than 2 measureTypes - result is OK, else is ERROR (less than 3 measureTypes). Save all measureTypes into array (measure_types)
	 * @param mediaType
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void Client6(String mediaType) throws Exception {
		WebTarget resourceWebTarget = service.path("measureTypes");
		Response response = resourceWebTarget.request().accept(mediaType).get(Response.class);
		
		String output = "";
		String result = "ERROR";
		if(response.getStatus() == 200) {
			output = response.readEntity(String.class);
			if(mediaType == MediaType.APPLICATION_XML){
				Element rootElement = getRootElement(output);
				NodeList types = rootElement.getChildNodes();
				for(int i = 0; i < types.getLength(); i++){
					measure_types.add(types.item(i).getTextContent());
				}
				if(types.getLength() > 2) {
					result = "OK";
				}
			}else if(mediaType == MediaType.APPLICATION_JSON){
				JSONObject jsonObj = new JSONObject(output);
				JSONArray jsonTypes = jsonObj.getJSONArray("measureTypes");
				for(int i = 0; i < jsonTypes.length(); i++) {
					measure_types.add(jsonTypes.getString(i));
				}
				if(jsonTypes.length() > 2) {
					result = "OK";
				}
			}
		} else {
			//System.out.println(response.getStatus());
		}
				
		ResponseTemplate(6, "GET", resourceWebTarget.getUri().toString(), mediaType, mediaType, result, response.getStatus(), output);
	}
	
	/**
	 * Send R#6 (GET BASE_URL/person/{id}/{measureType}) for the first person you obtained at the beginning and the last person, and for each measure types from measure_types. If no response has at least one measure - result is ERROR (no data at all) else result is OK. Store one measure_id and one measureType.
	 * @param args
	 * @throws Exception
	 */
	public void Client7(String mediaType) throws Exception {
		WebTarget resourceWebTarget = null;
		Response response = null;
		String result = "ERROR";
		String output = "";
		String print = "";
		
		Integer num = 0;
		for(int i = 0; i < measure_types.size(); i++) {
			resourceWebTarget = service.path("person/"+first_person_id+"/"+measure_types.get(i));
			response = resourceWebTarget.request().accept(mediaType).get(Response.class);
			output = response.readEntity(String.class);
			if(mediaType == MediaType.APPLICATION_XML){
				Element rootElement = getRootElement(output);
				if(rootElement.getChildNodes().getLength() > 0){
					print = output;
					measure_type = measure_types.get(i);
					result = "OK";
				}
			}else{
				JSONArray jsonHistory = new JSONArray(output);
				if(jsonHistory.length() > 0){
					print = output;
					measure_type = measure_types.get(i);
					result = "OK";
				}
			}
		}
		
		ResponseTemplate(7, "GET", resourceWebTarget.getUri().toString(), mediaType, mediaType, result, response.getStatus(), print);
	}
	
	/**
	 * Send R#7 (GET BASE_URL/person/{id}/{measureType}/{mid}) for the stored measure_id and measureType. If the response is 200, result is OK, else is ERROR.
	 * @param mediaType
	 * @throws Exception
	 */
	public void Client8(String mediaType) throws Exception {
		WebTarget resourceWebTarget = service.path("person/"+first_person_id+"/"+measure_types.get(0)+"/1");
		Response response = resourceWebTarget.request().accept(mediaType).get(Response.class);
		
		String output = "";
		String result = "ERROR";
		if(response.getStatus() == 200) {
			output = response.readEntity(String.class);
			result = "OK";
			if(mediaType == MediaType.APPLICATION_XML) {

			} else if(mediaType == MediaType.APPLICATION_JSON){

			}
		}
		ResponseTemplate(8, "GET", resourceWebTarget.getUri().toString(), mediaType, mediaType, result, response.getStatus(), output);
	}
	
	/**
	 * Choose a measureType from measure_types and send the request R#6 (GET BASE_URL/person/{first_person_id}/{measureType}) and save count value (e.g. 5 measurements).
	 * Then send R#8 (POST BASE_URL/person/{first_person_id}/{measureType}) with the measurement specified below.
	 * <measure>
     *     <value>72</value>
     *     <created>2011-12-09</created>
     * </measure>
	 * Follow up with another R#6 as the first to check the new count value.
	 * If it is 1 measure more - print OK, else print ERROR.
	 * Remember, first with JSON and then with XML as content-types
	 * @param args
	 * @throws Exception
	 */
	public void Client9(String mediaType) throws Exception {
		WebTarget resourceWebTarget = service.path("measureTypes");
		Response response = resourceWebTarget.request().accept(mediaType).get(Response.class);
		
		String json_payload = "{\"value\": 72,\"created\": \"2011-12-09\"}";
		String xml_payload = "<measure><value>72</value><created>2011-12-09</created></measure>";
		
		String output = "";	
		String result = "ERROR";
		if(response.getStatus() == 200) {
			output = response.readEntity(String.class);
			result = "OK";
			if(mediaType == MediaType.APPLICATION_XML) {

			} else if(mediaType == MediaType.APPLICATION_JSON){

			}
		}
		ResponseTemplate(9, "GET", resourceWebTarget.getUri().toString(), mediaType, mediaType, result, response.getStatus(), output);
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
    	
    	tc.Client1(MediaType.APPLICATION_JSON);
    	tc.Client1(MediaType.APPLICATION_XML);
    	tc.Client2(MediaType.APPLICATION_JSON);
    	tc.Client2(MediaType.APPLICATION_XML);
    	tc.Client3(MediaType.APPLICATION_JSON);
    	tc.Client3(MediaType.APPLICATION_XML);
    	tc.Client4(MediaType.APPLICATION_JSON);
    	tc.Client4(MediaType.APPLICATION_XML);
    	tc.Client5(MediaType.APPLICATION_JSON);
    	tc.Client6(MediaType.APPLICATION_JSON);
    	tc.Client6(MediaType.APPLICATION_XML);
    	tc.Client7(MediaType.APPLICATION_JSON);
    	tc.Client7(MediaType.APPLICATION_XML);
    	tc.Client8(MediaType.APPLICATION_JSON);
    	tc.Client8(MediaType.APPLICATION_XML);
//    	tc.Client9();
//    	tc.Client10();
//    	tc.Client11();
//    	tc.Client12();

    }
}
