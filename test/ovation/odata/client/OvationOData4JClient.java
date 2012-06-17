package ovation.odata.client;

import org.apache.log4j.Logger;
import org.core4j.Enumerable;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.behaviors.OClientBehaviors;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityGetRequest;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OQueryRequest;
import org.odata4j.jersey.consumer.ODataJerseyConsumer;

public class OvationOData4JClient {
	public static final Logger _log = Logger.getLogger(OvationOData4JClient.class);

	static final String[] ENTITY_SETS = {
		"Projects", "Experiments", "Sources", "Resources", "URLResources",
		"AnalysisRecords", "KeywordTags", "EpochGroups", "Epochs", "ExternalDevices", 
		"Stimuli", "Responses", "DerivedResponses"		
	};
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String serviceUrl = "http://win7-32:8887/Ovodata.svc/";
		if (args.length > 0) {
			serviceUrl = args[0];
		}
		
		ODataConsumer client = createClient(serviceUrl, "ron", "password");
		System.out.println("got client " + client);
		
		for (String entitySetName : ENTITY_SETS) {
			System.out.println("All " + entitySetName);
			print(getAll(client, entitySetName));
		}
		
		OEntityKey projectId = OEntityKey.create("5cd9ca34-a93b-49ba-a297-3344e74ebbe7");
		OEntity project = getEntity(client, "Projects", projectId);
		System.out.println("project = " + project);
	}

	public static ODataConsumer createClient(String url, String user, String password) {
        return ODataJerseyConsumer.newBuilder(url).setClientBehaviors(OClientBehaviors.basicAuth(user, password)).build();
	}

	public static Enumerable<OEntity> getAll(ODataConsumer client, String entitySetName) {
		OQueryRequest<OEntity> projectsQuery = client.getEntities(entitySetName);
		return projectsQuery.execute();
	}

	public static OEntity getEntity(ODataConsumer client, String entitySetName, OEntityKey key) {
		OEntityGetRequest<OEntity> query = client.getEntity(entitySetName, key);
		return query.execute();
	}

	static void print(Enumerable<OEntity> entities) {
		for (OEntity entity : entities) {
			System.out.println(entity);
		}
	}
}
