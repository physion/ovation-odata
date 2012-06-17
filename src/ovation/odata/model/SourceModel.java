package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.Source;

import com.google.common.collect.Maps;

/**
 * presents Source data to the OData4J framework
a * @author Ron
 */
public class SourceModel extends OvationModelBase<Source> {
	static final Logger _log = Logger.getLogger(SourceModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static { addSource(_propertyTypeMap, _collectionTypeMap); }
	
	public SourceModel() 					{ super(_propertyTypeMap, _collectionTypeMap); }
	public String 			getTypeName()	{ return "Source"; }
	public String			entityName() 	{ return "Sources"; }
	public Class<Source> 	getEntityType() { return Source.class; }
	
	public Iterable<?> 	getCollectionValue	(Object target, String collectionName)	{ return getCollection((Source)target, CollectionName.valueOf(collectionName)); }
	public Object 		getPropertyValue	(Object target, String propertyName) 	{ return getProperty((Source)target,   PropertyName.valueOf(propertyName)); }
	
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