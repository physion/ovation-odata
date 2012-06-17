package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.DerivedResponse;

import com.google.common.collect.Maps;

/**
 * presents DerivedResponse data to the OData4J framework
 * @author Ron
 */
public class DerivedResponseModel extends OvationModelBase<DerivedResponse> {
	static final Logger _log = Logger.getLogger(DerivedResponseModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static { addDerivedResponse(_propertyTypeMap, _collectionTypeMap); }
	
	public DerivedResponseModel() 					{ super(_propertyTypeMap, _collectionTypeMap); }
	public String 					entityName() 	{ return "DerivedResponses"; }
	public String 					getTypeName()	{ return "DerivedResponse"; }
	public Class<DerivedResponse> 	getEntityType() { return DerivedResponse.class;	}
	
	public Iterable<?> 	getCollectionValue	(Object target, String collectionName)	{ return getCollection((DerivedResponse)target, CollectionName.valueOf(collectionName)); }
	public Object 		getPropertyValue	(Object target, String propertyName) 	{ return getProperty((DerivedResponse)target,   PropertyName.valueOf(propertyName)); }

	public Func<Iterable<DerivedResponse>> allGetter() {
		return new Func<Iterable<DerivedResponse>>() { 
			public Iterable<DerivedResponse> apply() { 
				final Iterable<DerivedResponse> queryIter = executeQueryInfo();
				if (queryIter != null) {
					return queryIter;
				}
				return executeQuery(GET_ALL_PQL); 
			} 
		};
	}

	public Func1<DerivedResponse,String> idGetter() {
		return new Func1<DerivedResponse,String>() {
			public String apply(DerivedResponse record) {
				return record.getUuid();
			}		
		};
	}
}
