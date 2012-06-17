package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.ExternalDevice;

import com.google.common.collect.Maps;

/**
 * presents ExternalDevice data to the OData4J framework
a * @author Ron
 */
public class ExternalDeviceModel extends OvationModelBase<ExternalDevice> {
	static final Logger _log = Logger.getLogger(ExternalDeviceModel.class);

	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static { addExternalDevice(_propertyTypeMap, _collectionTypeMap); }
	
	public ExternalDeviceModel() 	{ super(_propertyTypeMap, _collectionTypeMap); }
	
	public String 					entityName() 	{ return "ExternalDevices"; }
	public String 					getTypeName()	{ return "ExternalDevice"; }
	public Class<ExternalDevice> 	getEntityType() { return ExternalDevice.class; }
	
	public Iterable<?> 	getCollectionValue	(Object target, String collectionName)	{ return getCollection((ExternalDevice)target, CollectionName.valueOf(collectionName)); }
	public Object 		getPropertyValue	(Object target, String propertyName) 	{ return getProperty((ExternalDevice)target,   PropertyName.valueOf(propertyName)); }


	public Func<Iterable<ExternalDevice>> allGetter() {
		return new Func<Iterable<ExternalDevice>>() { 
			public Iterable<ExternalDevice> apply() {
				final Iterable<ExternalDevice> queryIter = executeQueryInfo();
				if (queryIter != null) {
					return queryIter;
				}
				return executeQuery(GET_ALL_PQL); 
			} 
		};
	}

	public Func1<ExternalDevice,String> idGetter() {
		return new Func1<ExternalDevice,String>() {
			public String apply(ExternalDevice record) {
				return record.getUuid();
			}		
		};
	}
}