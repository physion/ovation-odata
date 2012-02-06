package ovation.odata.model;

import java.net.URL;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.AnalysisRecord;
import ovation.Epoch;
import ovation.Project;
import ovation.odata.model.dao.MapEntry;
import ovation.odata.util.CollectionUtils;

import com.google.common.collect.Maps;

/**
 * presents AnalysisRecord data to the OData4J framework
 * @author Ron
 */
public class AnalysisRecordModel extends ExtendedPropertyModel<String,AnalysisRecord> {
	static final Logger _log = Logger.getLogger(AnalysisRecordModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static {
		_propertyTypeMap.put(PropertyName.EntryFunctionName.name(),			String.class);
		_propertyTypeMap.put(PropertyName.Name.name(), 						String.class);
		_propertyTypeMap.put(PropertyName.Notes.name(), 					String.class);
		_propertyTypeMap.put(PropertyName.Project.name(), 					Project.class);
		_propertyTypeMap.put(PropertyName.ScmRevision.name(), 				int.class);
		_propertyTypeMap.put(PropertyName.ScmURL.name(), 					URL.class);
		
		_collectionTypeMap.put(CollectionName.AnalysisParameters.name(),	MapEntry.class);
		_collectionTypeMap.put(CollectionName.Epochs.name(), 				Epoch.class);
        
        addTaggableEntityBase(_propertyTypeMap, _collectionTypeMap);
        addEntityBase(_propertyTypeMap, _collectionTypeMap);
	}
	
	public AnalysisRecordModel() { super(_propertyTypeMap, _collectionTypeMap); }

	public String entityName() 	{ return "AnalysisRecords"; }
	public String getTypeName()	{ return "AnalysisRecord"; }

	public Iterable<?> getCollectionValue(Object target, String collectionName) {
		AnalysisRecord obj = (AnalysisRecord)target;

		switch (CollectionName.valueOf(collectionName)) {
			case AnalysisParameters:return MapEntry.makeIterable(obj.getAnalysisParameters());	// String,Object
			case Epochs:			return CollectionUtils.makeIterable(obj.getEpochs());
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
		AnalysisRecord obj = (AnalysisRecord)target;
		PropertyName prop = PropertyName.valueOf(propertyName);
		switch (prop) {
			case EntryFunctionName: 	return obj.getEntryFunctionName();
			case Name:					return obj.getName();
			case Notes:					return obj.getNotes();
			case Project:				return obj.getProject();
			case ScmRevision:			return Integer.valueOf(obj.getScmRevision());
			case ScmURL:				return obj.getScmURL();
		}
		return ExtendedPropertyModel.getPropertyValue(obj, prop);
	}

	public Class<AnalysisRecord> 	getEntityType() { return AnalysisRecord.class;	}
	public Class<String> 			getKeyType() 	{ return String.class; }

	public Func<Iterable<AnalysisRecord>> allGetter() {
		return new Func<Iterable<AnalysisRecord>>() { 
			public Iterable<AnalysisRecord> apply() { 
				final Iterable<AnalysisRecord> queryIter = executeQueryInfo();
				if (queryIter != null) {
					return queryIter;
				}
				return executeQuery(GET_ALL_PQL); 
			} 
		};
	}

	public Func1<AnalysisRecord,String> idGetter() {
		return new Func1<AnalysisRecord,String>() {
			public String apply(AnalysisRecord record) {
				return record.getUuid();
			}		
		};
	}
}