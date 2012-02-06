package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.Resource;
import ovation.odata.model.dao.MapEntry;
import ovation.odata.util.CollectionUtils;

import com.google.common.collect.Maps;

/**
 * presents Resource data to the OData4J framework
a * @author Ron
 */
public class ResourceModel extends ExtendedPropertyModel<String,Resource> {
	static final Logger _log = Logger.getLogger(ResourceModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static {
		_propertyTypeMap.put(PropertyName.Data.name(),	byte[].class);
		_propertyTypeMap.put(PropertyName.Name.name(), 	String.class);
		_propertyTypeMap.put(PropertyName.Notes.name(), String.class);
		_propertyTypeMap.put(PropertyName.Uti.name(), 	String.class);
        
        addTaggableEntityBase(_propertyTypeMap, _collectionTypeMap);
	}
	
	public ResourceModel() 	{ super(_propertyTypeMap, _collectionTypeMap); }
	
	public String 			entityName() 	{ return "Resources"; }
	public String 			getTypeName()	{ return "Resource"; }
	public Class<Resource>	getEntityType() { return Resource.class; }
	public Class<String> 	getKeyType() 	{ return String.class; }
	
	public Iterable<?> getCollectionValue(Object target, String collectionName) {
		Resource obj = (Resource)target;
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
		Resource obj = (Resource)target;
		switch(PropertyName.valueOf(propertyName)) {
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