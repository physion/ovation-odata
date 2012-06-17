package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.EpochGroup;

import com.google.common.collect.Maps;

/**
 * presents EpochGroup data to the OData4J framework
 * @author Ron
 */
public class EpochGroupModel extends OvationModelBase<EpochGroup> {
	static final Logger _log = Logger.getLogger(EpochGroupModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static { addEpochGroup(_propertyTypeMap, _collectionTypeMap); }
	
	public EpochGroupModel() 	{ super(_propertyTypeMap, _collectionTypeMap); }
	
	public String 				entityName() 	{ return "EpochGroups"; }
	public String 				getTypeName()	{ return "EpochGroup"; }
	public Class<EpochGroup> 	getEntityType() { return EpochGroup.class; }
	
	public Iterable<?> 	getCollectionValue	(Object target, String collectionName)	{ return getCollection((EpochGroup)target, CollectionName.valueOf(collectionName)); }
	public Object 		getPropertyValue	(Object target, String propertyName) 	{ return getProperty((EpochGroup)target,   PropertyName.valueOf(propertyName)); }

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