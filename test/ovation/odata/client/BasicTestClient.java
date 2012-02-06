package ovation.odata.client;

import org.apache.log4j.Logger;
import org.odata4j.consumer.ODataConsumer;

import ovation.odata.util.OData4JClientUtils;

public class BasicTestClient {
	public static final Logger _log = Logger.getLogger(BasicTestClient.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		org.apache.log4j.BasicConfigurator.configure();
		ODataConsumer client = ODataConsumer.create("http://localhost:8887/InMemoryProducerExample.svc/");
		OData4JClientUtils.reportMetadata(client.getMetadata());
	}
}
