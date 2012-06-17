package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.IAnnotation;
import ovation.odata.util.CollectionUtils;

import com.google.common.collect.Maps;

public class IAnnotationModel extends OvationModelBase<IAnnotation> {
	static final Logger _log = Logger.getLogger(UserModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static {
		addIAnnotation(_propertyTypeMap, _collectionTypeMap);
	}
	
	public IAnnotationModel() { super(_propertyTypeMap, _collectionTypeMap); }
	
	public String 				getTypeName()	{ return "Annotation"; }
	public String 				entityName() 	{ return "_Annotations"; }
	public Class<IAnnotation>	getEntityType() { return IAnnotation.class; }
	
	public Iterable<?> 	getCollectionValue(Object target, String collectionName) 	{ return getCollection((IAnnotation)target, CollectionName.valueOf(collectionName)); }
	public Object 		getPropertyValue(Object target, String propertyName) 		{ return getProperty  ((IAnnotation)target, PropertyName.valueOf(propertyName)); }

	public Func<Iterable<IAnnotation>> allGetter() {
		return new Func<Iterable<IAnnotation>>() {  public Iterable<IAnnotation> apply() { return CollectionUtils.makeEmptyIterable(); } };
	}

	public Func1<IAnnotation,String> idGetter() { 
		return new Func1<IAnnotation,String>() { public String apply(IAnnotation obj) { return obj != null ? obj.getUuid() : null; } };
	}
}
