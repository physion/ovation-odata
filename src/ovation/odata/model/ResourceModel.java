package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.Resource;

import com.google.common.collect.Maps;

/**
 * presents Resource data to the OData4J framework
a * @author Ron
 */
public class ResourceModel extends OvationModelBase<Resource> {
	static final Logger _log = Logger.getLogger(ResourceModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static { addResource(_propertyTypeMap, _collectionTypeMap);	}
	
	public ResourceModel() 	{ super(_propertyTypeMap, _collectionTypeMap); }
	
	public String 			entityName() 	{ return "Resources"; }
	public String 			getTypeName()	{ return "Resource"; }
	public Class<Resource>	getEntityType() { return Resource.class; }
	
	public Iterable<?> 	getCollectionValue	(Object target, String collectionName)	{ return getCollection((Resource)target, CollectionName.valueOf(collectionName)); }
	public Object 		getPropertyValue	(Object target, String propertyName) 	{ return getProperty((Resource)target,   PropertyName.valueOf(propertyName)); }

	public Func<Iterable<Resource>> allGetter() {
		return new Func<Iterable<Resource>>() { 
			public Iterable<Resource> apply() { 
				final Iterable<Resource> queryIter = executeQueryInfo();
				if (queryIter != null) {
					return queryIter;
				}
				return executeQuery(GET_ALL_PQL); 
			} 
		};
	}

	public Func1<Resource,String> idGetter() {
		return new Func1<Resource,String>() {
			public String apply(Resource record) {
				return record.getUuid();
			}		
		};
	}
}