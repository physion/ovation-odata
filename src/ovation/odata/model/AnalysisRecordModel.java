package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.AnalysisRecord;

import com.google.common.collect.Maps;

/**
 * presents AnalysisRecord data to the OData4J framework
 * @author Ron
 */
public class AnalysisRecordModel extends OvationModelBase<AnalysisRecord> {
	static final Logger _log = Logger.getLogger(AnalysisRecordModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static {
		addAnalysisRecord(_propertyTypeMap, _collectionTypeMap);
	}
	
	public AnalysisRecordModel() { super(_propertyTypeMap, _collectionTypeMap); }

	public String entityName() 	{ return "AnalysisRecords"; }
	public String getTypeName()	{ return "AnalysisRecord"; }

	public Iterable<?> 	getCollectionValue(Object target, String collectionName) 	{ return getCollection((AnalysisRecord)target, CollectionName.valueOf(collectionName));	}
	public Object 		getPropertyValue(Object target, String propertyName)		{ return getProperty((AnalysisRecord)target, PropertyName.valueOf(propertyName)); }

	public Class<AnalysisRecord> 	getEntityType() { return AnalysisRecord.class;	}

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