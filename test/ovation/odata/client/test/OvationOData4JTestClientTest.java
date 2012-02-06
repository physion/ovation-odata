package ovation.odata.client.test;

import junit.framework.TestCase;

import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OClientBehaviors;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;

import ovation.odata.client.OvationOData4JClient;

public class OvationOData4JTestClientTest extends TestCase {
	static final String[] ENTITY_SETS = {
		"Projects", "Experiments", "Sources", "Resources", "URLResources",
		"AnalysisRecords", "KeywordTags", "EpochGroups", "Epochs", "ExternalDevices", 
		"Stimuli", "Responses", "DerivedResponses"		
	};

	static class ClientHolder {
		static final String 		serviceUrl = "http://win7-32:8887/Ovodata.svc/";
		static final ODataConsumer 	client = ODataConsumer.create(serviceUrl, OClientBehaviors.basicAuth("ron", "password")); 
	}
	
	public void testProject() {
		OEntityKey projectId = OEntityKey.create("5cd9ca34-a93b-49ba-a297-3344e74ebbe7");
		OEntity project = OvationOData4JClient.getEntity(ClientHolder.client, "Projects", projectId);
		assertEquals("Test", project.getProperty("Name").getValue());
/*
			{
				"d" : {
				"__metadata" : {
				"uri" : "http://win7-32:8887/Ovodata.svc/Projects('')", "type" : "Ovodata .Projects"
				}, "EntityId" : "5cd9ca34-a93b-49ba-a297-3344e74ebbe7", "Notes" : null, "Name" : "Test", "SerializedName" : "Project_5cd9ca34-a93b-49ba-a297-3344e74ebbe7", "UUID" : "5cd9ca34-a93b-49ba-a297-3344e74ebbe7", "SerializedLocation" : "/Project_Test_5cd9ca34-a93b-49ba-a297-3344e74ebbe7", "EndTime" : null, "Purpose" : "OData Fixture", "StartTime" : "datetimeoffset'2011-09-05T09:26:51Z'", "URIString" : "ovation:///5cd9ca34-a93b-49ba-a297-3344e74ebbe7/#4-2-1-5:1000010", "Experiments" : {
				"__deferred" : {
				"uri" : "http://win7-32:8887/Ovodata.svc/Projects('5cd9ca34-a93b-49ba-a297-3344e74ebbe7')/Experiments"
				}
				}, "MyKeywordTags" : {
				"__deferred" : {
				"uri" : "http://win7-32:8887/Ovodata.svc/Projects('5cd9ca34-a93b-49ba-a297-3344e74ebbe7')/MyKeywordTags"
				}
				}, "AnalysisRecords" : {
				"__deferred" : {
				"uri" : "http://win7-32:8887/Ovodata.svc/Projects('5cd9ca34-a93b-49ba-a297-3344e74ebbe7')/AnalysisRecords"
				}
				}, "MyAnalysisRecords" : {
				"__deferred" : {
				"uri" : "http://win7-32:8887/Ovodata.svc/Projects('5cd9ca34-a93b-49ba-a297-3344e74ebbe7')/MyAnalysisRecords"
				}
				}, "MyResources" : {
				"__deferred" : {
				"uri" : "http://win7-32:8887/Ovodata.svc/Projects('5cd9ca34-a93b-49ba-a297-3344e74ebbe7')/MyResources"
				}
				}, "KeywordTags" : {
				"__deferred" : {
				"uri" : "http://win7-32:8887/Ovodata.svc/Projects('5cd9ca34-a93b-49ba-a297-3344e74ebbe7')/KeywordTags"
				}
				}, "Resources" : {
				"__deferred" : {
				"uri" : "http://win7-32:8887/Ovodata.svc/Projects('5cd9ca34-a93b-49ba-a297-3344e74ebbe7')/Resources"
				}
				}
				}
				}
*/			
	}
}
