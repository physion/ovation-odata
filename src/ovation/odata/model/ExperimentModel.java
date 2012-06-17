package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.Experiment;

import com.google.common.collect.Maps;

/**
 * presents Experiment data to the OData4J framework
a * @author Ron
 */
public class ExperimentModel extends OvationModelBase<Experiment> {
	static final Logger _log = Logger.getLogger(ExperimentModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static { addExperiment(_propertyTypeMap, _collectionTypeMap); }
	
	public ExperimentModel() 	{ super(_propertyTypeMap, _collectionTypeMap); }
	
	public String 				entityName() 	{ return "Experiments"; }
	public String 				getTypeName()	{ return "Experiment"; }
	public Class<Experiment> 	getEntityType() { return Experiment.class; }
	
	public Iterable<?> 	getCollectionValue	(Object target, String collectionName)	{ return getCollection((Experiment)target, CollectionName.valueOf(collectionName)); }
	public Object 		getPropertyValue	(Object target, String propertyName) 	{ return getProperty((Experiment)target,   PropertyName.valueOf(propertyName)); }

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