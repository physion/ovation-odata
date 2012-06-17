package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;
import org.odata4j.core.OEntityKey;

import ovation.odata.util.CollectionUtils;

import com.google.common.collect.Maps;

/**
 * minor hack to work around OData4J limitation that all collection types must be registered 
 * via model (and thus appear as top-level types in the service - that seems like a bug to me)
 * @author Ron
 */
public class StringModel extends ExtendedPropertyModel<String> {
	static final Logger _log = Logger.getLogger(StringModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static {
		_propertyTypeMap.put("value", String.class);
	}
	
	public StringModel() 	{ super(_propertyTypeMap, _collectionTypeMap, "value"); }
	
	public String 			getTypeName()	{ return "String"; }
	public String 			entityName() 	{ return "_Strings"; }
	public Class<String>	getEntityType() { return String.class; }
	
	public Iterable<?> getCollectionValue(Object target, String collectionName) {
		_log.error("unrecognized collection name '" + collectionName + "'");
		return null;
	}

	public Object getPropertyValue(Object target, String propertyName) {
		if ("value".equals(propertyName)) return String.valueOf(target);
		_log.error("unrecognized property name '" + propertyName + "'");
		return null;
	}

	public Func<Iterable<String>> allGetter() {
		return new Func<Iterable<String>>() {  public Iterable<String> apply() { return CollectionUtils.makeEmptyIterable(); } };
	}

	public Func1<String,String> idGetter() { 
		return new Func1<String,String>() { public String apply(String record) { _log.error("trying to get key out of '" + record + "'"); return ""; } };
	}
	@Override
	public String getEntityByKey(OEntityKey key) {
		// TODO Auto-generated method stub
		return null;
	}
}