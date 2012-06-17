package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.Epoch;

import com.google.common.collect.Maps;

/**
 * presents Epoch data to the OData4J framework
a * @author Ron
 */
public class EpochModel extends OvationModelBase<Epoch> {
	static final Logger _log = Logger.getLogger(EpochModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static { addEpoch(_propertyTypeMap, _collectionTypeMap); }
	
	public EpochModel() 	{ super(_propertyTypeMap, _collectionTypeMap); }
	
	public String 				entityName() 	{ return "Epochs"; }
	public String 				getTypeName()	{ return "Epoch"; }
	public Class<Epoch> 		getEntityType() { return Epoch.class; }
	
	public Iterable<?> 	getCollectionValue	(Object target, String collectionName)	{ return getCollection((Epoch)target, CollectionName.valueOf(collectionName)); }
	public Object 		getPropertyValue	(Object target, String propertyName) 	{ return getProperty((Epoch)target,   PropertyName.valueOf(propertyName)); }

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