package ovation.odata.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.core4j.Func;
import org.core4j.Func1;

import ovation.User;

import com.google.common.collect.Maps;

public class UserModel extends OvationModelBase<User> {
	static final Logger _log = Logger.getLogger(UserModel.class);


	static final HashMap<String,Class<?>> _propertyTypeMap 	 = Maps.newHashMap();
	static final HashMap<String,Class<?>> _collectionTypeMap = Maps.newHashMap();
	
	static {
		addUser(_propertyTypeMap, _collectionTypeMap);
	}
	
	public UserModel() 	{ super(_propertyTypeMap, _collectionTypeMap); }
	
	public String 			getTypeName()	{ return "User"; }
	public String 			entityName() 	{ return "Users"; }
	public Class<User>		getEntityType() { return User.class; }
	
	public Iterable<?> 	getCollectionValue(Object target, String collectionName) 	{ return getCollection((User)target, CollectionName.valueOf(collectionName)); }
	public Object 		getPropertyValue(Object target, String propertyName) 		{ return getProperty  ((User)target, PropertyName.valueOf(propertyName)); }

	public Func<Iterable<User>> allGetter() {
		return new Func<Iterable<User>>() {  
			public Iterable<User> apply() { 
				final Iterable<User> queryIter = executeQueryInfo();
				if (queryIter != null) { return queryIter; }
				return executeQuery(GET_ALL_PQL); 
			} 
		};
	}

	public Func1<User,String> idGetter() { 
		return new Func1<User,String>() { public String apply(User obj) { return obj != null ? obj.getUsername() : null; } };
	}
}
