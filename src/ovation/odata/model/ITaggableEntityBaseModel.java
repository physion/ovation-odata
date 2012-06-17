package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.ITaggableEntityBase;
import ovation.odata.util.CollectionUtils;

import com.google.common.collect.Maps;

public class ITaggableEntityBaseModel extends OvationModelBase<ITaggableEntityBase> {
    static final Logger _log = Logger.getLogger(ITaggableEntityBaseModel.class);

    static final HashMap<String,Class<?>> _propertyTypeMap   = Maps.newHashMap();
    static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
    
    static {
        addTaggableEntityBase(_propertyTypeMap, _collectionTypeMap);
    }
    
    public ITaggableEntityBaseModel() { super(_propertyTypeMap, _collectionTypeMap); }
    
    public String                       getTypeName()   { return "ITaggableEntityBase"; }
    public String                       entityName()    { return "_ITaggableEntityBases"; }
    public Class<ITaggableEntityBase>   getEntityType() { return ITaggableEntityBase.class; }
    
    public Iterable<?>  getCollectionValue(Object target, String collectionName)     { return getCollection((ITaggableEntityBase)target, CollectionName.valueOf(collectionName)); }
    public Object       getPropertyValue(Object target, String propertyName)         { return getProperty  ((ITaggableEntityBase)target, PropertyName.valueOf(propertyName)); }

    public Func<Iterable<ITaggableEntityBase>> allGetter() {
        return new Func<Iterable<ITaggableEntityBase>>() {  public Iterable<ITaggableEntityBase> apply() { return CollectionUtils.makeEmptyIterable(); } };
    }

    public Func1<ITaggableEntityBase,String> idGetter() { 
        return new Func1<ITaggableEntityBase,String>() { public String apply(ITaggableEntityBase obj) { return obj != null ? obj.getUuid() : null; } };
    }
}
