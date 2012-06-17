package ovation.odata.model.dao;

import java.util.Map;

import org.apache.log4j.Logger;
import org.core4j.Func1;

import ovation.odata.model.ExtendedPropertyModel;

import com.google.common.collect.Maps;

/**
 * a general purpose model to adapt collections of primitive-wrappers (Double, Integer, etc)
 * @author Ron
 *
 * @param <T>
 */
public class PrimitiveCollectionModel<T> extends ExtendedPropertyModel<T> {
    static final Logger _log = Logger.getLogger(PrimitiveCollectionModel.class);

    static Map<String,Class<?>> getMap(Class<?> type) {
        Map<String,Class<?>> typeMap = Maps.newHashMap();
        if (type != null) {
            typeMap.put("value", type);
        }
        return typeMap;
    }

    final Class<T> _type;
    final String _typeName;
    public PrimitiveCollectionModel(Class<T> type) { 
        super(getMap(type), getMap(null), "value");
        _type = type;
        _typeName = type.getSimpleName();
        setIdGetter(new Func1<T, String>() { public String apply(T record) { return String.valueOf(record); } });
    }
    
    @Override 
    public String     getTypeName()           { return _typeName; }
    @Override 
    public String     entityName()            { return "_" + _typeName + "s"; }
    @Override 
    public Class<T>   getEntityType()         { return _type; }
    @Override 
    public Iterable<?> getCollectionValue(Object target, String collectionName) {
        _log.error("unrecognized collection name '" + collectionName + "'");
        return null;
    }
    @Override 
    public Object getPropertyValue(Object target, String propertyName) {
        if ("value".equals(propertyName)) return target;
        _log.error("unrecognized property name '" + propertyName + "'");
        return null;
    }
}
