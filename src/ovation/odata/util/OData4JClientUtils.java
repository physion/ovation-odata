package ovation.odata.util;

import org.core4j.Enumerable;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityGetRequest;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OProperty;
import org.odata4j.core.OQueryRequest;
import org.odata4j.core.ORelatedEntitiesLink;
import org.odata4j.core.ORelatedEntityLink;
import org.odata4j.edm.EdmAssociation;
import org.odata4j.edm.EdmAssociationSet;
import org.odata4j.edm.EdmComplexType;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntityContainer;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmFunctionImport;
import org.odata4j.edm.EdmFunctionParameter;
import org.odata4j.edm.EdmNavigationProperty;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSchema;
import org.odata4j.edm.EdmSimpleType;

public class OData4JClientUtils {

	public static String 	getStringProperty(OEntity entity, String name) { 
		OProperty<String> prop = entity.getProperty(name, String.class);
		return prop != null ? prop.getValue() : null; 
	}
/*	
	public static String[] getStringArrayCollection(OEntity entity, String name) { 
		String[] link = entity.getLink(name, String[].class);
		return prop != null ? prop.getValue() : null; 
	}
	public static double[] getDoubleArrayCollection(OEntity entity, String name) { 
		OProperty<double[]> prop = entity.getProperty(name, double[].class);
		return prop != null ? prop.getValue() : null; 
	}
	public static float[] getFloatArrayCollection(OEntity entity, String name) { 
		OProperty<String[]> prop = entity.getProperty(name, String[].class);
		return prop != null ? prop.getValue() : null; 
	}
	public static long[] getLongArrayCollection(OEntity entity, String name) { 
		OProperty<double[]> prop = entity.getProperty(name, double[].class);
		return prop != null ? prop.getValue() : null; 
	}
	public static int[] getIntArrayCollection(OEntity entity, String name) { 
		OProperty<String[]> prop = entity.getProperty(name, String[].class);
		return prop != null ? prop.getValue() : null; 
	}
/*	
	public static AbstractMap.SimpleImmutableEntry<String,String>[] getPropertyCollection(OEntity entity, String name) {
		OCollection<OObject> col = entity.getLink(name, arg1)
		OProperty<double[]> prop = entity.getProperty(name, double[].class);
		return prop != null ? prop.getValue() : null; 
	}	
	
*/	
	public static DateTime getDateTimeProperty(OEntity entity, String name) { 
		OProperty<LocalDateTime> prop = entity.getProperty(name, LocalDateTime.class);
		return prop != null && prop.getValue() != null ? prop.getValue().toDateTime() : null; 
	}
	public static Integer  getIntegerProperty(OEntity entity, String name) { 
		OProperty<Integer> prop = entity.getProperty(name, Integer.class);
		return prop != null ? prop.getValue() : null; 
	}
	public static int		getIntegerProperty(OEntity entity, String name, int def) {
		Integer val = getIntegerProperty(entity, name);
		return val != null ? val.intValue() : def;
	}
	public static byte[]	getByteArrayProperty(OEntity entity, String name) { 
		OProperty<byte[]> prop = entity.getProperty(name, byte[].class);
		return prop != null ? prop.getValue() : null; 
	}
	public static Double	getDoubleProperty(OEntity entity, String name) { 
		OProperty<Double> prop = entity.getProperty(name, Double.class);
		return prop != null ? prop.getValue() : null; 
	}
	public static Boolean	getBooleanProperty(OEntity entity, String name) { 
		OProperty<Boolean> prop = entity.getProperty(name, Boolean.class);
		return prop != null ? prop.getValue() : null; 
	}	
	
	public static OEntity getEntity(ODataConsumer client, String setName, String entityId) {
		OEntityGetRequest<OEntity> req = client.getEntity(setName, OEntityKey.create(entityId));
		return req.execute();
	}
	
	public static Enumerable<OEntity> getAllEntities(ODataConsumer client, String setName) {
		OQueryRequest<OEntity> req = client.getEntities(setName);
		return req.execute();
	}

	public static OEntity getSubEntity(ODataConsumer client, OEntity entity, String name) {
		ORelatedEntityLink link = entity.getLink(name, ORelatedEntityLink.class);
		OEntityGetRequest<OEntity> req = client.getEntity(link);
		return req.execute();
	}	
	
	public static Enumerable<OEntity> getSubEntities(ODataConsumer client, OEntity entity, String name) {
		ORelatedEntitiesLink link = entity.getLink(name, ORelatedEntitiesLink.class);
		OQueryRequest<OEntity> req = client.getEntities(link);
		return req.execute();
	}	
	
    public static void report(String msg) {
        System.out.println(msg);
    }

    public static void report(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }
    
    public static void reportEntity(String caption, OEntity entity){
        report(caption);
        for(OProperty<?> p : entity.getProperties()){
                Object v = p.getValue();
                if (p.getType().equals(EdmSimpleType.BINARY))
                        v = org.odata4j.repack.org.apache.commons.codec.binary.Base64.encodeBase64String((byte[])v).trim();
            report("  %s: %s",p.getName(),v);
        }
    }
    public static int reportEntities(ODataConsumer c, String entitySet, int limit){
        report("entitySet: " + entitySet);
        Enumerable<OEntity> entities =  c.getEntities(entitySet).execute().take(limit);
        return reportEntities(entitySet, entities);
    }
    public static int reportEntities(String entitySet, Enumerable<OEntity> entities){
        
        int count = 0;
        
        for(OEntity e :entities){
            reportEntity(entitySet + " entity" + count,e);
            count++;
        }
        report("total count: %s \n\n" , count);
        
        return count;
    }
    private static void reportProperties(Enumerable<EdmProperty> properties){
        for(EdmProperty property : properties){
            String p = String.format("Property Name=%s Type=%s Nullable=%s",property.getName(),property.getType(),property.isNullable());
            if (property.getMaxLength() != null)
                p = p + " MaxLength="+ property.getMaxLength();
            if (property.getUnicode() != null)
                p = p + " Unicode="+ property.getUnicode();
            if (property.getFixedLength() != null)
                p = p + " FixedLength="+ property.getFixedLength();   
            if (property.getStoreGeneratedPattern() != null)
                p = p + " StoreGeneratedPattern="+ property.getStoreGeneratedPattern();  
            if (property.getFcTargetPath() != null)
                p = p + " TargetPath="+ property.getFcTargetPath();
            if (property.getFcContentKind() != null)
                p = p + " ContentKind="+ property.getFcContentKind();
            if (property.getFcKeepInContent() != null)
                p = p + " KeepInContent="+ property.getFcKeepInContent();
            if (property.getFcEpmContentKind() != null)
                p = p + " EpmContentKind="+ property.getFcEpmContentKind();
            if (property.getFcEpmKeepInContent() != null)
                p = p + " EpmKeepInContent="+ property.getFcEpmKeepInContent();
            report("    "+ p);
        }
    }
    
    private static void reportMetadata(EdmDataServices services){
        
        for(EdmSchema schema : services.getSchemas()){
            report("Schema Namespace=%s",schema.getNamespace());
            
            for(EdmEntityType et : schema.getEntityTypes()){
                String ets = String.format("  EntityType Name=%s",et.getName());
                if (et.getHasStream() != null)
                    ets = ets + " HasStream="+et.getHasStream();
                report(ets);
                
                for(String key : et.getKeys()){
                    report("    Key PropertyRef Name=%s",key);
                }
                
                reportProperties(et.getProperties());
                for(EdmNavigationProperty np : et.getNavigationProperties()){
                    report("    NavigationProperty Name=%s Relationship=%s FromRole=%s ToRole=%s",
                            np.getName(),np.getRelationship().getFQNamespaceName(),np.getFromRole().getRole(),np.getToRole().getRole());
                }
                 
            }
            for(EdmComplexType ct : schema.getComplexTypes()){
                report("  ComplexType Name=%s",ct.getName());
                
                reportProperties(ct.getProperties());
               
            }
            for(EdmAssociation assoc : schema.getAssociations()){
                report("  Association Name=%s",assoc.getName());
                report("    End Role=%s Type=%s Multiplicity=%s",assoc.getEnd1().getRole(),assoc.getEnd1().getType().getFullyQualifiedTypeName(),assoc.getEnd1().getMultiplicity());
                report("    End Role=%s Type=%s Multiplicity=%s",assoc.getEnd2().getRole(),assoc.getEnd2().getType().getFullyQualifiedTypeName(),assoc.getEnd2().getMultiplicity());
            }
            for(EdmEntityContainer ec : schema.getEntityContainers()){
                report("  EntityContainer Name=%s IsDefault=%s LazyLoadingEnabled=%s",ec.getName(),ec.isDefault(),ec.getLazyLoadingEnabled());
                
                for(EdmEntitySet ees : ec.getEntitySets()){
                    report("    EntitySet Name=%s EntityType=%s",ees.getName(),ees.getType().getFullyQualifiedTypeName());
                }
                
                for(EdmAssociationSet eas : ec.getAssociationSets()){
                    report("    AssociationSet Name=%s Association=%s",eas.getName(),eas.getAssociation().getFQNamespaceName());
                    report("      End Role=%s EntitySet=%s",eas.getEnd1().getRole().getRole(),eas.getEnd1().getEntitySet().getName());
                    report("      End Role=%s EntitySet=%s",eas.getEnd2().getRole().getRole(),eas.getEnd2().getEntitySet().getName());
                }
                
                for(EdmFunctionImport efi : ec.getFunctionImports()){
                    report("    FunctionImport Name=%s EntitySet=%s ReturnType=%s HttpMethod=%s",
                            efi.getName(),efi.getEntitySet()==null?null:efi.getEntitySet().getName(),efi.getReturnType(),efi.getHttpMethod());
                    for(EdmFunctionParameter efp : efi.getParameters()){
                        report("      Parameter Name=%s Type=%s Mode=%s",efp.getName(),efp.getType(),efp.getMode());
                    }
                }
            }
        }
    }
}
