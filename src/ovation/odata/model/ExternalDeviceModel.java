package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.Experiment;
import ovation.ExternalDevice;
import ovation.odata.model.dao.MapEntry;
import ovation.odata.util.CollectionUtils;

import com.google.common.collect.Maps;

/**
 * presents ExternalDevice data to the OData4J framework
a * @author Ron
 */
public class ExternalDeviceModel extends ExtendedPropertyModel<String,ExternalDevice> {
	static final Logger _log = Logger.getLogger(ExternalDeviceModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static {
		_propertyTypeMap.put(PropertyName.Experiment.name(),	Experiment.class);
		_propertyTypeMap.put(PropertyName.Manufacturer.name(), 	String.class);
		_propertyTypeMap.put(PropertyName.Name.name(), 			String.class);
        
        addTaggableEntityBase(_propertyTypeMap, _collectionTypeMap);
	}
	
	public ExternalDeviceModel() 	{ super(_propertyTypeMap, _collectionTypeMap); }
	
	public String 					entityName() 	{ return "ExternalDevices"; }
	public String 					getTypeName()	{ return "ExternalDevice"; }
	public Class<ExternalDevice> 	getEntityType() { return ExternalDevice.class; }
	public Class<String> 			getKeyType() 	{ return String.class; }
	
	public Iterable<?> getCollectionValue(Object target, String collectionName) {
		ExternalDevice obj = (ExternalDevice)target;
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
		ExternalDevice obj = (ExternalDevice)target;
		switch(PropertyName.valueOf(propertyName)) {
			case Experiment:			return obj.getExperiment();
			case Manufacturer:			return obj.getManufacturer();
			case Name:					return obj.getName();
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


	public Func<Iterable<ExternalDevice>> allGetter() {
		return new Func<Iterable<ExternalDevice>>() { 
			public Iterable<ExternalDevice> apply() {
				final Iterable<ExternalDevice> queryIter = executeQueryInfo();
				if (queryIter != null) {
					return queryIter;
				}
				return executeQuery(GET_ALL_PQL); 
			} 
		};
	}

	public Func1<ExternalDevice,String> idGetter() {
		return new Func1<ExternalDevice,String>() {
			public String apply(ExternalDevice record) {
				return record.getUuid();
			}		
		};
	}
}