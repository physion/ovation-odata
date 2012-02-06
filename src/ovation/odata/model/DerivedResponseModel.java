package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.DerivedResponse;
import ovation.Epoch;
//import ovation.NumericDataException;
import ovation.odata.model.dao.MapEntry;
import ovation.odata.util.CollectionUtils;

import com.google.common.collect.Maps;

/**
 * presents DerivedResponse data to the OData4J framework
 * @author Ron
 */
public class DerivedResponseModel extends ExtendedPropertyModel<String,DerivedResponse> {
	static final Logger _log = Logger.getLogger(AnalysisRecordModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static {
        _propertyTypeMap.put(PropertyName.Description.name(),				String.class);
        _propertyTypeMap.put(PropertyName.Epoch.name(), 					Epoch.class);
		_propertyTypeMap.put(PropertyName.Name.name(), 						String.class);
		_collectionTypeMap.put(CollectionName.DerivationParameters.name(),	MapEntry.class); // Map<String,Object>
        
        addResponseDataBase	 (_propertyTypeMap, _collectionTypeMap);
	}
	
	public DerivedResponseModel() 					{ super(_propertyTypeMap, _collectionTypeMap); }
	public String 					entityName() 	{ return "DerivedResponses"; }
	public String 					getTypeName()	{ return "DerivedResponse"; }
	public Class<DerivedResponse> 	getEntityType() { return DerivedResponse.class;	}
	public Class<String> 			getKeyType() 	{ return String.class; }

	
	public Iterable<?> getCollectionValue(Object target, String collectionName) {
		DerivedResponse obj = (DerivedResponse)target;
		switch (CollectionName.valueOf(collectionName)) {
			case DerivationParameters: 	return MapEntry.makeIterable(obj.getDerivationParameters());
	        // IOBase
			case DeviceParameters:		return MapEntry.makeIterable(obj.getDeviceParameters());
	        // TaggableEntityBase
			case KeywordTags:			return CollectionUtils.makeIterable(obj.getKeywordTags());
			case MyKeywordTags:			return CollectionUtils.makeIterable(obj.getMyKeywordTags());
			case MyTags:				return CollectionUtils.makeIterable(obj.getMyTags());
			case Tags:					return CollectionUtils.makeIterable(obj.getTags());
	        // EntityBase
			case MyProperties:			return MapEntry.makeIterable(obj.getMyProperties());		// String,Object
//			case MyResources:			return obj.getMyResources();
			case Properties:			return MapEntry.makeIterable(obj.getProperties());			// String,Object[]
			case Resources:				return obj.getResourcesIterable();
		}
		return null;
	}

	public Object getPropertyValue(Object target, String propertyName) {
		DerivedResponse obj = (DerivedResponse)target;
		try {
			switch(PropertyName.valueOf(propertyName)) {
				case Name:				return obj.getName();
				case Description:		return obj.getDescription();
				case Epoch:				return obj.getEpoch();
		        // ResponseDataBase
				case Data:				return obj.getData();
				case DataBytes:			return obj.getDataBytes();
				case DoubleData:		return obj.getDoubleData();
				case FloatData:			return obj.getFloatData();
				case FloatingPointData:	return obj.getFloatingPointData();
				case IntegerData:		return obj.getIntegerData();
				case MatlabShape:		return obj.getMatlabShape();
				case NumericDataType:	return obj.getNumericDataType();
				case Shape:				return obj.getShape();
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
		} catch (RuntimeException ndx) { 
			// was NumericDataException, but that's no longer public (??)
			// there are several reasons this exception is thrown; not all of them are due to type-mismatch (some are just thrown because there's no data at all)
		}
		return null;
	}

	public Func<Iterable<DerivedResponse>> allGetter() {
		return new Func<Iterable<DerivedResponse>>() { 
			public Iterable<DerivedResponse> apply() { 
				final Iterable<DerivedResponse> queryIter = executeQueryInfo();
				if (queryIter != null) {
					return queryIter;
				}
				return executeQuery(GET_ALL_PQL); 
			} 
		};
	}

	public Func1<DerivedResponse,String> idGetter() {
		return new Func1<DerivedResponse,String>() {
			public String apply(DerivedResponse record) {
				return record.getUuid();
			}		
		};
	}
}
