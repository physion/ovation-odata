package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.KeywordTag;
import ovation.odata.model.dao.MapEntry;
import ovation.odata.util.CollectionUtils;

import com.google.common.collect.Maps;

/**
 * presents KeywordTag data to the OData4J framework
a * @author Ron
 */
public class KeywordTagModel extends ExtendedPropertyModel<String,KeywordTag> {
	static final Logger _log = Logger.getLogger(KeywordTagModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static {
        _propertyTypeMap.put(PropertyName.Tag.name(), 			String.class);
        _collectionTypeMap.put(CollectionName.Tagged.name(),	String.class);	// FIXME TaggableEntityBase is package-private so no way to handle this properly
        
        addEntityBase(_propertyTypeMap, _collectionTypeMap);
	}
	
	public KeywordTagModel() 	{ super(_propertyTypeMap, _collectionTypeMap); }
	
	public String 				entityName() 	{ return "KeywordTags"; }
	public String 				getTypeName()	{ return "KeywordTag"; }
	public Class<KeywordTag> 	getEntityType() { return KeywordTag.class; }
	public Class<String> 		getKeyType() 	{ return String.class; }
	
	public Iterable<?> getCollectionValue(Object target, String collectionName) {
		KeywordTag obj = (KeywordTag)target;
		switch (CollectionName.valueOf(collectionName)) {
			case Tagged:			return CollectionUtils.makeIterable(obj.getTagged());
			// EntityBase
			case MyProperties:		return MapEntry.makeIterable(obj.getMyProperties());
//			case MyResources:		return CollectionUtils.makeIterable(obj.getMyResources());
			case Properties:		return MapEntry.makeIterable(obj.getProperties());
			case Resources:			return obj.getResourcesIterable();
		}
		return null;
	}

	public Object getPropertyValue(Object target, String propertyName) {
		KeywordTag obj = (KeywordTag)target;
		switch(PropertyName.valueOf(propertyName)) {
			case Tag:					return obj.getTag();
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


	public Func<Iterable<KeywordTag>> allGetter() {
		return new Func<Iterable<KeywordTag>>() { 
			public Iterable<KeywordTag> apply() { 
				final Iterable<KeywordTag> queryIter = executeQueryInfo();
				if (queryIter != null) {
					return queryIter;
				}
				return executeQuery(GET_ALL_PQL); 
			} 
		};
	}

	public Func1<KeywordTag,String> idGetter() {
		return new Func1<KeywordTag,String>() {
			public String apply(KeywordTag record) {
				return record.getUuid();
			}		
		};
	}
}