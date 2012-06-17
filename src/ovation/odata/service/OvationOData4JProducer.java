package ovation.odata.service;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.producer.EntitiesResponse;
import org.odata4j.producer.EntityQueryInfo;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.ODataProducerFactory;
import org.odata4j.producer.QueryInfo;
import org.odata4j.producer.Responses;
import org.odata4j.producer.exceptions.NotFoundException;
import org.odata4j.producer.inmemory.InMemoryProducer;

import ovation.odata.model.ExtendedPropertyModel;
import ovation.odata.model.OvationModelBase;
import ovation.odata.util.CollectionUtils;
import ovation.odata.util.DataContextCache;
import ovation.odata.util.OData4JServerUtils;
import ovation.odata.util.PropertyManager;
import ovation.odata.util.Props;

import com.google.common.collect.Lists;

/**
 * adapts OData4J's framework to Ovation's model 
 * @author Ron
 */
public class OvationOData4JProducer extends InMemoryProducer {
	public static final Logger _log = Logger.getLogger(OvationOData4JProducer.class);

	public static String getServiceName() { 
		return PropertyManager.getProperties(OvationOData4JProducer.class).getProperty(Props.SERVER_NAME, Props.SERVER_NAME_DEFAULT);
	}

	/**
	 * used when deploying into Tomcat environment via cmd-line/setenv.sh:
	 * 	-Dodata4j.producerfactory=ovation.odata.service.OvationOData4JServer$Factory
	 * or in web.xml:
	 * <init-param>
	 *   <param-name>odata4j.producerfactory</param-name>
	 *   <param-value>ovation.odata.service.OvationOData4JProducer$Factory</param-value>
	 * </init-param>
	 * 
	 * basically if this guy is called we're NOT in a Jersey stand-alone environment
	 * @author Ron
	 */
	public static class Factory implements ODataProducerFactory { 
		public ODataProducer create(Properties props) {
			return new OvationOData4JProducer();
		}
	}
	
	public static Properties getProps() { return PropertyManager.getProperties(OvationOData4JProducer.class); }
	
	public OvationOData4JProducer() {
		super(getProps().getProperty(Props.SERVER_NAME, Props.SERVER_NAME_DEFAULT), 
			  Props.getProp(getProps(), Props.SERVER_MAX_RESULTS, Props.SERVER_MAX_RESULTS_DEFAULT));
        registerHandlers();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void registerHandlers() {
		// register all the basic handlers
		OvationModelBase.registerOvationModel();
		
		// register all handlers with OData4J
		Set<String> allEntityNames = ExtendedPropertyModel.getEntityNames();
		for (String name : allEntityNames) {
			ExtendedPropertyModel model = ExtendedPropertyModel.getPropertyModel(name);
			register(model.getEntityType(), model, name, model.allGetter());
		}
	}
	
    /**
     * @param entityClass       the Java class type of the entity being registered
     * @param propertyModel     the model instance that handles the registered type
     * @param entitySetName     the name added to the URL to identify a request for this entity type
     * @param entityTypeName    TODO ?? (unknown)
     * @param get               Func object which returns Iterable<TEntity> to get all entities of this type
     * @param keys              one or more keys for the entity TODO ?? (not sure)
     */
    public <TEntity> void register( final Class<TEntity> entityClass, 
                                    final ExtendedPropertyModel<?> propertyModel, 
                                    final String entitySetName,
                                    final Func<Iterable<TEntity>> get) {
        final String entityTypeName = entitySetName;
        final String[] keys = propertyModel.getKeyPropertyNames();
        
        if (_log.isDebugEnabled()) {
            _log.debug("register(class:" + entityClass + ", model:" + propertyModel 
                            + ", name:" + entitySetName + ", type:" + entityTypeName 
                            + ", getAll:" + get + ", keys:" + Arrays.toString(keys));
        }
        super.register(entityClass, propertyModel, entitySetName, entityTypeName, get, keys);
    }
    
	/** 
	 * Obtains the service metadata for this producer.
	 * @return a fully-constructed metadata object
	 */
    @Override
	public EdmDataServices getMetadata() {
		try {
			EdmDataServices result = super.getMetadata();
			if (_log.isDebugEnabled()) {
				_log.debug("getMetadata() - " + result);
			}
			return result;
		} catch (Throwable ex) {
			_log.error("failed to getMetadata()", ex);
			throw new RuntimeException(ex.toString(), ex);
		}
	}
	
    /**
     * Creates a new OData entity.
     * 
     * @param entitySetName  the entity-set name
     * @param entity  the request entity sent from the client
     * @return the newly-created entity, fully populated with the key and default properties
     * @see <a href="http://www.odata.org/developers/protocols/operations#CreatingnewEntries">[odata.org] Creating new Entries</a>
     */
    @Override
	public EntityResponse createEntity(String entitySetName, OEntity entity) {
		return super.createEntity(entitySetName, entity);
	}
	
    /**
     * Creates a new OData entity as a reference of an existing entity, implicitly linked to the existing entity by a navigation property.
     * 
     * @param entitySetName  the entity-set name of the existing entity
     * @param entityKey  the entity-key of the existing entity
     * @param navProp  the navigation property off of the existing entity
     * @param entity  the request entity sent from the client
     * @return the newly-created entity, fully populated with the key and default properties, and linked to the existing entity
     * @see <a href="http://www.odata.org/developers/protocols/operations#CreatingnewEntries">[odata.org] Creating new Entries</a>
     */
    @Override
	public EntityResponse createEntity(String entitySetName, OEntityKey entityKey, String navProp, OEntity entity) {
		return super.createEntity(entitySetName, entityKey, navProp, entity);
	}
	
    /** 
     * Gets all the entities for a given top-level set matching the query information.
     * 
     * @param entitySetName  the entity-set name for entities to return
     * @param queryInfo  the additional constraints to apply to the entities
     * @return a packaged collection of entities to pass back to the client
     */
    @Override
	public EntitiesResponse getEntities(String entitySetName, QueryInfo queryInfo) {
		if (_log.isDebugEnabled()) {
			_log.debug("getEntities(set:" + entitySetName + ", queryInfo:{" + OData4JServerUtils.toString(queryInfo) + "}");
		}
		ExtendedPropertyModel.setQueryInfo(queryInfo);
		try {
			return super.getEntities(entitySetName, queryInfo);
		} finally {
			// remove from thread-local
			ExtendedPropertyModel.setQueryInfo(null);
		}
	}
	
    /** 
     * Obtains a single entity based on its type and key.
     * 
     * @param entitySetName  the entity-set name for the entity to return
     * @param entityKey  the unique entity-key within the set
     * @return the matching entity
     */
	@Override
	public EntityResponse getEntity(String entitySetName, OEntityKey entityKey, EntityQueryInfo queryInfo) { 
		if (_log.isDebugEnabled()) {
			_log.debug("getEntity(set:" + entitySetName + ", key:" + entityKey + ", queryInfo:{" + OData4JServerUtils.toString(queryInfo) + "}");
		}
		
		ExtendedPropertyModel.setQueryInfo(queryInfo);
		try {
			return super.getEntity(entitySetName, entityKey, queryInfo);
		} finally {
			// remove from thread-local
			ExtendedPropertyModel.setQueryInfo(null);
		}
	}
	
    /**
     * Modifies an existing entity using merge semantics.
     * 
     * @param entitySetName  the entity-set name
     * @param entity  the entity modifications sent from the client
     * @see <a href="http://www.odata.org/developers/protocols/operations#UpdatingEntries">[odata.org] Updating Entries</a>
     */
    @Override
	public void mergeEntity(String entitySetName, OEntity entity) {
		super.mergeEntity(entitySetName, entity);
	}

    /**
     * Modifies an existing entity using update semantics.
     * 
     * @param entitySetName  the entity-set name
     * @param entity  the entity modifications sent from the client
     * @see <a href="http://www.odata.org/developers/protocols/operations#UpdatingEntries">[odata.org] Updating Entries</a>
     */
	@Override
	public void updateEntity(String entitySetName, OEntity entity) {
		super.updateEntity(entitySetName, entity);
	}
	
    /**
     * Deletes an existing entity.
     * 
     * @param entitySetName  the entity-set name of the entity
     * @param entityKey  the entity-key of the entity
     * @see <a href="http://www.odata.org/developers/protocols/operations#DeletingEntries">[odata.org] Deleting Entries</a>
     */
    @Override
	public void deleteEntity(String entitySetName, OEntityKey entityKey) {
		super.deleteEntity(entitySetName, entityKey);
	}
	
    /** 
     * Given a specific entity, follow one of its navigation properties, applying constraints as appropriate.
     * Return the resulting entity, entities, or property value.
     * 
     * @param entitySetName  the entity-set of the entity to start with
     * @param entityKey  the unique entity-key of the entity to start with
     * @param navProp  the navigation property to follow
     * @param queryInfo  additional constraints to apply to the result
     * @return the resulting entity, entities, or property value
     */
	@Override
	public EntitiesResponse getNavProperty(String entitySetName, OEntityKey entityKey, String navProp, QueryInfo queryInfo) {
		ExtendedPropertyModel.setQueryInfo(queryInfo);
		try {
			final List<OEntity> entities = Lists.newArrayList();
			
			// work-around for a OData4J bug whereby URLs like this: http://win7-32:8080/ovodata/Ovodata.svc/&query=Sources('674dd7f3-6a1f-4f5a-a88f-86711b725921')/EpochGroups
			// product calls like this: getNavProperty(set:&query=Sources, key:('674dd7f3-6a1f-4f5a-a88f-86711b725921'), nav:EpochGroups, 
			// query:{{inlineCnt:null, top:null, skip:null, filter:null, orderBy:null, skipToken:null, customOptions:{}, expand:[], select:[]}}), model:null
			if (entitySetName != null && entitySetName.startsWith("&query=")) {
				entitySetName = entitySetName.substring("&query=".length());
			}
			
			// find the property-model associated with this entity set name
			ExtendedPropertyModel<?> model = ExtendedPropertyModel.getPropertyModel(entitySetName);
			
			if (_log.isInfoEnabled()) {
				_log.info("getNavProperty(set:" + entitySetName + ", key:" + entityKey + ", nav:" + navProp 
						+ ", query:{" + OData4JServerUtils.toString(queryInfo) + "}), model:" + model);
			}
			
			if (model == null) {
				_log.warn("Unable to find model for entitySetName '" + entitySetName + "'");
			    throw new NotFoundException(entitySetName + " type is not found");
			}
		
			// find root entity 
			Object entity = model.getEntityByKey(entityKey);
			if (entity == null) {
				if (_log.isInfoEnabled()) {
					_log.info("Unable to find entity in " + model + " with key " + entityKey);
				}
			    throw new NotFoundException(entitySetName + "(" + entityKey + ") was not found");
			}
			
			// navProp is the NAME of the entity within the element in entitySetName - need to resolve it to a TYPE
			// not ALWAYS a collection, tho, so we also have to check properties (tho it can't be both)
			Class<?> navPropType = model.getCollectionElementType(navProp);
			boolean isCollection = true;
			if (navPropType == null) {
				navPropType = model.getPropertyType(navProp);
				isCollection = false;
				if (navPropType == null) {
					_log.warn("Unrecognized collection/property '" + navProp + "' within '" + entitySetName + "'");
				    throw new NotFoundException(navProp + " collection not found in '" + entitySetName + "'");
				}
			}
			ExtendedPropertyModel<?> subModel = ExtendedPropertyModel.getPropertyModel(navPropType);
			if (subModel == null) {
				_log.warn("Unrecognized type '" + navPropType + "' of '" + navProp + "' within '" + entitySetName + "'");
			    throw new NotFoundException(navProp + " collection type '" + navPropType + "' is not known");
			}

			final EdmEntitySet subEntitySet = getMetadata().getEdmEntitySet(subModel.entityName());
			// iterate over each sub-entity of entity which matches the navProp - they will all be of the same type
			Iterable<?> iterable = isCollection ? model.getCollectionValue(entity, navProp) : CollectionUtils.makeIterable(model.getPropertyValue(entity, navProp));
			Iterator<?> iter = iterable != null ? iterable.iterator() : null;
			if (iter != null) {
				if (queryInfo.skip != null) {
					for (int numToSkip = queryInfo.skip.intValue(); numToSkip > 0 && iter.hasNext(); --numToSkip) {
						iter.next();	// skip
					}
				}
				
				// TODO - this should influence how data is returned
//TODO				List<EntitySimpleProperty> 	expand = queryInfo.expand; - whether or not to expand out sub-elements or leave them as references
//				BoolCommonExpression 		filter = queryInfo.filter; - should be used by model
//				List<OrderByExpression> 	orderBy = queryInfo.orderBy; - should be used by model?

				for (int numToReturn = queryInfo.top != null ? queryInfo.top.intValue() : Integer.MAX_VALUE; numToReturn > 0 && iter.hasNext(); --numToReturn) {
					Object o = iter.next();
		/*			
					List<OProperty<?>> properties = Lists.newArrayList();
					for (String propName : subModel.getPropertyNames()) {
						Class<?> propType = subModel.getPropertyType(propName);
						EdmSimpleType edmType = EdmSimpleType.forJavaType(propType);
						String propValue = String.valueOf(subModel.getPropertyValue(o, propName));
						// FIXME - seems weird to dumb this down to a string...
						properties.add(OProperties.parse(propName, edmType.getFullyQualifiedTypeName(), propValue));
					}
					
					List<OLink> links = Lists.newArrayList();
					for (String linkName : subModel.getCollectionNames()) {
	//					Class<?> linkType = subModel.getCollectionElementType(linkName);
	//					Iterable<?> linkValue = subModel.getCollectionValue(o, linkName);
						String relation = "unknown";	// FIXME - need values for relation
						String title = linkName;
						String href = "/" + linkName;	// FIXME absolute or relative to current URL?
						links.add(OLinks.relatedEntities(relation, title, href));
		//FIXME				OLinks.relatedEntitiesInline(relation, title, href, relatedEntities);	// controlled via queryInfo $inline/$expand
		//FIXME - how to select this one?				OLinks.relatedEntity(relation, title, href);
		//FIXME				OLinks.relatedEntityInline(relation, title, href, relatedEntity);		// controlled via queryInfo $inline/$expand
					}
		*/
					if (o != null) {
						entities.add(toOEntity(subEntitySet, o, queryInfo.expand));
					}
				}
	
			} else {
				// FIXME no elments found to iterate the navProp is invalid?
				_log.info("no elments found to iterate the navProp is invalid?");
			}
			
			return Responses.entities(entities, subEntitySet, Integer.valueOf(entities.size()), queryInfo.skipToken);
		} finally {
			// make sure to detach the QueryInfo from the thread when we're done
			ExtendedPropertyModel.setQueryInfo(null);
		}
	}		

    /**
     * Releases any resources managed by this producer.
     */
    @Override
	public void close() {
		// clean-up
		DataContextCache.close();
	}
}