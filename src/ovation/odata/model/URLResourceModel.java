package ovation.odata.model;

import java.net.URL;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.URLResource;
import ovation.odata.model.dao.MapEntry;
import ovation.odata.util.CollectionUtils;

import com.google.common.collect.Maps;

/**
 * presents URLResource data to the OData4J framework
a * @author Ron
 */
public class URLResourceModel extends ExtendedPropertyModel<String,URLResource> {
	static final Logger _log = Logger.getLogger(URLResourceModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static {
		_propertyTypeMap.put(PropertyName.Url.name(), URL.class);
		addResource(_propertyTypeMap, _collectionTypeMap);
	}
	
	
	public URLResourceModel() 	{ super(_propertyTypeMap, _collectionTypeMap); }
	
	public String 				getTypeName()	{ return "URLResource"; }
	public String 				entityName() 	{ return "URLResources"; }
	public Class<URLResource> 	getEntityType() { return URLResource.class; }
	public Class<String> 		getKeyType() 	{ return String.class; }
	
	public Iterable<?> getCollectionValue(Object target, String collectionName) {
		URLResource obj = (URLResource)target;
		switch (CollectionName.valueOf(collectionName)) {
			// TaggableEntityBase
			case KeywordTags:		return CollectionUtils.makeIterable(obj.getKeywordTags());
			case MyKeywordTags:		return CollectionUtils.makeIterable(obj.getMyKeywordTags());
			case MyTags:			return CollectionUtils.makeIterable(obj.getMyTags());
			case Tags:				return CollectionUtils.makeIterable(obj.getTags());
			// EntityBase
			case MyProperties:		return MapEntry.makeIterable(obj.getMyProperties());
//			case MyResources:		return CollectionUtils.makeIterable(obj.getMyResources());
			case Properties:		return MapEntry.makeIterable(obj.getProperties());
			case Resources:			return obj.getResourcesIterable();
		}
		return null;
	}

	public Object getPropertyValue(Object target, String propertyName) {
		URLResource obj = (URLResource)target;
		switch(PropertyName.valueOf(propertyName)) {
			case Url:					return obj.getUrl(); 
			// Resource
			case Data:					return obj.getData();
			case Name:					return obj.getName();
			case Notes:					return obj.getNotes();
			case Uti:					return obj.getUti();
			// EntityBase
			case Owner:					return obj.getOwner();
			case URI:					return obj.getURI();
			case UUID:					return obj.getUuid();
			case SerializedLocation:	return obj.getSerializedLocation();
			case SerializedName:		return obj.getSerializedName();
			case URIString:				return obj.getURIString();
		}
		return null;
	}

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