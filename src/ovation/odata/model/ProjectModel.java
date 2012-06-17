package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.Project;
import ovation.odata.util.CollectionUtils;
import ovation.odata.util.DataContextCache;

import com.google.common.collect.Maps;

/**
 * presents Project data to the OData4J framework
 * @author Ron
 */
public class ProjectModel extends OvationModelBase<Project> {
	static final Logger _log = Logger.getLogger(ProjectModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static { OvationModelBase.addProject(_propertyTypeMap, _collectionTypeMap);	}
	
	public ProjectModel() 					{ super(_propertyTypeMap, _collectionTypeMap); }
	public String 			entityName()	{ return "Projects"; }
	public String 			getTypeName() 	{ return "Project";	}
	public Class<Project> 	getEntityType() { return Project.class; }
	
	public Iterable<?> getCollectionValue(Object target, String collectionName) { return getCollection((Project)target, CollectionName.valueOf(collectionName)); }
	public Object getPropertyValue(Object target, String propertyName) 			{ return getProperty((Project)target, PropertyName.valueOf(propertyName)); }

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