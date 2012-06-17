package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.KeywordTag;

import com.google.common.collect.Maps;

/**
 * presents KeywordTag data to the OData4J framework
a * @author Ron
 */
public class KeywordTagModel extends OvationModelBase<KeywordTag> {
	static final Logger _log = Logger.getLogger(KeywordTagModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static { addKeywordTag(_propertyTypeMap, _collectionTypeMap); }
	
	public KeywordTagModel() 	{ super(_propertyTypeMap, _collectionTypeMap); }
	
	public String 				entityName() 	{ return "KeywordTags"; }
	public String 				getTypeName()	{ return "KeywordTag"; }
	public Class<KeywordTag> 	getEntityType() { return KeywordTag.class; }
	
	public Iterable<?> 	getCollectionValue	(Object target, String collectionName)	{ return getCollection((KeywordTag)target, CollectionName.valueOf(collectionName)); }
	public Object 		getPropertyValue	(Object target, String propertyName) 	{ return getProperty((KeywordTag)target,   PropertyName.valueOf(propertyName)); }


	public Func<Iterable<KeywordTag>> allGetter() {
		return new Func<Iterable<KeywordTag>>() { 
			public Iterable<KeywordTag> apply() { 
				final Iterable<KeywordTag> queryIter = executeQueryInfo();
				if (queryIter != null) {
					return queryIter;
				}
				return executeQuery(GET_ALL_PQL); 
			} 
		};
	}

	public Func1<KeywordTag,String> idGetter() {
		return new Func1<KeywordTag,String>() {
			public String apply(KeywordTag record) {
				return record.getUuid();
			}		
		};
	}
}