package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.Epoch;
import ovation.EpochGroup;
import ovation.Experiment;
import ovation.Source;
import ovation.odata.model.dao.MapEntry;
import ovation.odata.util.CollectionUtils;

import com.google.common.collect.Maps;

/**
 * presents EpochGroup data to the OData4J framework
 * @author Ron
 */
public class EpochGroupModel extends ExtendedPropertyModel<String,EpochGroup> {
	static final Logger _log = Logger.getLogger(EpochGroupModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static {
        _propertyTypeMap.put(PropertyName.EpochCount.name(),				Integer.class);
        _propertyTypeMap.put(PropertyName.Experiment.name(),				Experiment.class);
        _propertyTypeMap.put(PropertyName.Label.name(),						String.class);
        _propertyTypeMap.put(PropertyName.Parent.name(),					EpochGroup.class);
        _propertyTypeMap.put(PropertyName.Source.name(), 					Source.class);
		_collectionTypeMap.put(CollectionName.Children.name(),				EpochGroup.class);
		_collectionTypeMap.put(CollectionName.Epochs.name(),				Epoch.class);
		_collectionTypeMap.put(CollectionName.EpochsUnsorted.name(),		Epoch.class);

        addTimelineElement	 (_propertyTypeMap, _collectionTypeMap);
	}
	
	public EpochGroupModel() 	{ super(_propertyTypeMap, _collectionTypeMap); }
	
	public String 				entityName() 	{ return "EpochGroups"; }
	public String 				getTypeName()	{ return "EpochGroup"; }
	public Class<EpochGroup> 	getEntityType() { return EpochGroup.class; }
	public Class<String> 		getKeyType() 	{ return String.class; }
	
	public Iterable<?> getCollectionValue(Object target, String collectionName) {
		EpochGroup obj = (EpochGroup)target;
		switch (CollectionName.valueOf(collectionName)) {
			case Children:			return CollectionUtils.makeIterable(obj.getChildren());
			case Epochs:			return CollectionUtils.makeIterable(obj.getEpochs());
			case EpochsUnsorted:	return CollectionUtils.makeIterable(obj.getEpochsUnsorted());
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
		_log.warn("Unknown collection - " + collectionName);
		return null;
	}

	public Object getPropertyValue(Object target, String propertyName) {
		EpochGroup obj = (EpochGroup)target;
		switch(PropertyName.valueOf(propertyName)) {
			case EpochCount:			return Integer.valueOf(obj.getEpochCount());
			case Experiment:			return obj.getExperiment();
			case Label:					return obj.getLabel();
			case Parent:				return obj.getParent();
			case Source:				return obj.getSource();
			// TimelineElement
			case EndTime:				return obj.getEndTime();
			case StartTime:				return obj.getStartTime();
			// EntityBase
			case Owner:					return obj.getOwner();
			case URI:					return obj.getURI();
			case UUID:					return obj.getUuid();
			case SerializedLocation:	return obj.getSerializedLocation();
			case SerializedName:		return obj.getSerializedName();
			case URIString:				return obj.getURIString();
		}
		_log.warn("Unknown property - " + propertyName);
		return null;
	}

	public Func<Iterable<EpochGroup>> allGetter() {
		return new Func<Iterable<EpochGroup>>() { 
			public Iterable<EpochGroup> apply() { 
				final Iterable<EpochGroup> queryIter = executeQueryInfo();
				if (queryIter != null) {
					return queryIter;
				}
				return executeQuery(GET_ALL_PQL);  
			} 
		};
	}

	public Func1<EpochGroup,String> idGetter() {
		return new Func1<EpochGroup,String>() {
			public String apply(EpochGroup record) {
				return record.getUuid();
			}		
		};
	}
}