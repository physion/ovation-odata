package ovation.odata.service;

import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.core4j.Enumerable;
import org.joda.time.DateTime;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.EntitySetInfo;
import org.odata4j.core.OClientBehaviors;
import org.odata4j.core.OEntity;
import org.odata4j.core.OQueryRequest;

import ovation.DataContext;
import ovation.KeywordTag;
import ovation.Project;
import ovation.odata.util.OvationDBTestHelper;
import ovation.odata.util.OvationDBTestHelper.DeviceData;
import ovation.odata.util.OvationDBTestHelper.EpochData;
import ovation.odata.util.OvationDBTestHelper.EpochGroupData;
import ovation.odata.util.OvationDBTestHelper.ExperimentData;
import ovation.odata.util.OvationDBTestHelper.ProjectData;
import ovation.odata.util.OvationDBTestHelper.ResponseData;
import ovation.odata.util.OvationDBTestHelper.SourceData;
import ovation.odata.util.OvationDBTestHelper.StimulusData;
import ovation.odata.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class BasicGetTest extends TestCase {
	private static final String SERVICE_URL 	= "http://localhost:8080/ovodata/Ovodata.svc/";
	private static final String USERNAME 		= "ron";
	private static final String PASSWORD 		= "passpass1";
	private static final String DB_CON_FILE		= "/var/lib/ovation/db/dev.connection";
	
	private static final ODataConsumer _odataClient = ODataConsumer.create(SERVICE_URL, OClientBehaviors.basicAuth(USERNAME, PASSWORD)); 
	
	private static final Set<String> _knownEntities = Sets.newHashSet(new String[]{
			"Experiments", "EpochGroups", "_MapEntries", "AnalysisRecords", "Responses", "ExternalDevices",
			"Sources", "DerivedResponses", "Projects", "URLResources", "_Strings", "Stimuli",
			"KeywordTags", "Epochs", "Resources"
	});

	// determine all the data in the DB that we're testing against first (direct to DB)
	private static Project[] _allProjects;
	
	static {
		DataContext context = null;
		try {
			context = OvationDBTestHelper.getContext(USERNAME, PASSWORD, DB_CON_FILE);
			_allProjects = context.getProjects();
			KeywordTag[] 	keywordTags = context.getAllTags();
			String[] 		stringTags 	= context.getAllStringTags();
			String[] 		userNames 	= context.getUsernames();
			System.out.println("keyword-tags : " + StringUtils.toString(keywordTags));
			System.out.println("string-tags  : " + StringUtils.toString(stringTags));
			System.out.println("user-names   : " + StringUtils.toString(userNames));
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("failed to insert test data - " + ex.toString());
		} finally {
			if (context != null) {
				context.close();
			}
		}
	}
//	<collection href="Experiments">
//	<collection href="EpochGroups">
//	<collection href="_MapEntries">
//	<collection href="AnalysisRecords">
//	<collection href="Responses">
//	<collection href="ExternalDevices">
//	<collection href="Sources">
//	<collection href="DerivedResponses">
//	<collection href="Projects">
//	<collection href="URLResources">
//	<collection href="_Strings">
//	<collection href="Stimuli">
//	<collection href="KeywordTags">
//	<collection href="Epochs">
//	<collection href="Resources">
	
	// disabled so it doesn't keep inserting new records every time
	public static void _testInsertData() {
		// insert project into DB
		List<ProjectData> projects = Lists.newArrayList();
		projects.add(new ProjectData().name("Test Project 3").purpose("test OvOData service")
						.add(new ExperimentData().end(new DateTime())
								.add(new DeviceData().name("Device 1").manufacturer("Initech"))
								.add(new DeviceData().name("Device 42").manufacturer("Initrobe"))
								.add(new DeviceData().name("Probe 100").manufacturer("ProbieTech"))
								.add(new SourceData().label("Source 1")
									.add(new EpochGroupData().label("epoch group 1").end(new DateTime())
										.add(new EpochData()
												.protocolId("insertionStress")
												.param("key1", Double.valueOf(1.0))
												.param("epochNumber", Integer.valueOf(1))
												.tag("howdy!")
												.tag("snow is cold")
												.addPair("Device 1", new StimulusData(), new ResponseData())
												.addPair("Device 42",
														new StimulusData().devParam("theAnswer", Integer.valueOf(42)) .param("theQuestion", "Life, the Universe, and Everything"), 
														new ResponseData().data(new double[]{42}, "none"))
										)
									)
								)
						)
		);
		
		DataContext context = null;
		try {
			context = OvationDBTestHelper.getContext(USERNAME, PASSWORD, DB_CON_FILE);
			OvationDBTestHelper.insertFixture(context, projects);
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("failed to insert test data - " + ex.toString());
		} finally {
			if (context != null) {
				context.close();
			}
		}
	}
	
	/**
	 * assert that the server recognizes all entity types we expect it to recognize
	 */
	public static void testBasics() {
		Enumerable<EntitySetInfo> serviceEntities = _odataClient.getEntitySets();
		assertEquals(_knownEntities.size(), serviceEntities.count());
		for (EntitySetInfo info : serviceEntities) {
			assertTrue(_knownEntities.contains(info.getTitle()));
		}
	}
	
	/**
	 * assert that the server returns the same project data as the direct-to-DB connection produced
	 */
	public static void testProjectGet() {

//			OEntityKey projectId = OEntityKey.create("5cd9ca34-a93b-49ba-a297-3344e74ebbe7");
		OQueryRequest<OEntity> query = _odataClient.getEntities("Projects");
		Enumerable<OEntity> projects = query.execute();
		assertEquals(_allProjects.length, projects.count());
		
		// http://localhost:8080/ovodata/Ovodata.svc/ExternalDevices?$format=json - 6 devices returned
		// http://localhost:8080/ovodata/Ovodata.svc/Experiments?$format=json - 2
		// http://localhost:8080/ovodata/Ovodata.svc/Sources?$format=json - 2
		// http://localhost:8080/ovodata/Ovodata.svc/Projects?$format=json- 2
		
		
		
	}
}
