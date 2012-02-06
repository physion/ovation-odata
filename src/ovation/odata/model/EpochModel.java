package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.AnalysisRecord;
import ovation.DerivedResponse;
import ovation.Epoch;
import ovation.EpochGroup;
import ovation.Response;
import ovation.Stimulus;
import ovation.odata.model.dao.MapEntry;
import ovation.odata.util.CollectionUtils;

import com.google.common.collect.Maps;

/**
 * presents Epoch data to the OData4J framework
a * @author Ron
 */
public class EpochModel extends ExtendedPropertyModel<String,Epoch> {
	static final Logger _log = Logger.getLogger(EpochModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static {
		_propertyTypeMap.put(PropertyName.Duration.name(), 					Double.class);
		_propertyTypeMap.put(PropertyName.EpochGroup.name(), 				EpochGroup.class);
		_propertyTypeMap.put(PropertyName.ExcludeFromAnalysis.name(),		Boolean.class);
		_propertyTypeMap.put(PropertyName.NextEpoch.name(), 				Epoch.class);
		_propertyTypeMap.put(PropertyName.PreviousEpoch.name(), 			Epoch.class);
		_propertyTypeMap.put(PropertyName.ProtocolID.name(), 				String.class);

		_collectionTypeMap.put(CollectionName.AnalysisRecords.name(), 		AnalysisRecord.class);
		_collectionTypeMap.put(CollectionName.DerivedResponses.name(), 		DerivedResponse.class);
		_collectionTypeMap.put(CollectionName.MyDerivedResponses.name(), 	DerivedResponse.class);
		_collectionTypeMap.put(CollectionName.ProtocolParameters.name(), 	MapEntry.class);	// Map<String,Object>
		_collectionTypeMap.put(CollectionName.Responses.name(), 			Response.class);
		_collectionTypeMap.put(CollectionName.Stimuli.name(), 				Stimulus.class);
        
        addTaggableEntityBase(_propertyTypeMap, _collectionTypeMap);
	}
	
	public EpochModel() 	{ super(_propertyTypeMap, _collectionTypeMap); }
	
	public String 				entityName() 	{ return "Epochs"; }
	public String 				getTypeName()	{ return "Epoch"; }
	public Class<Epoch> 		getEntityType() { return Epoch.class; }
	public Class<String> 		getKeyType() 	{ return String.class; }
	
	public Iterable<?> getCollectionValue(Object target, String collectionName) {
		Epoch obj = (Epoch)target;
		switch (CollectionName.valueOf(collectionName)) {
			case AnalysisRecords:	return CollectionUtils.makeIterable(obj.getAnalysisRecords());
			case DerivedResponses:	return obj.getDerivedResponseIterable();
			case MyDerivedResponses:return CollectionUtils.makeIterable(obj.getMyDerivedResponses());
			case ProtocolParameters:return MapEntry.makeIterable(obj.getProtocolParameters());
			case Responses:			return obj.getResponseIterable();
			case Stimuli:			return obj.getStimulusIterable();

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
		Epoch obj = (Epoch)target;
		switch(PropertyName.valueOf(propertyName)) {
			case Duration: 				return Double.valueOf(obj.getDuration()); 
			case EpochGroup: 			return obj.getEpochGroup();
			case ExcludeFromAnalysis:	return obj.getExcludeFromAnalysis() ? Boolean.TRUE : Boolean.FALSE;
			case NextEpoch:				return obj.getNextEpoch();
			case PreviousEpoch:			return obj.getPreviousEpoch();
			case ProtocolID:			return obj.getProtocolID();
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

	public Func<Iterable<Epoch>> allGetter() {
		return new Func<Iterable<Epoch>>() { 
			public Iterable<Epoch> apply() { 
				final Iterable<Epoch> queryIter = executeQueryInfo();
				if (queryIter != null) {
					return queryIter;
				}
				return executeQuery(GET_ALL_PQL);  
			} 
		};
	}

	public Func1<Epoch,String> idGetter() {
		return new Func1<Epoch,String>() {
			public String apply(Epoch record) {
				return record.getUuid();
			}		
		};
	}
}