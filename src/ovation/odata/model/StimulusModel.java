package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.Stimulus;

import com.google.common.collect.Maps;

/**
 * presents Stimulus data to the OData4J framework
a * @author Ron
 */
public class StimulusModel extends OvationModelBase<Stimulus> {
	static final Logger _log = Logger.getLogger(StimulusModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static { addStimulus(_propertyTypeMap, _collectionTypeMap);	}
	
	public StimulusModel() 	{ super(_propertyTypeMap, _collectionTypeMap); }
	
	public String 			getTypeName()	{ return "Stimulus"; }
	public String			entityName() 	{ return "Stimuli"; }
	public Class<Stimulus> 	getEntityType() { return Stimulus.class; }
	
	public Iterable<?> 	getCollectionValue	(Object target, String collectionName)	{ return getCollection((Stimulus)target, CollectionName.valueOf(collectionName)); }
	public Object 		getPropertyValue	(Object target, String propertyName) 	{ return getProperty((Stimulus)target,   PropertyName.valueOf(propertyName)); }
	
	public Func<Iterable<Stimulus>> allGetter() {
		return new Func<Iterable<Stimulus>>() { 
			public Iterable<Stimulus> apply() { 
				final Iterable<Stimulus> queryIter = executeQueryInfo();
				if (queryIter != null) {
					return queryIter;
				}
				return executeQuery(GET_ALL_PQL); 
			} 
		};
	}

	public Func1<Stimulus,String> idGetter() {
		return new Func1<Stimulus,String>() {
			public String apply(Stimulus record) {
				return record.getUuid();
			}		
		};
	}
}