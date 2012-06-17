package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.URLResource;

import com.google.common.collect.Maps;

/**
 * presents URLResource data to the OData4J framework
a * @author Ron
 */
public class URLResourceModel extends OvationModelBase<URLResource> {
	static final Logger _log = Logger.getLogger(URLResourceModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static { addURLResource(_propertyTypeMap, _collectionTypeMap); }
	
	public URLResourceModel() 	{ super(_propertyTypeMap, _collectionTypeMap); }
	
	public String 				getTypeName()	{ return "URLResource"; }
	public String 				entityName() 	{ return "URLResources"; }
	public Class<URLResource> 	getEntityType() { return URLResource.class; }

	public Iterable<?> 	getCollectionValue	(Object target, String collectionName)	{ return getCollection((URLResource)target, CollectionName.valueOf(collectionName)); }
	public Object 		getPropertyValue	(Object target, String propertyName) 	{ return getProperty((URLResource)target,   PropertyName.valueOf(propertyName)); }

	public Func<Iterable<URLResource>> allGetter() {
		return new Func<Iterable<URLResource>>() { 
			public Iterable<URLResource> apply() {
				final Iterable<URLResource> queryIter = executeQueryInfo();
				if (queryIter != null) {
					return queryIter;
				}
				return executeQuery(GET_ALL_PQL); 
			} 
		};
	}

	public Func1<URLResource,String> idGetter() {
		return new Func1<URLResource,String>() {
			public String apply(URLResource record) {
				return record.getUuid();
			}		
		};
	}
}