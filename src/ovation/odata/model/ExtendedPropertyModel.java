package ovation.odata.model;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;
import org.joda.time.DateTime;
import org.odata4j.core.OEntityKey;
import org.odata4j.producer.QueryInfo;
import org.odata4j.producer.inmemory.PropertyModel;

import ovation.ExternalDevice;
import ovation.IAnnotatableEntityBase;
import ovation.IEntityBase;
import ovation.ITaggableEntityBase;
import ovation.ITimelineElement;
import ovation.KeywordTag;
import ovation.NumericData;
import ovation.NumericDataType;
import ovation.Resource;
import ovation.User;
import ovation.odata.model.dao.MapEntry;
import ovation.odata.util.CollectionUtils;
import ovation.odata.util.DataContextCache;

import com.google.common.collect.Maps;

/**
 * this class is an extension of the OData4J PropertyModel interface which allows us to (hopefully) package
 * everything an OData4J Producer implementation needs to serve up the object model entities.  in some
 * ways it replicates what is exposed via the JavaBean API but gives us finer-grained control over how
 * it's exposed and used (and addresses areas where the object model isn't 100% JavaBean compliant).
 *    
 * @author Ron
 *
 * @param <K> Key type - the type of object used to look up this type of Value
 * @param <V> Value type - the type this model is about
 */
public abstract class ExtendedPropertyModel<K,V> implements PropertyModel {
    public static final Logger _log = Logger.getLogger(ExtendedPropertyModel.class);

    public static final String GET_ALL_PQL = "true";	// apparently this PQL "query" returns all instances of a type
    private static final ThreadLocal<QueryInfo> _threadQueryInfo = new ThreadLocal<QueryInfo>();
    
    protected Map<String,Class<?>> _fieldTypes;
    protected Map<String,Class<?>> _collectionTypes;
    
    protected ExtendedPropertyModel(Map<String,Class<?>> fieldTypes, Map<String,Class<?>> collectionTypes) {
        _fieldTypes = fieldTypes;
        _collectionTypes = collectionTypes;
    }
    
    /** @return the type of the specified collection within the associated entity type */
    public Class<?> getCollectionElementType(String collectionName) {
        if (_collectionTypes.containsKey(collectionName) == false) {
            _log.error("Unrecognized collection - " + collectionName);
        }
        return _collectionTypes.get(collectionName); 
    }
    
    /** @return the names of collections within the associated entity type */
    public Iterable<String> getCollectionNames() { 
        return _collectionTypes.keySet(); 
    }
    
    /** @return the names of the properties of the associated entity type */
    public Iterable<String> getPropertyNames() { 
        return _fieldTypes.keySet(); 
    }
    
    /** @return the type of the specified property of the associated entity type */
    public Class<?> getPropertyType(String propertyName) { 
        if (_fieldTypes.containsKey(propertyName) == false) {
            _log.error("Unrecognized property - " + propertyName);
        }
        return _fieldTypes.get(propertyName); 
    }
    
    /** @return the name of the entity set this model represents and by which it is registered with OData4J */
    public abstract String entityName();
    /** @return the elements of the specified collection with the associated entity type */
    public abstract Iterable<?> getCollectionValue(Object target, String collectionName);
    /** @return the value of the specified property of the associated entity type */
    public abstract Object getPropertyValue(Object target, String propertyName);
    
    /** @return the type of Entity this Model is for */
    public abstract Class<V> getEntityType(); 
    /** @return the primary-key type for this model's entity type */
    public abstract Class<K> getKeyType();
    /** @return a Func object which, when apply()ed will return all top-level entities of this model's type */
    public abstract Func<Iterable<V>> allGetter();
    /** @return a Func1 object which, when apply()ed will return the primary key for the provided entity instance */
    public abstract Func1<V,K> idGetter();
    /** @return the name used to identify this type in the Ovation DB */
    public abstract String getTypeName();
    
    /** @return entity that matches key or null if none */
    public V getEntityByKey(OEntityKey key) {
        Iterator<V> itr = getEntityIterByUUID(key);
        if (itr.hasNext() == false) {
            _log.error("Unable to find Project with UUID " + key.toKeyStringWithoutParentheses());
            return null;
        }
        V v = itr.next(); // the object we want.
        if (itr.hasNext()) {
            _log.error("Found multiple " + getEntityType() + " with UUID " + key.toKeyStringWithoutParentheses());
        }
        return v;
    }
    
    @SuppressWarnings("unchecked")
    protected Iterator<V> getEntityIterByUUID(OEntityKey key) {
        String typeName = getTypeName();
        String query     = "uuid == " + key.toKeyStringWithoutParentheses();
        _log.info("executing type:'" + typeName + "', query:'" + query + "'");
        return (Iterator<V>)DataContextCache.getThreadContext().query(typeName, query); 
    }
    
    // ExtendedPropertyModel instances keyed by value type
    private static final HashMap<Class<?>, ExtendedPropertyModel<?,?>> _modelByTypeMap = Maps.newHashMap();
    private static final HashMap<String, ExtendedPropertyModel<?,?>>   _modelByNameMap = Maps.newHashMap();
    
    public static ExtendedPropertyModel<?,?> getPropertyModel(Class<?> type) {
        return _modelByTypeMap.get(type);
    }
    public static ExtendedPropertyModel<?,?> getPropertyModel(String name) {
        return _modelByNameMap.get(name);
    }
    public static void setPropertyModel(Class<?> type, ExtendedPropertyModel<?,?> model) {
        _modelByTypeMap.put(type, model);
        _modelByNameMap.put(model.entityName(), model);
    }
    public static void addPropertyModel(ExtendedPropertyModel<?,?> model) {
        setPropertyModel(model.getEntityType(), model);
    }
    public static Set<Class<?>> getEntityTypes() { 
        return _modelByTypeMap.keySet(); 
    }
    public static Set<String> getEntityNames() {
        return _modelByNameMap.keySet();
    }
    
    public static void 		setQueryInfo(QueryInfo info) { if (info != null) { _threadQueryInfo.set(info); } else { _threadQueryInfo.remove(); } }
    public static QueryInfo getQueryInfo()             	 { return _threadQueryInfo.get(); }
    
    /** register model handlers for all Ovation API model classes */
    public static void registerOvationModel() {
        ExtendedPropertyModel.addPropertyModel(new ProjectModel());
        ExtendedPropertyModel.addPropertyModel(new AnalysisRecordModel());
        ExtendedPropertyModel.addPropertyModel(new ExperimentModel());
        ExtendedPropertyModel.addPropertyModel(new SourceModel());
        ExtendedPropertyModel.addPropertyModel(new ResourceModel());
        ExtendedPropertyModel.addPropertyModel(new URLResourceModel());
        ExtendedPropertyModel.addPropertyModel(new KeywordTagModel());
        ExtendedPropertyModel.addPropertyModel(new EpochGroupModel());
        ExtendedPropertyModel.addPropertyModel(new EpochModel());
        ExtendedPropertyModel.addPropertyModel(new ExternalDeviceModel());
        ExtendedPropertyModel.addPropertyModel(new StimulusModel());
        ExtendedPropertyModel.addPropertyModel(new ResponseModel());
        ExtendedPropertyModel.addPropertyModel(new DerivedResponseModel());
        ExtendedPropertyModel.addPropertyModel(new MapEntryModel());
        ExtendedPropertyModel.addPropertyModel(new StringModel());
        // TODO Query?
    }
    
    /** every property of every child-type - ensures consistent naming and also makes common util functions doable */
    protected enum PropertyName     {
        SamplingRate, SamplingUnits,
        ParentRoot,
        PluginID,
        Url,
        Uti,
        Tag,
        Manufacturer,
        Duration, EpochGroup, ExcludeFromAnalysis, NextEpoch, PreviousEpoch, ProtocolID,
        EpochCount, Experiment, Label, Parent, Source,
        Description, Epoch,  
        Data, DataBytes, DoubleData, FloatData, FloatingPointData, IntegerData, MatlabShape, NumericDataType, Shape, 
        ExternalDevice, Units, 
        EntryFunctionName, Name, Notes, Project, ScmRevision, ScmURL,   
        Purpose,                                                                    
        EndTime, StartTime,
        Owner, SerializedLocation, SerializedName, URI, URIString, UUID
    };
    /** every collection (association) of every child-type - ensures consistent naming */
    protected enum CollectionName {
        AllEpochGroups, AllExperiments,   
        StimulusParameters,
        Experiments, MyAnalysisRecords,
        Tagged,
        AnalysisRecords, DerivedResponses, MyDerivedResponses, ProtocolParameters, Responses, Stimuli,   
        Children, EpochsUnsorted,
        DerivationParameters, 
        DeviceParameters, 
        AnalysisParameters,  
        EpochGroups, Epochs, ExternalDevices, Projects, Sources, 
        KeywordTags, MyKeywordTags, MyTags, Tags, 
        MyProperties, /*MyResources,*/ Properties, Resources 
    };
    
    protected static void addPurposeAndNotesEntity(Map<String,Class<?>> propertyTypeMap, Map<String,Class<?>> collectionTypeMap) {
        propertyTypeMap.put(PropertyName.Notes.name(),        			String.class);
        propertyTypeMap.put(PropertyName.Purpose.name(),     			String.class);
        // base-type data
        addTimelineElement(propertyTypeMap, collectionTypeMap);
    }
    
    protected static void addTimelineElement(Map<String,Class<?>> propertyTypeMap, Map<String,Class<?>> collectionTypeMap) {
        propertyTypeMap.put(PropertyName.EndTime.name(),    			DateTime.class);
        propertyTypeMap.put(PropertyName.StartTime.name(),    			DateTime.class);
        // base-type data
        addTaggableEntityBase(propertyTypeMap, collectionTypeMap);
    }

    protected static void addResponseDataBase(Map<String,Class<?>> propertyTypeMap, Map<String,Class<?>> collectionTypeMap) {
        propertyTypeMap.put(PropertyName.Data.name(),					NumericData.class);
        propertyTypeMap.put(PropertyName.DataBytes.name(),          	byte[].class);
        propertyTypeMap.put(PropertyName.DoubleData.name(),         	double[].class);
        propertyTypeMap.put(PropertyName.FloatData.name(),          	float[].class);
        propertyTypeMap.put(PropertyName.FloatingPointData.name(),  	double[].class);
        propertyTypeMap.put(PropertyName.IntegerData.name(),        	int[].class);
        propertyTypeMap.put(PropertyName.MatlabShape.name(),        	long[].class);
        propertyTypeMap.put(PropertyName.NumericDataType.name(),    	NumericDataType.class);
        propertyTypeMap.put(PropertyName.Shape.name(),              	long[].class);
        // base-type data
        addIOBase(propertyTypeMap, collectionTypeMap);
    }
    protected static void addIOBase(Map<String,Class<?>> propertyTypeMap, Map<String,Class<?>> collectionTypeMap) {
        propertyTypeMap.put(PropertyName.ExternalDevice.name(),     	ExternalDevice.class);
        propertyTypeMap.put(PropertyName.Units.name(),              	String.class);
        collectionTypeMap.put(CollectionName.DeviceParameters.name(),   MapEntry.class);
        // base-type data
        addTaggableEntityBase(propertyTypeMap, collectionTypeMap);
    }
    
    protected static void addResource(Map<String,Class<?>> propertyTypeMap, Map<String,Class<?>> collectionTypeMap) {
        propertyTypeMap.put(PropertyName.Data.name(),               	byte[].class);
        propertyTypeMap.put(PropertyName.Name.name(),               	String.class);
        propertyTypeMap.put(PropertyName.Notes.name(),              	String.class);
        propertyTypeMap.put(PropertyName.Uti.name(),                	String.class);
        // base-type data
        addTaggableEntityBase(propertyTypeMap, collectionTypeMap);
    }
    
    protected static void addTaggableEntityBase(Map<String,Class<?>> propertyTypeMap, Map<String,Class<?>> collectionTypeMap) {
        collectionTypeMap.put(CollectionName.KeywordTags.name(),    	KeywordTag.class);
        collectionTypeMap.put(CollectionName.MyKeywordTags.name(),     	KeywordTag.class);
        collectionTypeMap.put(CollectionName.MyTags.name(),     		String.class);
        collectionTypeMap.put(CollectionName.Tags.name(),       		String.class);
        // base-type data
        addEntityBase(propertyTypeMap, collectionTypeMap);
    }
    
    protected static void addEntityBase(Map<String,Class<?>> propertyTypeMap, Map<String,Class<?>> collectionTypeMap) {
        propertyTypeMap.put(PropertyName.Owner.name(),              	User.class);
        propertyTypeMap.put(PropertyName.SerializedLocation.name(), 	String.class);
        propertyTypeMap.put(PropertyName.SerializedName.name(),     	String.class);
        propertyTypeMap.put(PropertyName.URI.name(),                	URI.class);
        propertyTypeMap.put(PropertyName.URIString.name(),          	String.class);
        propertyTypeMap.put(PropertyName.UUID.name(),               	String.class);
        collectionTypeMap.put(CollectionName.MyProperties.name(),   	MapEntry.class);
//        collectionTypeMap.put(CollectionName.MyResources.name(),    	Resource.class);
        collectionTypeMap.put(CollectionName.Properties.name(),     	MapEntry.class);
        collectionTypeMap.put(CollectionName.Resources.name(),			Resource.class);
        // no parent type within Ovation
    }
    
    /**
     * base-class-like utility methods for all the models to handle base-type collections 
     * (to reduce large amounts of duplicate code throughout the model adapters)
     * 
     * model-hierarchy (if this changes this code must be updated)
     *  AnalysisRecord											-> AnnotatableEntityBase -> TaggableEntityBase -> EntityBase -> ooObj -> ooAbstractObj -> 
     *  DerivedResp -> ResponseDataBase      -> IOBase 			-> AnnotatableEntityBase -> ...
     *  EpochGroup 							 -> TimelineElement -> AnnotatableEntityBase -> ...
     *  Epoch 								 -> TimelineElement -> ...
     *  Experiment 	-> PurposeAndNotesEntity -> TimelineElement -> ...
     *  ExternalDevi								   			-> AnnotatableEntityBase -> ...
     *  KeywordTag 																					  		   -> EntityBase -> ...
     *  Project 	-> PurposeAndNotesEntity -> ...
     *  Resource 									   			-> AnnotatableEntityBase -> ...
     *  Response 	-> ResponseDataBase      -> ...
     *  Source 										   			-> AnnotatableEntityBase -> ...
     *  Stimulus 							 -> IOBase          -> ...
     *  URLResource	-> Resource              -> ...
     * 
     * @param obj - the base-type object
     * @param col - the collection to extract from the base-type object
     * @return an Iterable<?> over the specified collection
     */
/*    
    protected static Iterable<?> getCollectionValue(IResponseDataBase obj, CollectionName col) {
    	switch (col) {
    		// TODO
    	}
    	return getCollectionValue((IIOBase)obj, col); 
    }
*//*
    protected static Iterable<?> getCollectionValue(IPurposeAndNotesEntity obj, CollectionName col) {
    	switch (col) {
    		// TODO
    	}
    	return getCollectionValue((ITimelineElement)obj, col); 
    }
*/    
    protected static Iterable<?> getCollectionValue(ITimelineElement obj, CollectionName col) {
    	switch (col) {
    		// no collections
    	}
    	return getCollectionValue((IAnnotatableEntityBase)obj, col); 
    }
/*
    protected static Iterable<?> getCollectionValue(IResponseDataBase obj, CollectionName col) {
    	switch (col) {
    	}
    	return getCollectionValue((ITaggableEntityBase)obj, col);
    }
*//*    
    protected static Iterable<?> getCollectionValue(IIOBase obj, CollectionName col) {
    	switch (col) {
    	}
    	return getCollectionValue((ITaggableEntityBase)obj, col);
    }
*/    
    protected static Iterable<?> getCollectionValue(IAnnotatableEntityBase obj, CollectionName col) {
    	switch (col) {
/* TOOD - add all these
  public abstract Set<String> 	getAnnotationGroupTagSet();
  public abstract String[] 		getAnnotationGroupTags();
  public abstract Set<String> 	getMyAnnotationGroupTagSet();
  public abstract String[] 		getMyAnnotationGroupTags();
  public abstract IAnnotation[] getAnnotations();
  
  public abstract Iterable<IAnnotation> 	getAnnotationsIterable();
  public abstract Iterable<IAnnotation> 	getMyAnnotationsIterable();
  public abstract IAnnotation[] 			getMyAnnotations();
  public abstract IAnnotation[] 			getAnnotations(String paramString);
  public abstract IAnnotation[] 			getMyAnnotations(String paramString);
  
  public abstract Iterable<IAnnotation> 		getAnnotationsIterable(String paramString);
  public abstract Iterable<IAnnotation> 		getMyAnnotationsIterable(String paramString);
  public abstract Iterable<INoteAnnotation> 	getNoteAnnotationsIterable(String paramString);
  public abstract INoteAnnotation[] 			getNoteAnnotations(String paramString);
  public abstract Iterable<INoteAnnotation> 	getMyNoteAnnotationsIterable(String paramString);
  public abstract INoteAnnotation[] 			getMyNoteAnnotations(String paramString);
  public abstract Iterable<ITimelineAnnotation> getTimelineAnnotationsIterable(String paramString);
  public abstract ITimelineAnnotation[] 		getTimelineAnnotations(String paramString);
  public abstract Iterable<ITimelineAnnotation> getMyTimelineAnnotationsIterable(String paramString);
  public abstract ITimelineAnnotation[] 		getMyTimelineAnnotations(String paramString);
  
  public abstract INoteAnnotation 			addNote(String paramString);
  public abstract INoteAnnotation 			addNote(String paramString1, String paramString2);
  public abstract INoteAnnotation 			addNote(Note paramNote);
  public abstract void 						removeAnnotations(String paramString);
  public abstract void 						removeAnnotation(IAnnotation paramIAnnotation); */
    	}
    	// pass up to base-type handler
    	return getCollectionValue((ITaggableEntityBase)obj, col);
    }
    
    protected static Iterable<?> getCollectionValue(ITaggableEntityBase obj, CollectionName col) {
    	switch (col) {
			case KeywordTags:		return CollectionUtils.makeIterable(obj.getKeywordTags());
			case MyKeywordTags:		return CollectionUtils.makeIterable(obj.getMyKeywordTags());
			case MyTags:			return CollectionUtils.makeIterable(obj.getMyTags());
			case Tags:				return CollectionUtils.makeIterable(obj.getTags());
    	}
    	// pass up to base-type handler
    	return getCollectionValue((IEntityBase)obj, col);
    }
    
    protected static Iterable<?> getCollectionValue(IEntityBase obj, CollectionName col) {
    	switch (col) {
			case MyProperties:		return MapEntry.makeIterable(obj.getMyProperties());
	//		case MyResources:		return CollectionUtils.makeIterable(obj.getMyResources());	- removed between 1.0.4 and 1.1
			case Properties:		return MapEntry.makeIterable(obj.getProperties());
			case Resources:			return obj.getResourcesIterable();
    	}
    	// no handler found for specified collection
    	return null;
    }

    /**
     * base-class-like utility methods for all the models to handle base-type property fields 
     * (to reduce large amounts of duplicate code throughout the model adapters)
     * @param obj - the base-type object
     * @param prop - the property to extract from the base-type object
     */
    /*    
    protected static Object getPropertyValue(IResponseDataBase obj, PropertyName prop) {
    	switch (prop) {
    		// TODO
    	}
    	return getPropertyValue((IIOBase)obj, prop); 
    }
*//*
    protected static Object getPropertyValue(IPurposeAndNotesEntity obj, PropertyName prop) {
    	switch (prop) {
    		// TODO
    	}
    	return getPropertyValue((ITimelineElement)obj, prop); 
    }
*/    
    protected static Object getPropertyValue(ITimelineElement obj, PropertyName prop) {
    	switch (prop) {
			case EndTime:	return obj.getEndTime();
			case StartTime:	return obj.getStartTime();
    	}
    	return getPropertyValue((IAnnotatableEntityBase)obj, prop); 
    }
/*
    protected static Object getPropertyValue(IResponseDataBase obj, PropertyName prop) {
    	switch (prop) {
    	}
    	return getPropertyValue((ITaggableEntityBase)obj, prop);
    }
*//*    
    protected static Object getPropertyValue(IIOBase obj, PropertyName prop) {
    	switch (prop) {
    	}
    	return getPropertyValue((ITaggableEntityBase)obj, prop);
    }
*/    
    protected static Object getPropertyValue(IAnnotatableEntityBase obj, PropertyName prop) {
    	switch (prop) {
    		// no props
    	}
    	// pass up to base-type handler
    	return getPropertyValue((ITaggableEntityBase)obj, prop);
    }
    
    protected static Object getPropertyValue(ITaggableEntityBase obj, PropertyName prop) {
    	switch (prop) {
    		// no props
    	}
    	// pass up to base-type handler
    	return getPropertyValue((IEntityBase)obj, prop);
    }
    
    protected static Object getPropertyValue(IEntityBase obj, PropertyName prop) {
    	switch (prop) {
			case Owner:					return obj.getOwner();
			case URI:					return obj.getURI();
			case UUID:					return obj.getUuid();
// FIXME	case SerializedLocation:	return obj.getSerializedLocation(); not in interface
// FIXME	case SerializedName:		return obj.getSerializedName();
			case URIString:				return obj.getURIString();
    	}
    	// no entry found for specified property
    	return null;
    }
    
    /*
The big new one in 1.1 is IAnnotation and IAnnotatableEntityBase. 
It's a pretty simple model (IAnnotatableEntityBase has a collection of IAnnotations per-user, like the ITaggableEntityBase=>KeywordTag relationship). 
Let's start by exposing this simple model. 
The usage, in fact, gets much more complicated
	—annotations are grouped to "annotation groups" by keyword tag (think GMail's labels to get groups of messages), 
	but I'm honestly not sure how we want to handle the complicated usage from OData beyond just exposing the annotations as a collection and letting the client sort things out.
     */
//    @SuppressWarnings("unchecked")
    protected Iterable<V> executeQueryInfo() {
        return (Iterable<V>)executeQueryInfo(getEntityType(), getQueryInfo());
    }
    
	protected Iterable<V> executeQuery(String query) {
    	return CollectionUtils.makeIterable(executeQuery(getEntityType(), query));
    }
    
//    @SuppressWarnings({ "rawtypes" })    // because EntityBase is package-private FIXME
    protected static Iterable<Object> executeQueryInfo(Class type, QueryInfo info) {    // Iterator<EntityBase> FIXME
        // http://win7-32:8080/ovodata/Ovodata.svc/Projects/?$format=json&pql=query%20goes%20here        
        // queryInfo:{{inlineCnt:null, top:null, skip:null, filter:null, orderBy:null, skipToken:null, customOptions:{pql=query goes here}, expand:[], select:[]}
        Map<String,String> customOptions = (info != null && info.customOptions != null) ? info.customOptions : null;
        if (customOptions != null) {
            String pqlQuery = customOptions.get("pql");
            String entityUrl = customOptions.get("url");
            if (pqlQuery != null) {
                Iterator<Object> iter = executeQuery(type, pqlQuery);    // should be Iterator<EntityBase> FIXME
                _log.info("query '" + pqlQuery + "' = " + iter);
                return CollectionUtils.makeIterable(iter);
            } else
            if (entityUrl != null) {
            	Object obj = getByURI(entityUrl);
                _log.info("url '" + entityUrl + "' = " + obj);
                return CollectionUtils.makeIterable(obj != null ? new Object[]{obj} : new Object[0]);
            } else {
                _log.info("both pql and url params are null");
            }
        } else {
            _log.info("customOptions is null - info:" + info);
        }
        return null;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })    // because EntityBase is package-private FIXME
	public static <T> Iterator<T> executeQuery(Class<T> type, String pqlQuery) {
    	return DataContextCache.getThreadContext().query((Class)type, pqlQuery); // should be Iterator<EntityBase> FIXME
    }

    public static Object getByURI(String uri) { 
    	return DataContextCache.getThreadContext().objectWithURI(uri);    // should be EntityBase FIXME
    }

}
