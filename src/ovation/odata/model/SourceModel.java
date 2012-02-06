package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.EpochGroup;
import ovation.Experiment;
import ovation.Source;
import ovation.odata.model.dao.MapEntry;
import ovation.odata.util.CollectionUtils;

import com.google.common.collect.Maps;

/**
 * presents Source data to the OData4J framework
a * @author Ron
 */
public class SourceModel extends ExtendedPropertyModel<String,Source> {
	static final Logger _log = Logger.getLogger(SourceModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static {
		_propertyTypeMap.put(PropertyName.Label.name(), 					String.class);
		_propertyTypeMap.put(PropertyName.Parent.name(), 					Source.class);
		_propertyTypeMap.put(PropertyName.ParentRoot.name(),				Source.class);

		_collectionTypeMap.put(CollectionName.AllEpochGroups.name(), 		EpochGroup.class);
		_collectionTypeMap.put(CollectionName.AllExperiments.name(), 		Experiment.class);
		_collectionTypeMap.put(CollectionName.Children.name(), 				Source.class);
		_collectionTypeMap.put(CollectionName.EpochGroups.name(), 			EpochGroup.class);
		_collectionTypeMap.put(CollectionName.Experiments.name(), 			Experiment.class);

        addTaggableEntityBase(_propertyTypeMap, _collectionTypeMap);
	}
	
	public SourceModel() 					{ super(_propertyTypeMap, _collectionTypeMap); }
	public String 			getTypeName()	{ return "Source"; }
	public String			entityName() 	{ return "Sources"; }
	public Class<Source> 	getEntityType() { return Source.class; }
	public Class<String> 	getKeyType() 	{ return String.class; }
	
	public Iterable<?> getCollectionValue(Object target, String collectionName) {
		Source obj = (Source)target;
		switch (CollectionName.valueOf(collectionName)) {
			case AllEpochGroups:		return CollectionUtils.makeIterable(obj.getAllEpochGroups());
			case AllExperiments:		return CollectionUtils.makeIterable(obj.getAllExperiments());
			case Children:				return CollectionUtils.makeIterable(obj.getChildren());
			case EpochGroups:			return CollectionUtils.makeIterable(obj.getEpochGroups());
			case Experiments:			return CollectionUtils.makeIterable(obj.getExperiments());
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
		Source obj = (Source)target;
		switch(PropertyName.valueOf(propertyName)) {
			case Label: 			return obj.getLabel();
			case Parent: 			return obj.getParent();
			case ParentRoot: 		return obj.getParentRoot();
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


	public Func<Iterable<Source>> allGetter() {
		return new Func<Iterable<Source>>() { 
			public Iterable<Source> apply() { 
				final Iterable<Source> queryIter = executeQueryInfo();
				if (queryIter != null) {
					return queryIter;
				}
				return executeQuery(GET_ALL_PQL); 
			} 
		};
	}

	public Func1<Source,String> idGetter() {
		return new Func1<Source,String>() {
			public String apply(Source record) {
				return record.getUuid();
			}		
		};
	}
}