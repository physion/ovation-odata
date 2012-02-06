package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.Epoch;
import ovation.EpochGroup;
import ovation.Experiment;
import ovation.ExternalDevice;
import ovation.Project;
import ovation.Source;
import ovation.odata.model.dao.MapEntry;
import ovation.odata.util.CollectionUtils;

import com.google.common.collect.Maps;

/**
 * presents Experiment data to the OData4J framework
a * @author Ron
 */
public class ExperimentModel extends ExtendedPropertyModel<String,Experiment> {
	static final Logger _log = Logger.getLogger(ExperimentModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static {
		// rules of thumb - 
		// collections (association) can ONLY refer to other entity types 
		// properties (aggregation) can refer to primitive types and entity types
		_collectionTypeMap.put(CollectionName.EpochGroups.name(), 			EpochGroup.class);
		_collectionTypeMap.put(CollectionName.Epochs.name(), 				Epoch.class);
		_collectionTypeMap.put(CollectionName.ExternalDevices.name(), 		ExternalDevice.class);
		_collectionTypeMap.put(CollectionName.Projects.name(), 				Project.class);
		_collectionTypeMap.put(CollectionName.Sources.name(), 				Source.class);
		
		addPurposeAndNotesEntity(_propertyTypeMap, _collectionTypeMap);
	}
	
	public ExperimentModel() 	{ super(_propertyTypeMap, _collectionTypeMap); }
	
	public String 				entityName() 	{ return "Experiments"; }
	public String 				getTypeName()	{ return "Experiment"; }
	public Class<Experiment> 	getEntityType() { return Experiment.class; }
	public Class<String> 		getKeyType() 	{ return String.class; }
	
	public Iterable<?> getCollectionValue(Object target, String collectionName) {
		Experiment obj = (Experiment)target;
		switch (CollectionName.valueOf(collectionName)) {
			case EpochGroups:		return CollectionUtils.makeIterable(obj.getEpochGroups());
			case Epochs:			return obj.getEpochIterable();
			case ExternalDevices:	return CollectionUtils.makeIterable(obj.getExternalDevices());
			case Projects:			return CollectionUtils.makeIterable(obj.getProjects());
			case Sources:			return CollectionUtils.makeIterable(obj.getSources());
			// TaggableEntityBase - FIXME - can't use base-type (TaggableEntityBase) because it's package-private
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
		Experiment obj = (Experiment)target;
		switch(PropertyName.valueOf(propertyName)) {
			// PurposeAndNotesEntity
			case Notes:					return obj.getNotes();
			case Purpose:				return obj.getPurpose();
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
		return null;
	}


	public Func<Iterable<Experiment>> allGetter() {
		return new Func<Iterable<Experiment>>() { 
			public Iterable<Experiment> apply() { 
				final Iterable<Experiment> queryIter = executeQueryInfo();
				if (queryIter != null) {
					return queryIter;
				}
				return executeQuery(GET_ALL_PQL); 
			} 
		};
	}

	public Func1<Experiment,String> idGetter() {
		return new Func1<Experiment,String>() {
			public String apply(Experiment record) {
				return record.getUuid();
			}		
		};
	}
}