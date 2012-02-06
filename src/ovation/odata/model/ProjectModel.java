package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.AnalysisRecord;
import ovation.Experiment;
import ovation.Project;
import ovation.odata.model.dao.MapEntry;
import ovation.odata.util.CollectionUtils;
import ovation.odata.util.DataContextCache;

import com.google.common.collect.Maps;

/**
 * presents Project data to the OData4J framework
 * @author Ron
 */
public class ProjectModel extends ExtendedPropertyModel<String,Project> {
	static final Logger _log = Logger.getLogger(ProjectModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static {
        _propertyTypeMap.put(PropertyName.Name.name(), 						String.class);
		_collectionTypeMap.put(CollectionName.AnalysisRecords.name(), 		AnalysisRecord.class);
		_collectionTypeMap.put(CollectionName.Experiments.name(), 			Experiment.class);
		_collectionTypeMap.put(CollectionName.MyAnalysisRecords.name(), 	AnalysisRecord.class);

		addPurposeAndNotesEntity(_propertyTypeMap, _collectionTypeMap);
	}
	
	public ProjectModel() 					{ super(_propertyTypeMap, _collectionTypeMap); }
	public String 			entityName()	{ return "Projects"; }
	public String 			getTypeName() 	{ return "Project";	}
	public Class<Project> 	getEntityType() { return Project.class; }
	public Class<String> 	getKeyType() 	{ return String.class; }
	
	public Iterable<?> getCollectionValue(Object target, String collectionName) {
		Project obj = (Project)target;
		CollectionName col = CollectionName.valueOf(collectionName); 
		switch (col) {
			case AnalysisRecords:	return obj.getAnalysisRecordIterable();
			case Experiments:		return CollectionUtils.makeIterable(obj.getExperiments());
			case MyAnalysisRecords:	return obj.getMyAnalysisRecordIterable();
		}
		// look for it in base-type(s) of obj
		return ExtendedPropertyModel.getCollectionValue(obj, col);
	}

	public Object getPropertyValue(Object target, String propertyName) {
		Project obj = (Project)target;
		PropertyName prop = PropertyName.valueOf(propertyName);
		switch(prop) {
			case Name:					return obj.getName();
			// PurposeAndNotesEntity
			case Notes:					return obj.getNotes();
			case Purpose:				return obj.getPurpose();
		}
		// look for it in base-type(s) of obj
		return ExtendedPropertyModel.getPropertyValue(obj, prop);
	}

	public Func<Iterable<Project>> allGetter() {
		return new Func<Iterable<Project>>() { 
			public Iterable<Project> apply() {
				final Iterable<Project> queryIter = executeQueryInfo();
				if (queryIter != null) {
					return queryIter;
				}
				return CollectionUtils.makeIterable(DataContextCache.getThreadContext().getProjects()); 
			} 
		};
	}

	public Func1<Project,String> idGetter() {
		return new Func1<Project,String>() {
			public String apply(Project project) {
				// when using base-type getEntityByKey the uri must be base-64 encoded so it's URL-safe
				return project.getUuid();
			}		
		};
	}
}