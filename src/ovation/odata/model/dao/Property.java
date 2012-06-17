package ovation.odata.model.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.core4j.Func1;

import ovation.odata.model.ExtendedPropertyModel;
import ovation.odata.util.CollectionUtils;
import ovation.odata.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * almost all properties are String,Object pairs stored in maps - this object is intended to adapt
 * that data to OData4J's view of the collections world
 * @author Ron
 */
public class Property {
	/**
	 * to adapt this type to the OData4J plumbing
	 * @author Ron
	 */
	public static class Model extends ExtendedPropertyModel<Property> {
		static final Logger _log = Logger.getLogger(Model.class);

		private enum PropertyName { key, value}
		
		static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
		static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
		
		static {
			// currently mapping everything to string-string pairs (has to support String,Object and String,Object[] eventually)
			_propertyTypeMap.put(PropertyName.key.name(), 	String.class);
			_propertyTypeMap.put(PropertyName.value.name(), String.class);
			// no collections
		}
		
		public Model() 	{ 
			super(_propertyTypeMap, _collectionTypeMap, PropertyName.key.name());
			setIdGetter(new Func1<Property,String>() { public String apply(Property record) { return record != null ? record.getKey() : null; } });
		}
		
		public String 			getTypeName()	{ return "Property"; }
		public String 			entityName() 	{ return "_Properties"; }
		public Class<Property>	getEntityType() { return Property.class; }
		
		public Iterable<?> getCollectionValue(Object target, String collectionName) {
			_log.error("unrecognized collection name '" + collectionName + "'");
			return CollectionUtils.makeEmptyIterable();
		}

		@Override
		public Object getPropertyValue(Object target, String propertyName) {
			// eventually this functionality could be moved up to base-class (so long as we can add prop and col name enums as template type of base)
			return getPropertyValue((Property)target, PropertyName.valueOf(propertyName));
		}
		public Object getPropertyValue(Property obj, PropertyName prop) {
			switch (prop) {
				case key: 	return obj.getKey();
				case value: return obj.getValue();
				default:	_log.error("unrecognized property '" + prop + "' requested for " + obj); return null;
			}
		}
	}	

	/**
	 * this version must support Map<String, Object[]>  and Map<String, Object> (both are used for properties in different cases)
	 * and since Map<String, Object[]> isn't Map<String, Object> we must use unbounded wildcard type for the value. :( 
	 * @param map
	 * @return
	 */
	public static Iterable<Property> makeIterable(Map<String,? extends Object> map) {
		List<Property> props = Lists.newArrayList();
		for (Map.Entry<String,? extends Object> entry : map.entrySet()) {
			props.add(new Property(entry.getKey(), toString(entry.getValue())));
		}

		return props;
	}
	
	private final String _key, _value;
	
	public Property(String key, String value) {
		_key = key;
		_value = value;
	}
	public String getKey() 		{ return _key; }
	public String getValue() 	{ return _value; }
	
	private static String toString(Object o) {
		return StringUtils.toString(o);
	}
}
