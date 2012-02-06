package ovation.odata.client.examples;

import java.util.List;

import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntity;
import org.odata4j.core.OProperty;

public class InMemoryConsumer {
	public static void main(String[] args) {
        ODataConsumer client = ODataConsumer.create("http://localhost:8887/InMemoryProducerExample.svc/");

        
        int morganSpurlockId = client.getEntities("People").filter("substringof('Spurlock',Name)").execute().first().getProperty("Id", Integer.class).getValue();

        // lookup and print all titles he's acted in
        List<OEntity> titlesActedIn = client.getEntities("People").nav(morganSpurlockId, "TitlesActedIn").execute().toList();
        for(OEntity title : titlesActedIn) {
            for(OProperty<?> p : title.getProperties()) {
                System.out.printf("%s: %s", p.getName(), p.getValue());
            }
        }
        System.out.println("count: " + titlesActedIn.size());
		
	}
}
