package ovation.odata.service;

import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.core4j.Enumerable;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.behaviors.OClientBehaviors;
import org.odata4j.core.EntitySetInfo;
import org.odata4j.core.OEntity;
import org.odata4j.jersey.consumer.ODataJerseyConsumer;

import ovation.AnalysisRecord;
import ovation.DataContext;
import ovation.DerivedResponse;
import ovation.Epoch;
import ovation.EpochGroup;
import ovation.Experiment;
import ovation.ExternalDevice;
import ovation.IAnnotation;
import ovation.ITaggableEntityBase;
import ovation.KeywordTag;
import ovation.NumericDataType;
import ovation.Project;
import ovation.Resource;
import ovation.Response;
import ovation.Source;
import ovation.Stimulus;
import ovation.URLResource;
import ovation.User;
import ovation.odata.model.dao.Property;
import ovation.odata.util.OData4JClientUtils;
import ovation.odata.util.OvationDBTestHelper;
import ovation.odata.util.OvationJUnitUtils;
import ovation.odata.util.OvationUtils;

import com.google.common.collect.Sets;

/**
 * comparison tests to confirm that the OvOData service is returning objects consistent with those return by direct Ovation DB connection
 * @author Ron
 *
 */
public class BasicGetTest {
	static final String SERVICE_URL 	= System.getProperty("ovodataUrl",      "http://localhost:8080/ovodata/Ovodata.svc/");
	static final String USERNAME 		= System.getProperty("ovationUser", 	"ron");
	static final String PASSWORD 		= System.getProperty("ovationPassword", "passpass1");
	static final String DB_CON_FILE		= System.getProperty("ovationDbFile",   "/var/lib/ovation/db/dev.connection");
	
	private static ODataConsumer 	_odataClient; 
	private static DataContext 		_dbContext;
	
	@BeforeClass 
	public static void beforeAllTests() throws Throwable {
	    _odataClient = ODataJerseyConsumer.newBuilder(SERVICE_URL).setClientBehaviors(OClientBehaviors.basicAuth(USERNAME, PASSWORD)).build();
//		_odataClient = ODataConsumer.create(SERVICE_URL, OClientBehaviors.basicAuth(USERNAME, PASSWORD)); 
		_dbContext = OvationDBTestHelper.getContext(USERNAME, PASSWORD, DB_CON_FILE);
	}
	
	@AfterClass 
	public static void logout() {
		_odataClient = null; 
		_dbContext.logout();
		_dbContext.close();
		_dbContext = null;
	 }
	
	// TODO can probably be moved to OvationJUnitUtils or similar ovation-specific util class
	static enum Entity {
		EXPERIMENT("Experiments", 			Experiment.class), 
		EPOCH_GROUP("EpochGroups", 			EpochGroup.class),
		ANALYSIS_RECORD("AnalysisRecords", 	AnalysisRecord.class), 
		RESPONSE("Responses", 				Response.class),
		EXTERNAL_DEVICE("ExternalDevices", 	ExternalDevice.class), 
		SOURCE("Sources", 					Source.class),
		DERIVED_RESPONSE("DerivedResponses",DerivedResponse.class), 
		PROJECT("Projects", 				Project.class),
		URL_RESOURCE("URLResources", 		URLResource.class), 
		STIMULUS("Stimuli", 				Stimulus.class),
		KEYWORD_TAG("KeywordTags", 			KeywordTag.class), 
		EPOCH("Epochs", 					Epoch.class),
		RESOURCE("Resources", 				Resource.class),
		USER("Users",						User.class),
		
		_STRING("_Strings",              	String.class),
		_DOUBLE("_Doubles",                 Double.class),
		_FLOAT("_Floats",                 	Float.class),
		_LONG("_Longs",                 	Long.class),
		_INTEGER("_Integers",               Integer.class),
		
		_PROPERTY("_Properties",			Property.class),
		_ANNOTATION("_Annotations",			IAnnotation.class),
		
		_TaggableEntityBase("_ITaggableEntityBases",ITaggableEntityBase.class),
		_NumericDataTypes("_NumericDataTypes",		NumericDataType.class),
		;

		private final String 	_setName;
		
		Entity(String setName, Class<?> type) { _setName = setName; }
	}
	
	private static final Set<String> _knownEntities = Sets.newHashSet();
	static {
		for(Entity e : Entity.values()) {
			_knownEntities.add(e._setName);
		}
	}

	// ------------ utility methods (to adapt our enum to OData4J and Ovation APIs) --------------
	//
	static Enumerable<OEntity> 	getAllEntities(Entity type) 				{ return OData4JClientUtils.getAllEntities(_odataClient, type._setName); }
	static OEntity 				getSubEntity(OEntity root, String name) 	{ return OData4JClientUtils.getSubEntity(_odataClient, root, name); }
	static Enumerable<OEntity> 	getSubEntities(OEntity root, String name) 	{ return OData4JClientUtils.getSubEntities(_odataClient, root, name); }
	static <T> List<T> 			getAllFromDB(Class<T> type) 				{ return OvationUtils.getAllFromDB(_dbContext, type); }
	static <T> T 				getFromDB(String uri) 						{ return OvationUtils.getFromDB(_dbContext, uri); }
	
	/**
	 * assert that the server recognizes all entity types we expect it to recognize
	 */
	@Test
	public void testBasics() throws Throwable {
		// assert that service exposes all known entity types (and no more or less)
		Enumerable<EntitySetInfo> serviceEntities = _odataClient.getEntitySets();
		Assert.assertEquals(_knownEntities.size(), serviceEntities.count());
		for (EntitySetInfo info : serviceEntities) {
			Assert.assertTrue(_knownEntities.contains(info.getTitle()));
		}
	}

	/** common util methods for testing that what came from the service matches what came from the database */
	static <T> void assertEquals(Entity type, List<T> allFromDb, Enumerable<OEntity> allFromService) {
		// assert that the count returned by the DB match the count returned by the service
		// and that every element from the service matches the same element in the DB
		Assert.assertEquals(allFromDb.size(), allFromService.count());
		boolean first = true;
		for (OEntity entity : allFromService) {
			if (first) { System.out.println(type + " links - " + entity.getLinks()); first = false; }
			compareToDb(type, entity);
		}
	}
	
	@Test
	public void testGetAllExperiments() throws Throwable {
		Entity 				type 			= Entity.EXPERIMENT;
		List<Experiment> 	allFromDb 		= getAllFromDB(Experiment.class);	
		Enumerable<OEntity> allFromService 	= getAllEntities(type);
		assertEquals(type, allFromDb, allFromService);
	}
	
	@Test
	public void testGetAllEpochGroups() throws Throwable {
		Entity 				type 			= Entity.EPOCH_GROUP;
		List<EpochGroup> 	allFromDb 		= getAllFromDB(EpochGroup.class);	
		Enumerable<OEntity> allFromService 	= getAllEntities(type);
		assertEquals(type, allFromDb, allFromService);
	}


	@Test
	public void testGetAllAnalysisRecords() throws Throwable {
		Entity 					type 			= Entity.ANALYSIS_RECORD;
		List<AnalysisRecord> 	allFromDb 		= getAllFromDB(AnalysisRecord.class);	
		Enumerable<OEntity> 	allFromService 	= getAllEntities(type);
		assertEquals(type, allFromDb, allFromService);
	}

	@Test
	public void testGetAllResponses() throws Throwable {
		Entity 				type 			= Entity.RESPONSE;
		List<Response> 		allFromDb 		= getAllFromDB(Response.class);	
		Enumerable<OEntity> allFromService 	= getAllEntities(type);
		assertEquals(type, allFromDb, allFromService);
	}

	@Test
	public void testGetAllExternalDevices() throws Throwable {
		Entity 					type 			= Entity.EXTERNAL_DEVICE;
		List<ExternalDevice> 	allFromDb 		= getAllFromDB(ExternalDevice.class);	
		Enumerable<OEntity> 	allFromService 	= getAllEntities(type);
		assertEquals(type, allFromDb, allFromService);
	}

	@Test
	public void testGetAllSources() throws Throwable {
		Entity 				type 			= Entity.SOURCE;
		List<Source> 		allFromDb 		= getAllFromDB(Source.class);	
		Enumerable<OEntity> allFromService 	= getAllEntities(type);
		assertEquals(type, allFromDb, allFromService);
	}

	@Test
	public void testGetAllDerivedResponses() throws Throwable {
		Entity 					type 			= Entity.DERIVED_RESPONSE;
		List<DerivedResponse>	allFromDb 		= getAllFromDB(DerivedResponse.class);	
		Enumerable<OEntity> 	allFromService 	= getAllEntities(type);
		assertEquals(type, allFromDb, allFromService);
	}

	@Test
	public void testGetAllProjects() throws Throwable {
		Entity 				type 			= Entity.PROJECT;
		List<Project> 		allFromDb 		= getAllFromDB(Project.class);	
		Enumerable<OEntity> allFromService 	= getAllEntities(type);
		assertEquals(type, allFromDb, allFromService);
	}

	@Test
	public void testGetAllURLResources() throws Throwable {
		Entity 				type 			= Entity.URL_RESOURCE;
		List<URLResource> 	allFromDb 		= getAllFromDB(URLResource.class);	
		Enumerable<OEntity> allFromService 	= getAllEntities(type);
		assertEquals(type, allFromDb, allFromService);
	}

	@Test
	public void testGetAllStimuli() throws Throwable {
		Entity 				type 			= Entity.STIMULUS;
		List<Stimulus> 		allFromDb 		= getAllFromDB(Stimulus.class);	
		Enumerable<OEntity> allFromService 	= getAllEntities(type);
		assertEquals(type, allFromDb, allFromService);
	}

	@Test
	public void testGetAllKeywordTags() throws Throwable {
		Entity 				type 			= Entity.KEYWORD_TAG;
		List<KeywordTag> 	allFromDb 		= getAllFromDB(KeywordTag.class);	
		Enumerable<OEntity> allFromService 	= getAllEntities(type);
		assertEquals(type, allFromDb, allFromService);
	}

	@Test
	public void testGetAllEpochs() throws Throwable {
		Entity 				type 			= Entity.EPOCH;
		List<Epoch> 		allFromDb 		= getAllFromDB(Epoch.class);	
		Enumerable<OEntity> allFromService 	= getAllEntities(type);
		assertEquals(type, allFromDb, allFromService);
	}

	@Test
	public void testGetAllResources() throws Throwable {
		Entity 				type 			= Entity.RESOURCE;
		List<Resource> 		allFromDb 		= getAllFromDB(Resource.class);	
		Enumerable<OEntity> allFromService 	= getAllEntities(type);
		assertEquals(type, allFromDb, allFromService);
	}

	@Test
	public void testGetUsers() throws Throwable {
		Entity 				type 			= Entity.USER;
		List<User> 			allFromDb 		= getAllFromDB(User.class);	
		Enumerable<OEntity> allFromService 	= getAllEntities(type);
		assertEquals(type, allFromDb, allFromService);
	}
	
	/**
	 * compare top-level entities to each other - actual comparison handled by OvationJUnitUtils methods
	 * DB entry is retrieved by using the URI in the OData4J entity
	 * @param type used to ensure proper casting of entity to proper DB type
	 * @param entity
	 */
	static void compareToDb(Entity type, OEntity entity) {
		switch(type) {
			case EXPERIMENT : {
				String 		uri 	= OData4JClientUtils.getStringProperty(entity, "URI");
				Experiment 	fromDb 	= getFromDB(uri);
				OvationJUnitUtils.assertEquals(uri, fromDb, entity, _odataClient, _dbContext);
				break;
			}
			case ANALYSIS_RECORD : {
				String 			uri 	= OData4JClientUtils.getStringProperty(entity, "URI");
				AnalysisRecord 	fromDb 	= getFromDB(uri);
				OvationJUnitUtils.assertEquals(uri, fromDb, entity, _odataClient, _dbContext);
				break;
			}
			case DERIVED_RESPONSE : {
				String 			uri 	= OData4JClientUtils.getStringProperty(entity, "URI");
				DerivedResponse fromDb 	= getFromDB(uri);
				OvationJUnitUtils.assertEquals(uri, fromDb, entity, _odataClient, _dbContext);
				break;
			}
			case EPOCH : {
				String 	uri 	= OData4JClientUtils.getStringProperty(entity, "URI");
				Epoch	fromDb 	= getFromDB(uri);
				OvationJUnitUtils.assertEquals(uri, fromDb, entity, _odataClient, _dbContext);
				break;
			}
			case EPOCH_GROUP : {
				String 		uri 	= OData4JClientUtils.getStringProperty(entity, "URI");
				EpochGroup 	fromDb 	= getFromDB(uri);
				OvationJUnitUtils.assertEquals(uri, fromDb, entity, _odataClient, _dbContext);
				break;
			}
			case EXTERNAL_DEVICE : {
				String 			uri 	= OData4JClientUtils.getStringProperty(entity, "URI");
				ExternalDevice 	fromDb 	= getFromDB(uri);
				OvationJUnitUtils.assertEquals(uri, fromDb, entity, _odataClient, _dbContext);
				break;
			}
			case KEYWORD_TAG : {
				String 		uri 	= OData4JClientUtils.getStringProperty(entity, "URI");
				KeywordTag 	fromDb 	= getFromDB(uri);
				OvationJUnitUtils.assertEquals(uri, fromDb, entity, _odataClient, _dbContext);
				break;
			}
			case PROJECT : {
				String 	uri 	= OData4JClientUtils.getStringProperty(entity, "URI");
				Project fromDb 	= getFromDB(uri);
				OvationJUnitUtils.assertEquals(uri, fromDb, entity, _odataClient, _dbContext);
				break;
			}
			case RESOURCE : {
				String 		uri 	= OData4JClientUtils.getStringProperty(entity, "URI");
				Resource 	fromDb 	= getFromDB(uri);
				OvationJUnitUtils.assertEquals(uri, fromDb, entity, _odataClient, _dbContext);
				break;
			}
			case RESPONSE : {
				String 		uri 	= OData4JClientUtils.getStringProperty(entity, "URI");
				Response 	fromDb 	= getFromDB(uri);
				OvationJUnitUtils.assertEquals(uri, fromDb, entity, _odataClient, _dbContext);
				break;
			}
			case SOURCE : {
				String uri 		= OData4JClientUtils.getStringProperty(entity, "URI");
				Source fromDb 	= getFromDB(uri);
				OvationJUnitUtils.assertEquals(uri, fromDb, entity, _odataClient, _dbContext);
				break;
			}
			case STIMULUS : {
				String 		uri 	= OData4JClientUtils.getStringProperty(entity, "URI");
				Stimulus 	fromDb 	= getFromDB(uri);
				OvationJUnitUtils.assertEquals(uri, fromDb, entity, _odataClient, _dbContext);
				break;
			}
			case URL_RESOURCE : {
				String 		uri 	= OData4JClientUtils.getStringProperty(entity, "URI");
				URLResource fromDb 	= getFromDB(uri);
				OvationJUnitUtils.assertEquals(uri, fromDb, entity, _odataClient, _dbContext);
				break;
			}
			case USER : {
				String 	uri 	= OData4JClientUtils.getStringProperty(entity, "URI");
				User	fromDb 	= getFromDB(uri);
				OvationJUnitUtils.assertEquals(uri, fromDb, entity, _odataClient, _dbContext);
			}
		}
	}
}
