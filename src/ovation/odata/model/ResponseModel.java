package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.Epoch;
import ovation.Response;
import ovation.odata.model.dao.MapEntry;
import ovation.odata.util.CollectionUtils;

import com.google.common.collect.Maps;

/**
 * presents Response data to the OData4J framework
a * @author Ron
 */
public class ResponseModel extends ExtendedPropertyModel<String,Response> {
	static final Logger _log = Logger.getLogger(ResponseModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static {
		_propertyTypeMap.put(PropertyName.Epoch.name(),         Epoch.class);
		_propertyTypeMap.put(PropertyName.SamplingRate.name(),	Double.class);
		_propertyTypeMap.put(PropertyName.SamplingUnits.name(),	String.class);
        
        addResponseDataBase(_propertyTypeMap, _collectionTypeMap);
	}
	
	public ResponseModel() 					{ super(_propertyTypeMap, _collectionTypeMap); }
	public String			getTypeName()	{ return "Response"; }
	public String 			entityName() 	{ return "Responses"; }
	public Class<Response> 	getEntityType() { return Response.class; }
	public Class<String> 	getKeyType() 	{ return String.class; }
	
	public Iterable<?> getCollectionValue(Object target, String collectionName) {
		Response obj = (Response)target;
		switch (CollectionName.valueOf(collectionName)) {
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
		Response obj = (Response)target;
		try {
			switch(PropertyName.valueOf(propertyName)) {
				case Epoch: 			return obj.getEpoch();
				case SamplingRate: 		return obj.getSamplingRates();	// FIXME from double to double[]
				case SamplingUnits:		return obj.getSamplingUnits();
	
		        // ResponseDataBase - TODO, not public and no interface
				case Data:				return obj.getData();
				case DataBytes:			return obj.getDataBytes();
				case DoubleData:		return obj.getDoubleData();
				case FloatData:			return obj.getFloatData();
				case FloatingPointData:	return obj.getFloatingPointData();
				case IntegerData:		return obj.getIntegerData();
				case MatlabShape:		return obj.getMatlabShape();
				case NumericDataType:	return obj.getNumericDataType();
				case Shape:				return obj.getShape();
		        // IOBase - TODO, not public and no interface
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
		} catch (RuntimeException ndx) {	// used to be NumericDataException
			// there are several reasons this exception is thrown; not all of them are due to type-mismatch (some are just thrown because there's no data at all)
		}
		return null;
	}


	public Func<Iterable<Response>> allGetter() {
		return new Func<Iterable<Response>>() { 
			public Iterable<Response> apply() { 
				final Iterable<Response> queryIter = executeQueryInfo();
				if (queryIter != null) {
					return queryIter;
				}
				return executeQuery(GET_ALL_PQL); 
			} 
		};
	}

	public Func1<Response,String> idGetter() {
		return new Func1<Response,String>() {
			public String apply(Response record) {
				return record.getUuid();
			}		
		};
	}
}