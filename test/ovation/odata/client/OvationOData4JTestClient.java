package ovation.odata.client;

import org.core4j.Enumerable;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntity;
import org.odata4j.core.OQueryRequest;

import ovation.odata.util.OData4JClientUtils;

public class OvationOData4JTestClient {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		org.apache.log4j.BasicConfigurator.configure();
		
		ODataConsumer client = ODataConsumer.create("http://win7-32:8080/Ovodata.svc/");
		OData4JClientUtils.reportMetadata(client.getMetadata());
		
		OQueryRequest<OEntity> projectsQuery = client.getEntities("Projects");
		Enumerable<OEntity> projects = projectsQuery.execute();
		for (OEntity project : projects) {
			System.out.println("project - " + project);
		}

		OQueryRequest<OEntity> sourcesQuery = client.getEntities("Sources");
		Enumerable<OEntity> sources = sourcesQuery.execute();
		for (OEntity source : sources) {
			System.out.println("source - " + source);
		}
		
	}

}
