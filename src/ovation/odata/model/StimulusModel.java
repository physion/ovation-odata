package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.Epoch;
import ovation.Stimulus;
import ovation.odata.model.dao.MapEntry;
import ovation.odata.util.CollectionUtils;

import com.google.common.collect.Maps;

/**
 * presents Stimulus data to the OData4J framework
a * @author Ron
 */
public class StimulusModel extends ExtendedPropertyModel<String,Stimulus> {
	static final Logger _log = Logger.getLogger(StimulusModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static {
        _propertyTypeMap.put(PropertyName.Epoch.name(), 					Epoch.class);
        _propertyTypeMap.put(PropertyName.PluginID.name(), 					String.class);
        _collectionTypeMap.put(CollectionName.StimulusParameters.name(), 	MapEntry.class);	// Map<String,Object>
        
        addIOBase(_propertyTypeMap, _collectionTypeMap);
	}
	
	public StimulusModel() 	{ super(_propertyTypeMap, _collectionTypeMap); }
	
	public String 			getTypeName()	{ return "Stimulus"; }
	public String			entityName() 	{ return "Stimuli"; }
	public Class<Stimulus> 	getEntityType() { return Stimulus.class; }
	public Class<String> 	getKeyType() 	{ return String.class; }
	
	public Iterable<?> getCollectionValue(Object target, String collectionName) {
		Stimulus obj = (Stimulus)target;
		switch (CollectionName.valueOf(collectionName)) {
			case StimulusParameters:	return MapEntry.makeIterable(obj.getStimulusParameters());
	        // IOBase
			case DeviceParameters:		return MapEntry.makeIterable(obj.getDeviceParameters());
	        // TaggableEntityBase
			case KeywordTags:			return CollectionUtils.makeIterable(obj.getKeywordTags());
			case MyKeywordTags:			return CollectionUtils.makeIterable(obj.getMyKeywordTags());
			case MyTags:				return CollectionUtils.makeIterable(obj.getMyTags());
			case Tags:					return CollectionUtils.makeIterable(obj.getTags());
	        // EntityBase
			case MyProperties:			return MapEntry.makeIterable(obj.getMyProperties());		// String,Object
//			case MyResources:			return CollectionUtils.makeIterable(obj.getMyResources());
			case Properties:			return MapEntry.makeIterable(obj.getProperties());			// String,Object[]
			case Resources:				return obj.getResourcesIterable();
		}
		return null;
	}

	public Object getPropertyValue(Object target, String propertyName) {
		Stimulus obj = (Stimulus)target;
		switch(PropertyName.valueOf(propertyName)) {
			case Epoch:				return obj.getEpoch();
			case PluginID:			return obj.getPluginID();
	        // IOBase
			case ExternalDevice:	return obj.getExternalDevice();
			case Units:				return obj.getUnits();
	        // TaggableEntityBase
	        // EntityBase
			case Owner:				return obj.getOwner();
			case SerializedLocation:return obj.getSerializedLocation();
			case SerializedName:	return obj.getSerializedName();
			case URI:				return obj.getURI();
			case URIString:			return obj.getURIString();
			case UUID:				return obj.getUuid();
		}
		return null;
	}


	public Func<Iterable<Stimulus>> allGetter() {
		return new Func<Iterable<Stimulus>>() { 
			public Iterable<Stimulus> apply() { 
				final Iterable<Stimulus> queryIter = executeQueryInfo();
				if (queryIter != null) {
					return queryIter;
				}
				return executeQuery(GET_ALL_PQL); 
			} 
		};
	}

	public Func1<Stimulus,String> idGetter() {
		return new Func1<Stimulus,String>() {
			public String apply(Stimulus record) {
				return record.getUuid();
			}		
		};
	}
}