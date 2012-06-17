package ovation.odata.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;
import org.odata4j.core.OEntityKey;
import org.odata4j.producer.QueryInfo;
import org.odata4j.producer.inmemory.PropertyModel;

import ovation.odata.util.CollectionUtils;

import com.google.common.collect.Maps;

/**
 * this class is an extension of the OData4J PropertyModel interface which allows us to (hopefully) package
 * everything an OData4J Producer implementation needs to serve up the object model entities.  in some
 * ways it replicates what is exposed via the JavaBean API but gives us finer-grained control over how
 * it's exposed and used (and addresses areas where the object model isn't 100% JavaBean compliant).
 *    
 * @author Ron
 *
 * @param <K> Key type - String (since odata4j 0.6)
 * @param <V> Value type - the type this model is about
 */
public abstract class ExtendedPropertyModel<V> implements PropertyModel {
    public static final Logger _log = Logger.getLogger(ExtendedPropertyModel.class);

    /** allows us to attach the QueryInfo to the handling thread so we don't have to pass it around everywhere */
    private static final ThreadLocal<QueryInfo> _threadQueryInfo = new ThreadLocal<QueryInfo>();
    
    private final Map<String,Class<?>>    _fieldTypes;
    private final Map<String,Class<?>>    _collectionTypes;
    private final String[]                _keyProperties;
    
    // default implementations - overwritten via either overwriting allGetter()/idGetter() or providing new 
    private Func<Iterable<V>> 	_allGetter = new Func<Iterable<V>>()	{  public Iterable<V> apply() { return CollectionUtils.makeEmptyIterable(); } };
    private Func1<V,String>		_idGetter;	// no way to provide a default for this unless we want to always return null which is probably a bad idea 
    
    protected ExtendedPropertyModel(Map<String,Class<?>> fieldTypes, Map<String,Class<?>> collectionTypes, String... keyProperties) {
        _fieldTypes = fieldTypes;
        _collectionTypes = collectionTypes;
        _keyProperties = keyProperties;
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
    
    /** @return the set of property names that represent the key for an entity */
    public String[] getKeyPropertyNames() { 
        return _keyProperties; 
    }
    
    /** @return the name of the entity set this model represents and by which it is registered with OData4J */
    public abstract String 		entityName();
    /** @return the elements of the specified collection with the associated entity type */
    public abstract Iterable<?> getCollectionValue(Object target, String collectionName);
    /** @return the value of the specified property of the associated entity type */
    public abstract Object 		getPropertyValue(Object target, String propertyName);
    
    /** @return the type of Entity this Model is for */
    public abstract Class<V> 	getEntityType(); 
    /** @return a Func object which, when apply()ed will return all top-level entities of this model's type */
    public Func<Iterable<V>> 	allGetter() { return _allGetter; }
    /** @return a Func1 object which, when apply()ed will return the primary key for the provided entity instance */
    public Func1<V,String>		idGetter() { return _idGetter; }
    /** @return the name used to identify this type in the Ovation DB */
    public abstract String 		getTypeName();
    /** @return the value associated with the key */
    public V 					getEntityByKey(OEntityKey key) { return null; }

    // to be called from sub-class' ctor
    protected void setAllGetter(Func<Iterable<V>> allGetter) 	{ _allGetter = allGetter; }
    protected void setIdGetter(Func1<V,String> idGetter)		{ _idGetter = idGetter; }
    
//    /** @return the collection of values associated with the query */
// FIXME	public abstract Iterable<V> executeQuery(String query);
// FIXME    public Iterable<V> executeQueryInfo() { return (Iterable<V>)executeQueryInfo(getEntityType(), getQueryInfo()); }
    
    

    
    // ExtendedPropertyModel instances keyed by value type
    private static final HashMap<Class<?>, ExtendedPropertyModel<?>> _modelByTypeMap = Maps.newHashMap();
    private static final HashMap<String, ExtendedPropertyModel<?>>   _modelByNameMap = Maps.newHashMap();
    
    public static ExtendedPropertyModel<?> getPropertyModel(Class<?> type) {
        return _modelByTypeMap.get(type);
    }
    public static ExtendedPropertyModel<?> getPropertyModel(String name) {
        return _modelByNameMap.get(name);
    }
    public static void setPropertyModel(Class<?> type, ExtendedPropertyModel<?> model) {
        _modelByTypeMap.put(type, model);
        _modelByNameMap.put(model.entityName(), model);
    }
    public static void addPropertyModel(ExtendedPropertyModel<?> model) {
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
}
