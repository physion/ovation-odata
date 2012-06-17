package ovation.odata.util;

import junit.framework.Assert;
import org.core4j.Enumerable;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntity;
import ovation.*;
import ovation.odata.model.OvationModelBase;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * very similar to the model hierarchy in OvationModelBase, but this one compares OData4J OEntity fields with Ovation DB fields
 * 
 * *** EntityBase (MyProperties:<String,Object>[], Owner:User, Properties:<String,Object[]>[], ResourceNames:String[], Resources:Resource[], URI:String, UUID:String, IsComplete:bool)
 * **** KeywordTag (Tag:String, Tagged:TaggableEntityBase[])
 * **** TaggableEntityBase (KeywordTags:KeywordTag[], MyKeywordTags:KeywordTag[], MyTags:String[], Tags:String[]) 
 * ***** AnnotatableEntityBase (AnnotationGroupTags:String[], Annotations:IAnnotation[], MyAnnotationGroupTags:String[], MyAnnotations:IAnnotation[]) 
 * ****** AnalysisRecord (AnalysisParameters:<String,Object>[], EntryFunctionName:String, Epochs:Epoch[], Name:String, Notes:String, Project:Project, ScmRevision:String, ScmURL:String, SerializedLocation:String)
 * ****** ExternalDevice (Experiment:Experiment, Manufacturer:String, Name:String, SerializedLocation:String)
 * ****** Source (AllEpochGroups:EpochGroup[], AllExperiments:Experiment[], ChildLeafDescendants:Source[], Children:Source[], EpochGroups:EpochGroup[], Experiments:Experiment[], Label:String, Parent:Source, ParentRoot:Source, SerializedLocation:String)
 * ****** Resource (Data:byte[], Name:String, Notes:String, Uti:String)
 * ******* URLResource (URL:String)
 * ****** IOBase (DeviceParameters:<String,Object>[], DimensionLabels:String[], ExternalDevice:ExternalDevice, Units:String)
 * ******* Stimulus (Epoch:Epoch, PluginID:String, SerializedLocation:String, StimulusParameters:<String,Object>[])
 * ******* ResponseDataBase (Data:NumericData, DataBytes:byte[], DoubleData:double[], FloatData:float[], FloatingPointData:double[], IntData:int[], IntegerData:int[], MatlabShape:long[], NumericDataType:NumericDataType, Shape:long[])
 * ******** Response (Epoch:Epoch, SamplingRates:double[], SamplingUnits:String[], SerializedLocation:String, UTI:String)
 * ******** DerivedResponse (DerivationParameters:<String,Object>[], Description:String, Epoch:Epoch, Name:String, SerializedLocation:String)
 * ****** TimelineElement (EndTime:DateTime, StartTime:DateTime)
 * ******* EpochGroup (ChildLeafDescendants:EpochGroup[], Children:EpochGroup[], EpochCount:int, Epochs:Epoch[], EpochsUnsorted:Epoch[], Experiment:Experiement, Label:String, Parent:EpochGroup, SerializedLocation:String, Source:Source)
 * ******* Epoch (AnalysisRecords:AnalysisRecord[], DerivedResponses:DerivedResponse[], DerivedResponseNames:String[], Duration:double, EpochGroup:EpochGroup, ExcludeFromAnalysis:bool, MyDerivedResponseNames:String[], MyDerivedResponses:DerivedResponse[], NextEpoch:Epoch, PreviousEpoch:Epoch, ProtocolID:String, ProtocolParameters:<String,Object>[], Responses:Response[], ResponseNames:String[], SerializedLocation:String, StimuliNames:String[], Stimuli:Stimulus[])
 * ******* PurposeAndNotesEntity[IOwnerNotes,IScientificPurpose] (Notes:String, Purpose:String)
 * ******** Experiment (EpochGroups:EpochGroup[], Epochs:Epoch[], ExternalDevices:ExternalDevice[], Projects:Project[], SerializedLocation:String, Sources:Source[])
 * ******** Project (AnalysisRecords:AnalysisRecord[], AnalysisRecordNames:String[], Experiments:Experiment[], MyAnalysisRecords:AnalysisRecord[], MyAnalysisRecordNames:String[], Name:String, SerializedLocation:String)
 * 
 * @author Ron
 */
public class OvationJUnitUtils {

	/** EntityBase (MyProperties:<String,Object>[], Owner:User, Properties:<String,Object[]>[], ResourceNames:String[], Resources:Resource[], URI:String, UUID:String, IsComplete:bool) */
	public static void assertEquals(String msg, IEntityBase fromDb, OEntity fromSvc, ODataConsumer svc, DataContext db) {
		Assert.assertNotNull(msg, fromDb);
		Assert.assertNotNull(msg, fromSvc);
		
		// simple props
		Assert.assertEquals(msg, fromDb.getURIString(), 		OData4JClientUtils.getStringProperty(fromSvc, 	"URI"));
		Assert.assertEquals(msg, fromDb.getUuid(), 				OData4JClientUtils.getStringProperty(fromSvc, 	"UUID"));
		JUnitUtils.assertEquals(msg, fromDb.isIncomplete(), 	OData4JClientUtils.getBooleanProperty(fromSvc,	"IsIncomplete"));
	//	Assert.assertEquals(msg, fromDb.getSerializedLocation(),OData4JClientUtils.getStringProperty(fromSvc, 	"SerializedLocation")); - EntityBase has this, but not IEntityBase (but AnalysisRecord does)
	//	Assert.assertEquals(msg, fromDb.getSerializedName(),	OData4JClientUtils.getStringProperty(fromSvc, 	"SerializedName"));		- EntityBase has this, but not IEntityBase

		
		// object props
		User 	owner 		= fromDb.getOwner();
		OEntity svcOwner 	= OData4JClientUtils.getSubEntity(svc, fromSvc, "Owner");
		OvationJUnitUtils.assertEquals(msg + " - user", owner, svcOwner, svc, db);

		
		// object collections
		Map<String,Object> myProperties = fromDb.getMyProperties();
		
		Map<String,Object[]> properties = fromDb.getProperties();
		
		assertEquals(msg + "- resourceNames", 	OData4JClientUtils.getSubEntities(svc, fromSvc, "ResourceNames"), 	fromDb.getResourceNames());
		assertEquals(msg + "- resources", 		OData4JClientUtils.getSubEntities(svc, fromSvc, "Resources"), 		fromDb.getResourcesIterable());
	}

	public static void assertEquals(String msg, IAnnotatableEntityBase fromDb, OEntity fromSvc, ODataConsumer svc, DataContext db) {
		assertEquals(msg, (ITaggableEntityBase)fromDb, fromSvc, svc, db);
		
		assertEquals(msg, OData4JClientUtils.getSubEntities(svc, fromSvc, "AnnotationGroupTags"), 	fromDb.getAnnotationGroupTags());
		assertEquals(msg, OData4JClientUtils.getSubEntities(svc, fromSvc, "Annotations"), 			fromDb.getAnnotations());
		assertEquals(msg, OData4JClientUtils.getSubEntities(svc, fromSvc, "MyAnnotationGroupTags"), fromDb.getMyAnnotationGroupTags());
		assertEquals(msg, OData4JClientUtils.getSubEntities(svc, fromSvc, "MyAnnotations"), 		fromDb.getMyAnnotations());
	}
	
	public static void assertEquals(String msg, ITimelineElement fromDb, OEntity fromSvc, ODataConsumer svc, DataContext db) {
		assertEquals(msg, (IAnnotatableEntityBase)fromDb, fromSvc, svc, db);
		Assert.assertEquals(msg, fromDb.getStartTime(),	OData4JClientUtils.getDateTimeProperty(fromSvc, "StartTime"));
		Assert.assertEquals(msg, fromDb.getEndTime(), 	OData4JClientUtils.getDateTimeProperty(fromSvc, "EndTime"));
	}
	
	// TaggableEntityBase extends EntityBase implements ITaggableEntityBase
	public static void assertEquals(String msg, ITaggableEntityBase fromDb, OEntity fromSvc, ODataConsumer svc, DataContext db) {
		assertEquals(msg, (IEntityBase)fromDb, fromSvc, svc, db);
		
		// TaggableEntityBase (KeywordTags:KeywordTag[], MyKeywordTags:KeywordTag[], MyTags:String[], Tags:String[])
		assertEquals(msg, OData4JClientUtils.getSubEntities(svc, fromSvc, "Tags"), 			fromDb.getTags());
		assertEquals(msg, OData4JClientUtils.getSubEntities(svc, fromSvc, "MyTags"), 		fromDb.getMyTags());
		assertEquals(msg, OData4JClientUtils.getSubEntities(svc, fromSvc, "KeywordTags"), 	fromDb.getKeywordTags());
		assertEquals(msg, OData4JClientUtils.getSubEntities(svc, fromSvc, "MyKeywordTags"), fromDb.getMyKeywordTags());
	}
	
	// KeywordTag extends EntityBase
	public static void assertEquals(String msg, KeywordTag fromDb, OEntity fromSvc, ODataConsumer svc, DataContext db) {
		assertEquals(msg, (IEntityBase)fromDb, fromSvc, svc, db);

		/* KEYWORD_TAG links - [
		 * ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Owner,title=Owner,href=KeywordTags('6eb6ee4e-b386-4013-9da0-8cdc17c6228c')/Owner], 
		 * ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyProperties,title=MyProperties,href=KeywordTags('6eb6ee4e-b386-4013-9da0-8cdc17c6228c')/MyProperties], 
		 * ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ResourceNames,title=ResourceNames,href=KeywordTags('6eb6ee4e-b386-4013-9da0-8cdc17c6228c')/ResourceNames], 
		 * ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Tagged,title=Tagged,href=KeywordTags('6eb6ee4e-b386-4013-9da0-8cdc17c6228c')/Tagged], 
		 * ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Resources,title=Resources,href=KeywordTags('6eb6ee4e-b386-4013-9da0-8cdc17c6228c')/Resources], 
		 * ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Properties,title=Properties,href=KeywordTags('6eb6ee4e-b386-4013-9da0-8cdc17c6228c')/Properties]]		
		 */
		Assert.assertEquals(msg, fromDb.getTag(), OData4JClientUtils.getStringProperty(fromSvc, "Tag"));
		
		// assert length is the same, but we don't assert contents as it might create circular loops (in case any of these contain this KeywordTag)
		ITaggableEntityBase[] tagged = fromDb.getTagged();
		Enumerable<OEntity> svcTagged = OData4JClientUtils.getSubEntities(svc, fromSvc, "Tagged");
		Assert.assertEquals(tagged.length, svcTagged.count());
	}


	public static void assertEquals(String msg, IIOBase fromDb, OEntity fromSvc, ODataConsumer svc, DataContext db) {
		assertEquals(msg, (IAnnotatableEntityBase)fromDb, fromSvc, svc, db);
/*		
    		case ExternalDevice:return obj.getExternalDevice();
    		case Units:			return obj.getUnits();
    		default: 			return getProperty((IAnnotatableEntityBase)obj, prop);
			case DeviceParameters: 	return CollectionUtils.makeIterable(obj.getDeviceParameters());
			case DimensionLabels:	return CollectionUtils.makeEmptyIterable(); // obj.getDimensionLables();	// FIXME
			default: return getCollection(obj, col);
*/
	}
    
	public static void assertEquals(String msg, IResponseDataBase fromDb, OEntity fromSvc, ODataConsumer svc, DataContext db) {
		assertEquals(msg, (IIOBase)fromDb, fromSvc, svc, db);
		
    	Response 		res = fromDb instanceof Response ? (Response)fromDb : null;
    	DerivedResponse dRes = fromDb instanceof DerivedResponse ? (DerivedResponse)fromDb : null;
    	Assert.assertTrue(msg + " invalid type of IResponseDataBase", res != null || dRes != null);

/*    	
	    		case NumericData:		return res != null ? res.getData() 				: dRes.getData();
	    		case Data:				return res != null ? res.getDataBytes() 		: dRes.getDataBytes();
	    		case NumericDataType:	return res != null ? res.getNumericDataType() 	: dRes.getNumericDataType();
	    		default: 				return getProperty(()obj, prop);
			case DoubleData:		return CollectionUtils.makeIterable(res != null ? res.getDoubleData() 		: dRes.getDoubleData());
			case FloatData:			return CollectionUtils.makeIterable(res != null ? res.getFloatData() 		: dRes.getFloatData());
			case FloatingPointData:	return CollectionUtils.makeIterable(res != null ? res.getFloatingPointData(): dRes.getFloatingPointData());
			case IntData:			return CollectionUtils.makeIterable(res != null ? res.getIntData() 			: dRes.getIntData());
			case IntegerData:		return CollectionUtils.makeIterable(res != null ? res.getIntegerData() 		: dRes.getIntegerData());
			case MatlabShape:		return CollectionUtils.makeIterable(res != null ? res.getMatlabShape() 		: dRes.getMatlabShape());
			case Shape:				return CollectionUtils.makeIterable(res != null ? res.getShape() 			: dRes.getShape());
			default: 				return getCollection((IIOBase)obj, col);
*/			
    }
    
	public static void assertEquals(String msg, IAnnotation fromDb, OEntity fromSvc, ODataConsumer svc, DataContext db) {
		assertEquals(msg, (ITaggableEntityBase)fromDb, fromSvc, svc, db);
/*		
	    	case Text:	return obj.getText();
	    	default: 	return getProperty(()obj, prop); 
    		case Annotated:	return CollectionUtils.makeIterable(obj.getAnnotated()); 
	    	default:		return getCollection((ITaggableEntityBase)obj, col); 
*/
	}
	
	
/*
EXPERIMENT links - [ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Owner,title=Owner,href=Experiments('0519d351-e28d-4978-b11a-f1a1fc0a08be')/Owner], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ResourceNames,title=ResourceNames,href=Experiments('0519d351-e28d-4978-b11a-f1a1fc0a08be')/ResourceNames], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/EpochGroups,title=EpochGroups,href=Experiments('0519d351-e28d-4978-b11a-f1a1fc0a08be')/EpochGroups], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyTags,title=MyTags,href=Experiments('0519d351-e28d-4978-b11a-f1a1fc0a08be')/MyTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ExternalDevices,title=ExternalDevices,href=Experiments('0519d351-e28d-4978-b11a-f1a1fc0a08be')/ExternalDevices], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Sources,title=Sources,href=Experiments('0519d351-e28d-4978-b11a-f1a1fc0a08be')/Sources], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotationGroupTags,title=MyAnnotationGroupTags,href=Experiments('0519d351-e28d-4978-b11a-f1a1fc0a08be')/MyAnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Properties,title=Properties,href=Experiments('0519d351-e28d-4978-b11a-f1a1fc0a08be')/Properties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Projects,title=Projects,href=Experiments('0519d351-e28d-4978-b11a-f1a1fc0a08be')/Projects], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotations,title=MyAnnotations,href=Experiments('0519d351-e28d-4978-b11a-f1a1fc0a08be')/MyAnnotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Tags,title=Tags,href=Experiments('0519d351-e28d-4978-b11a-f1a1fc0a08be')/Tags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyProperties,title=MyProperties,href=Experiments('0519d351-e28d-4978-b11a-f1a1fc0a08be')/MyProperties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyKeywordTags,title=MyKeywordTags,href=Experiments('0519d351-e28d-4978-b11a-f1a1fc0a08be')/MyKeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AnnotationGroupTags,title=AnnotationGroupTags,href=Experiments('0519d351-e28d-4978-b11a-f1a1fc0a08be')/AnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Annotations,title=Annotations,href=Experiments('0519d351-e28d-4978-b11a-f1a1fc0a08be')/Annotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/KeywordTags,title=KeywordTags,href=Experiments('0519d351-e28d-4978-b11a-f1a1fc0a08be')/KeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Epochs,title=Epochs,href=Experiments('0519d351-e28d-4978-b11a-f1a1fc0a08be')/Epochs], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Resources,title=Resources,href=Experiments('0519d351-e28d-4978-b11a-f1a1fc0a08be')/Resources]]
EPOCH_GROUP links - [ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Source,title=Source,href=EpochGroups('bfbcc14c-bdf4-4b43-9e2b-04b029c46a48')/Source], ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Owner,title=Owner,href=EpochGroups('bfbcc14c-bdf4-4b43-9e2b-04b029c46a48')/Owner], ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ParentEpochGroup,title=ParentEpochGroup,href=EpochGroups('bfbcc14c-bdf4-4b43-9e2b-04b029c46a48')/ParentEpochGroup], ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Experiment,title=Experiment,href=EpochGroups('bfbcc14c-bdf4-4b43-9e2b-04b029c46a48')/Experiment], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ResourceNames,title=ResourceNames,href=EpochGroups('bfbcc14c-bdf4-4b43-9e2b-04b029c46a48')/ResourceNames], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyTags,title=MyTags,href=EpochGroups('bfbcc14c-bdf4-4b43-9e2b-04b029c46a48')/MyTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotationGroupTags,title=MyAnnotationGroupTags,href=EpochGroups('bfbcc14c-bdf4-4b43-9e2b-04b029c46a48')/MyAnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Properties,title=Properties,href=EpochGroups('bfbcc14c-bdf4-4b43-9e2b-04b029c46a48')/Properties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotations,title=MyAnnotations,href=EpochGroups('bfbcc14c-bdf4-4b43-9e2b-04b029c46a48')/MyAnnotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Tags,title=Tags,href=EpochGroups('bfbcc14c-bdf4-4b43-9e2b-04b029c46a48')/Tags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyProperties,title=MyProperties,href=EpochGroups('bfbcc14c-bdf4-4b43-9e2b-04b029c46a48')/MyProperties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyKeywordTags,title=MyKeywordTags,href=EpochGroups('bfbcc14c-bdf4-4b43-9e2b-04b029c46a48')/MyKeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AnnotationGroupTags,title=AnnotationGroupTags,href=EpochGroups('bfbcc14c-bdf4-4b43-9e2b-04b029c46a48')/AnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/GroupChildren,title=GroupChildren,href=EpochGroups('bfbcc14c-bdf4-4b43-9e2b-04b029c46a48')/GroupChildren], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Annotations,title=Annotations,href=EpochGroups('bfbcc14c-bdf4-4b43-9e2b-04b029c46a48')/Annotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/KeywordTags,title=KeywordTags,href=EpochGroups('bfbcc14c-bdf4-4b43-9e2b-04b029c46a48')/KeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Epochs,title=Epochs,href=EpochGroups('bfbcc14c-bdf4-4b43-9e2b-04b029c46a48')/Epochs], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/EpochsUnsorted,title=EpochsUnsorted,href=EpochGroups('bfbcc14c-bdf4-4b43-9e2b-04b029c46a48')/EpochsUnsorted], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Resources,title=Resources,href=EpochGroups('bfbcc14c-bdf4-4b43-9e2b-04b029c46a48')/Resources], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ChildLeafGroupDescendants,title=ChildLeafGroupDescendants,href=EpochGroups('bfbcc14c-bdf4-4b43-9e2b-04b029c46a48')/ChildLeafGroupDescendants]]
RESPONSE links - [ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Owner,title=Owner,href=Responses('aa1cb51a-3aa1-4aea-8a5c-dc33226e965f')/Owner], ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/NumericDataType,title=NumericDataType,href=Responses('aa1cb51a-3aa1-4aea-8a5c-dc33226e965f')/NumericDataType], ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ExternalDevice,title=ExternalDevice,href=Responses('aa1cb51a-3aa1-4aea-8a5c-dc33226e965f')/ExternalDevice], ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Epoch,title=Epoch,href=Responses('aa1cb51a-3aa1-4aea-8a5c-dc33226e965f')/Epoch], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/SamplingUnits,title=SamplingUnits,href=Responses('aa1cb51a-3aa1-4aea-8a5c-dc33226e965f')/SamplingUnits], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ResourceNames,title=ResourceNames,href=Responses('aa1cb51a-3aa1-4aea-8a5c-dc33226e965f')/ResourceNames], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/SamplingRates,title=SamplingRates,href=Responses('aa1cb51a-3aa1-4aea-8a5c-dc33226e965f')/SamplingRates], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/FloatingPointData,title=FloatingPointData,href=Responses('aa1cb51a-3aa1-4aea-8a5c-dc33226e965f')/FloatingPointData], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyTags,title=MyTags,href=Responses('aa1cb51a-3aa1-4aea-8a5c-dc33226e965f')/MyTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/FloatData,title=FloatData,href=Responses('aa1cb51a-3aa1-4aea-8a5c-dc33226e965f')/FloatData], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/DeviceParameters,title=DeviceParameters,href=Responses('aa1cb51a-3aa1-4aea-8a5c-dc33226e965f')/DeviceParameters], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotationGroupTags,title=MyAnnotationGroupTags,href=Responses('aa1cb51a-3aa1-4aea-8a5c-dc33226e965f')/MyAnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Properties,title=Properties,href=Responses('aa1cb51a-3aa1-4aea-8a5c-dc33226e965f')/Properties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Tags,title=Tags,href=Responses('aa1cb51a-3aa1-4aea-8a5c-dc33226e965f')/Tags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotations,title=MyAnnotations,href=Responses('aa1cb51a-3aa1-4aea-8a5c-dc33226e965f')/MyAnnotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Shape,title=Shape,href=Responses('aa1cb51a-3aa1-4aea-8a5c-dc33226e965f')/Shape], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyProperties,title=MyProperties,href=Responses('aa1cb51a-3aa1-4aea-8a5c-dc33226e965f')/MyProperties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyKeywordTags,title=MyKeywordTags,href=Responses('aa1cb51a-3aa1-4aea-8a5c-dc33226e965f')/MyKeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MatlabShape,title=MatlabShape,href=Responses('aa1cb51a-3aa1-4aea-8a5c-dc33226e965f')/MatlabShape], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/IntData,title=IntData,href=Responses('aa1cb51a-3aa1-4aea-8a5c-dc33226e965f')/IntData], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AnnotationGroupTags,title=AnnotationGroupTags,href=Responses('aa1cb51a-3aa1-4aea-8a5c-dc33226e965f')/AnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/IntegerData,title=IntegerData,href=Responses('aa1cb51a-3aa1-4aea-8a5c-dc33226e965f')/IntegerData], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/DoubleData,title=DoubleData,href=Responses('aa1cb51a-3aa1-4aea-8a5c-dc33226e965f')/DoubleData], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/KeywordTags,title=KeywordTags,href=Responses('aa1cb51a-3aa1-4aea-8a5c-dc33226e965f')/KeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/DimensionLabels,title=DimensionLabels,href=Responses('aa1cb51a-3aa1-4aea-8a5c-dc33226e965f')/DimensionLabels], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Annotations,title=Annotations,href=Responses('aa1cb51a-3aa1-4aea-8a5c-dc33226e965f')/Annotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Resources,title=Resources,href=Responses('aa1cb51a-3aa1-4aea-8a5c-dc33226e965f')/Resources]]
EXTERNAL_DEVICE links - [ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Owner,title=Owner,href=ExternalDevices('86be1da4-f1dd-4fa0-a4c7-145035422b5a')/Owner], ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Experiment,title=Experiment,href=ExternalDevices('86be1da4-f1dd-4fa0-a4c7-145035422b5a')/Experiment], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Tags,title=Tags,href=ExternalDevices('86be1da4-f1dd-4fa0-a4c7-145035422b5a')/Tags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotations,title=MyAnnotations,href=ExternalDevices('86be1da4-f1dd-4fa0-a4c7-145035422b5a')/MyAnnotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyProperties,title=MyProperties,href=ExternalDevices('86be1da4-f1dd-4fa0-a4c7-145035422b5a')/MyProperties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ResourceNames,title=ResourceNames,href=ExternalDevices('86be1da4-f1dd-4fa0-a4c7-145035422b5a')/ResourceNames], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyKeywordTags,title=MyKeywordTags,href=ExternalDevices('86be1da4-f1dd-4fa0-a4c7-145035422b5a')/MyKeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AnnotationGroupTags,title=AnnotationGroupTags,href=ExternalDevices('86be1da4-f1dd-4fa0-a4c7-145035422b5a')/AnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyTags,title=MyTags,href=ExternalDevices('86be1da4-f1dd-4fa0-a4c7-145035422b5a')/MyTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/KeywordTags,title=KeywordTags,href=ExternalDevices('86be1da4-f1dd-4fa0-a4c7-145035422b5a')/KeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Annotations,title=Annotations,href=ExternalDevices('86be1da4-f1dd-4fa0-a4c7-145035422b5a')/Annotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotationGroupTags,title=MyAnnotationGroupTags,href=ExternalDevices('86be1da4-f1dd-4fa0-a4c7-145035422b5a')/MyAnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Resources,title=Resources,href=ExternalDevices('86be1da4-f1dd-4fa0-a4c7-145035422b5a')/Resources], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Properties,title=Properties,href=ExternalDevices('86be1da4-f1dd-4fa0-a4c7-145035422b5a')/Properties]]
SOURCE links - [ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Owner,title=Owner,href=Sources('f77690b2-d575-4524-887c-297e40774ce9')/Owner], ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ParentRoot,title=ParentRoot,href=Sources('f77690b2-d575-4524-887c-297e40774ce9')/ParentRoot], ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ParentSource,title=ParentSource,href=Sources('f77690b2-d575-4524-887c-297e40774ce9')/ParentSource], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Experiments,title=Experiments,href=Sources('f77690b2-d575-4524-887c-297e40774ce9')/Experiments], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ResourceNames,title=ResourceNames,href=Sources('f77690b2-d575-4524-887c-297e40774ce9')/ResourceNames], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/EpochGroups,title=EpochGroups,href=Sources('f77690b2-d575-4524-887c-297e40774ce9')/EpochGroups], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/SourceChildren,title=SourceChildren,href=Sources('f77690b2-d575-4524-887c-297e40774ce9')/SourceChildren], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyTags,title=MyTags,href=Sources('f77690b2-d575-4524-887c-297e40774ce9')/MyTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ChildLeafSourceDescendants,title=ChildLeafSourceDescendants,href=Sources('f77690b2-d575-4524-887c-297e40774ce9')/ChildLeafSourceDescendants], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotationGroupTags,title=MyAnnotationGroupTags,href=Sources('f77690b2-d575-4524-887c-297e40774ce9')/MyAnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Properties,title=Properties,href=Sources('f77690b2-d575-4524-887c-297e40774ce9')/Properties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Tags,title=Tags,href=Sources('f77690b2-d575-4524-887c-297e40774ce9')/Tags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotations,title=MyAnnotations,href=Sources('f77690b2-d575-4524-887c-297e40774ce9')/MyAnnotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyProperties,title=MyProperties,href=Sources('f77690b2-d575-4524-887c-297e40774ce9')/MyProperties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AllEpochGroups,title=AllEpochGroups,href=Sources('f77690b2-d575-4524-887c-297e40774ce9')/AllEpochGroups], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyKeywordTags,title=MyKeywordTags,href=Sources('f77690b2-d575-4524-887c-297e40774ce9')/MyKeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AllExperiments,title=AllExperiments,href=Sources('f77690b2-d575-4524-887c-297e40774ce9')/AllExperiments], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AnnotationGroupTags,title=AnnotationGroupTags,href=Sources('f77690b2-d575-4524-887c-297e40774ce9')/AnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Annotations,title=Annotations,href=Sources('f77690b2-d575-4524-887c-297e40774ce9')/Annotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/KeywordTags,title=KeywordTags,href=Sources('f77690b2-d575-4524-887c-297e40774ce9')/KeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Resources,title=Resources,href=Sources('f77690b2-d575-4524-887c-297e40774ce9')/Resources]]
PROJECT links - [ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Owner,title=Owner,href=Projects('93298e5d-4819-4a55-8ebf-9b923e82d86a')/Owner], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Experiments,title=Experiments,href=Projects('93298e5d-4819-4a55-8ebf-9b923e82d86a')/Experiments], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ResourceNames,title=ResourceNames,href=Projects('93298e5d-4819-4a55-8ebf-9b923e82d86a')/ResourceNames], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AnalysisRecords,title=AnalysisRecords,href=Projects('93298e5d-4819-4a55-8ebf-9b923e82d86a')/AnalysisRecords], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyTags,title=MyTags,href=Projects('93298e5d-4819-4a55-8ebf-9b923e82d86a')/MyTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnalysisRecordNames,title=MyAnalysisRecordNames,href=Projects('93298e5d-4819-4a55-8ebf-9b923e82d86a')/MyAnalysisRecordNames], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotationGroupTags,title=MyAnnotationGroupTags,href=Projects('93298e5d-4819-4a55-8ebf-9b923e82d86a')/MyAnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Properties,title=Properties,href=Projects('93298e5d-4819-4a55-8ebf-9b923e82d86a')/Properties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotations,title=MyAnnotations,href=Projects('93298e5d-4819-4a55-8ebf-9b923e82d86a')/MyAnnotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Tags,title=Tags,href=Projects('93298e5d-4819-4a55-8ebf-9b923e82d86a')/Tags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyProperties,title=MyProperties,href=Projects('93298e5d-4819-4a55-8ebf-9b923e82d86a')/MyProperties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyKeywordTags,title=MyKeywordTags,href=Projects('93298e5d-4819-4a55-8ebf-9b923e82d86a')/MyKeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AnnotationGroupTags,title=AnnotationGroupTags,href=Projects('93298e5d-4819-4a55-8ebf-9b923e82d86a')/AnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnalysisRecords,title=MyAnalysisRecords,href=Projects('93298e5d-4819-4a55-8ebf-9b923e82d86a')/MyAnalysisRecords], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AnalysisRecordNames,title=AnalysisRecordNames,href=Projects('93298e5d-4819-4a55-8ebf-9b923e82d86a')/AnalysisRecordNames], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Annotations,title=Annotations,href=Projects('93298e5d-4819-4a55-8ebf-9b923e82d86a')/Annotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/KeywordTags,title=KeywordTags,href=Projects('93298e5d-4819-4a55-8ebf-9b923e82d86a')/KeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Resources,title=Resources,href=Projects('93298e5d-4819-4a55-8ebf-9b923e82d86a')/Resources]]
STIMULUS links - [ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Owner,title=Owner,href=Stimuli('d5f95fea-30cf-4be4-8c2e-5b455c863890')/Owner], ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ExternalDevice,title=ExternalDevice,href=Stimuli('d5f95fea-30cf-4be4-8c2e-5b455c863890')/ExternalDevice], ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Epoch,title=Epoch,href=Stimuli('d5f95fea-30cf-4be4-8c2e-5b455c863890')/Epoch], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ResourceNames,title=ResourceNames,href=Stimuli('d5f95fea-30cf-4be4-8c2e-5b455c863890')/ResourceNames], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/DeviceParameters,title=DeviceParameters,href=Stimuli('d5f95fea-30cf-4be4-8c2e-5b455c863890')/DeviceParameters], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyTags,title=MyTags,href=Stimuli('d5f95fea-30cf-4be4-8c2e-5b455c863890')/MyTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotationGroupTags,title=MyAnnotationGroupTags,href=Stimuli('d5f95fea-30cf-4be4-8c2e-5b455c863890')/MyAnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Properties,title=Properties,href=Stimuli('d5f95fea-30cf-4be4-8c2e-5b455c863890')/Properties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotations,title=MyAnnotations,href=Stimuli('d5f95fea-30cf-4be4-8c2e-5b455c863890')/MyAnnotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Tags,title=Tags,href=Stimuli('d5f95fea-30cf-4be4-8c2e-5b455c863890')/Tags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyProperties,title=MyProperties,href=Stimuli('d5f95fea-30cf-4be4-8c2e-5b455c863890')/MyProperties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyKeywordTags,title=MyKeywordTags,href=Stimuli('d5f95fea-30cf-4be4-8c2e-5b455c863890')/MyKeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/StimulusParameters,title=StimulusParameters,href=Stimuli('d5f95fea-30cf-4be4-8c2e-5b455c863890')/StimulusParameters], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AnnotationGroupTags,title=AnnotationGroupTags,href=Stimuli('d5f95fea-30cf-4be4-8c2e-5b455c863890')/AnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/DimensionLabels,title=DimensionLabels,href=Stimuli('d5f95fea-30cf-4be4-8c2e-5b455c863890')/DimensionLabels], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Annotations,title=Annotations,href=Stimuli('d5f95fea-30cf-4be4-8c2e-5b455c863890')/Annotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/KeywordTags,title=KeywordTags,href=Stimuli('d5f95fea-30cf-4be4-8c2e-5b455c863890')/KeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Resources,title=Resources,href=Stimuli('d5f95fea-30cf-4be4-8c2e-5b455c863890')/Resources]]
KEYWORD_TAG links - [ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Owner,title=Owner,href=KeywordTags('82a53a96-d8bd-41fe-8b11-8f47e1b3af42')/Owner], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyProperties,title=MyProperties,href=KeywordTags('82a53a96-d8bd-41fe-8b11-8f47e1b3af42')/MyProperties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ResourceNames,title=ResourceNames,href=KeywordTags('82a53a96-d8bd-41fe-8b11-8f47e1b3af42')/ResourceNames], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Tagged,title=Tagged,href=KeywordTags('82a53a96-d8bd-41fe-8b11-8f47e1b3af42')/Tagged], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Resources,title=Resources,href=KeywordTags('82a53a96-d8bd-41fe-8b11-8f47e1b3af42')/Resources], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Properties,title=Properties,href=KeywordTags('82a53a96-d8bd-41fe-8b11-8f47e1b3af42')/Properties]]
EPOCH links - [ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Owner,title=Owner,href=Epochs('91f5c0b2-f564-4daa-b2f7-c226c89606cf')/Owner], ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/EpochGroup,title=EpochGroup,href=Epochs('91f5c0b2-f564-4daa-b2f7-c226c89606cf')/EpochGroup], ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/PreviousEpoch,title=PreviousEpoch,href=Epochs('91f5c0b2-f564-4daa-b2f7-c226c89606cf')/PreviousEpoch], ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/NextEpoch,title=NextEpoch,href=Epochs('91f5c0b2-f564-4daa-b2f7-c226c89606cf')/NextEpoch], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ResourceNames,title=ResourceNames,href=Epochs('91f5c0b2-f564-4daa-b2f7-c226c89606cf')/ResourceNames], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AnalysisRecords,title=AnalysisRecords,href=Epochs('91f5c0b2-f564-4daa-b2f7-c226c89606cf')/AnalysisRecords], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Responses,title=Responses,href=Epochs('91f5c0b2-f564-4daa-b2f7-c226c89606cf')/Responses], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyTags,title=MyTags,href=Epochs('91f5c0b2-f564-4daa-b2f7-c226c89606cf')/MyTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/DerivedResponseNames,title=DerivedResponseNames,href=Epochs('91f5c0b2-f564-4daa-b2f7-c226c89606cf')/DerivedResponseNames], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotationGroupTags,title=MyAnnotationGroupTags,href=Epochs('91f5c0b2-f564-4daa-b2f7-c226c89606cf')/MyAnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Properties,title=Properties,href=Epochs('91f5c0b2-f564-4daa-b2f7-c226c89606cf')/Properties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/DerivedResponses,title=DerivedResponses,href=Epochs('91f5c0b2-f564-4daa-b2f7-c226c89606cf')/DerivedResponses], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Tags,title=Tags,href=Epochs('91f5c0b2-f564-4daa-b2f7-c226c89606cf')/Tags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotations,title=MyAnnotations,href=Epochs('91f5c0b2-f564-4daa-b2f7-c226c89606cf')/MyAnnotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyProperties,title=MyProperties,href=Epochs('91f5c0b2-f564-4daa-b2f7-c226c89606cf')/MyProperties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyKeywordTags,title=MyKeywordTags,href=Epochs('91f5c0b2-f564-4daa-b2f7-c226c89606cf')/MyKeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AnnotationGroupTags,title=AnnotationGroupTags,href=Epochs('91f5c0b2-f564-4daa-b2f7-c226c89606cf')/AnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Stimuli,title=Stimuli,href=Epochs('91f5c0b2-f564-4daa-b2f7-c226c89606cf')/Stimuli], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ProtocolParameters,title=ProtocolParameters,href=Epochs('91f5c0b2-f564-4daa-b2f7-c226c89606cf')/ProtocolParameters], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Annotations,title=Annotations,href=Epochs('91f5c0b2-f564-4daa-b2f7-c226c89606cf')/Annotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/KeywordTags,title=KeywordTags,href=Epochs('91f5c0b2-f564-4daa-b2f7-c226c89606cf')/KeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/StimuliNames,title=StimuliNames,href=Epochs('91f5c0b2-f564-4daa-b2f7-c226c89606cf')/StimuliNames], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ResponseNames,title=ResponseNames,href=Epochs('91f5c0b2-f564-4daa-b2f7-c226c89606cf')/ResponseNames], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Resources,title=Resources,href=Epochs('91f5c0b2-f564-4daa-b2f7-c226c89606cf')/Resources]]
RESOURCE links - [ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Owner,title=Owner,href=Resources('2c8fec8a-0248-444b-b42b-af46df407712')/Owner], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Tags,title=Tags,href=Resources('2c8fec8a-0248-444b-b42b-af46df407712')/Tags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotations,title=MyAnnotations,href=Resources('2c8fec8a-0248-444b-b42b-af46df407712')/MyAnnotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyProperties,title=MyProperties,href=Resources('2c8fec8a-0248-444b-b42b-af46df407712')/MyProperties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ResourceNames,title=ResourceNames,href=Resources('2c8fec8a-0248-444b-b42b-af46df407712')/ResourceNames], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyKeywordTags,title=MyKeywordTags,href=Resources('2c8fec8a-0248-444b-b42b-af46df407712')/MyKeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AnnotationGroupTags,title=AnnotationGroupTags,href=Resources('2c8fec8a-0248-444b-b42b-af46df407712')/AnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyTags,title=MyTags,href=Resources('2c8fec8a-0248-444b-b42b-af46df407712')/MyTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/KeywordTags,title=KeywordTags,href=Resources('2c8fec8a-0248-444b-b42b-af46df407712')/KeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Annotations,title=Annotations,href=Resources('2c8fec8a-0248-444b-b42b-af46df407712')/Annotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotationGroupTags,title=MyAnnotationGroupTags,href=Resources('2c8fec8a-0248-444b-b42b-af46df407712')/MyAnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Resources,title=Resources,href=Resources('2c8fec8a-0248-444b-b42b-af46df407712')/Resources], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Properties,title=Properties,href=Resources('2c8fec8a-0248-444b-b42b-af46df407712')/Properties]]
USER links - [ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Owner,title=Owner,href=Users('lab_head')/Owner], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Tags,title=Tags,href=Users('lab_head')/Tags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyProperties,title=MyProperties,href=Users('lab_head')/MyProperties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ResourceNames,title=ResourceNames,href=Users('lab_head')/ResourceNames], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyKeywordTags,title=MyKeywordTags,href=Users('lab_head')/MyKeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyTags,title=MyTags,href=Users('lab_head')/MyTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/KeywordTags,title=KeywordTags,href=Users('lab_head')/KeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Resources,title=Resources,href=Users('lab_head')/Resources], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Properties,title=Properties,href=Users('lab_head')/Properties]]
 */

/*
 */
	public static void assertEquals(String msg, Experiment fromDb, OEntity fromSvc, ODataConsumer svc, DataContext db) {
/*
    // Experiment extends PurposeAndNotesEntity (extends TimelineElement (extends AnnotatableEntityBase implements ITimelineElement) implements IOwnerNotes, IScientificPurpose)
    protected static Object getProperty(Experiment obj, PropertyName prop) {
    	switch (prop) {
    		case SerializedLocation:	return obj.getSerializedLocation();
	    	case Notes:					return obj.getNotes();		// 2 different interfaces so easier to just handle it here (for now)
	    	case Purpose: 				return obj.getPurpose();
	    	default: 					return getProperty((ITimelineElement)obj, prop); 
    		case EpochGroups:		return CollectionUtils.makeIterable(obj.getEpochGroups());
    		case Epochs:			return obj.getEpochIterable();
    		case ExternalDevices:	return CollectionUtils.makeIterable(obj.getExternalDevices());
    		case Projects:			return CollectionUtils.makeIterable(obj.getProjects());
    		case Sources:   		return CollectionUtils.makeIterable(obj.getSources());
    		default:				return getCollection((ITimelineElement)obj, col); 
    	
    	}
    }    
 */
/*
EXPERIMENT links - [
	ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Owner,title=Owner,href=Experiments('5440f852-0aea-4f86-a2da-e185f91929f4')/Owner], 
	ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ResourceNames,title=ResourceNames,href=Experiments('5440f852-0aea-4f86-a2da-e185f91929f4')/ResourceNames], 
	ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/EpochGroups,title=EpochGroups,href=Experiments('5440f852-0aea-4f86-a2da-e185f91929f4')/EpochGroups], 
	ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyTags,title=MyTags,href=Experiments('5440f852-0aea-4f86-a2da-e185f91929f4')/MyTags], 
	ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ExternalDevices,title=ExternalDevices,href=Experiments('5440f852-0aea-4f86-a2da-e185f91929f4')/ExternalDevices], 
	ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Sources,title=Sources,href=Experiments('5440f852-0aea-4f86-a2da-e185f91929f4')/Sources], 
	ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotationGroupTags,title=MyAnnotationGroupTags,href=Experiments('5440f852-0aea-4f86-a2da-e185f91929f4')/MyAnnotationGroupTags], 
	ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Properties,title=Properties,href=Experiments('5440f852-0aea-4f86-a2da-e185f91929f4')/Properties], 
	ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Projects,title=Projects,href=Experiments('5440f852-0aea-4f86-a2da-e185f91929f4')/Projects], 
	ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotations,title=MyAnnotations,href=Experiments('5440f852-0aea-4f86-a2da-e185f91929f4')/MyAnnotations], 
	ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Tags,title=Tags,href=Experiments('5440f852-0aea-4f86-a2da-e185f91929f4')/Tags], 
	ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyProperties,title=MyProperties,href=Experiments('5440f852-0aea-4f86-a2da-e185f91929f4')/MyProperties], 
	ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyKeywordTags,title=MyKeywordTags,href=Experiments('5440f852-0aea-4f86-a2da-e185f91929f4')/MyKeywordTags], 
	ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AnnotationGroupTags,title=AnnotationGroupTags,href=Experiments('5440f852-0aea-4f86-a2da-e185f91929f4')/AnnotationGroupTags], 
	ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Annotations,title=Annotations,href=Experiments('5440f852-0aea-4f86-a2da-e185f91929f4')/Annotations], 
	ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/KeywordTags,title=KeywordTags,href=Experiments('5440f852-0aea-4f86-a2da-e185f91929f4')/KeywordTags], 
	ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Epochs,title=Epochs,href=Experiments('5440f852-0aea-4f86-a2da-e185f91929f4')/Epochs], 
	ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Resources,title=Resources,href=Experiments('5440f852-0aea-4f86-a2da-e185f91929f4')/Resources]]
 */
		Assert.assertNotNull(msg, fromDb);
		Assert.assertNotNull(msg, fromSvc);
		Assert.assertEquals(msg, fromDb.getURIString(), 		OData4JClientUtils.getStringProperty(fromSvc, 	"URI"));
		Assert.assertEquals(msg, fromDb.getUuid(), 				OData4JClientUtils.getStringProperty(fromSvc, 	"UUID"));
		JUnitUtils.assertEquals(msg, fromDb.isIncomplete(), 	OData4JClientUtils.getBooleanProperty(fromSvc,	"IsIncomplete"));
		Assert.assertEquals(msg, fromDb.getSerializedLocation(),OData4JClientUtils.getStringProperty(fromSvc, 	"SerializedLocation"));
	//	Assert.assertEquals(msg, fromDb.getSerializedName(),	OData4JClientUtils.getStringProperty(fromSvc, 	"SerializedName"));
		
		Assert.assertEquals(msg, fromDb.getNotes(), 			OData4JClientUtils.getStringProperty(fromSvc, 	"Notes"));
		Assert.assertEquals(msg, fromDb.getStartTime(), 		OData4JClientUtils.getDateTimeProperty(fromSvc, "StartTime"));
		Assert.assertEquals(msg, fromDb.getEndTime(), 			OData4JClientUtils.getDateTimeProperty(fromSvc, "EndTime"));
		Assert.assertEquals(msg, fromDb.getPurpose(), 			OData4JClientUtils.getStringProperty(fromSvc, 	"Purpose"));
	}
	
	public static void assertEquals(String msg, AnalysisRecord fromDb, OEntity fromSvc, ODataConsumer svc, DataContext db) {
		assertEquals(msg, (IAnnotatableEntityBase)fromDb, fromSvc, svc, db);
		
		Assert.assertEquals(msg, fromDb.getNotes(), 			OData4JClientUtils.getStringProperty(fromSvc, 	"Notes"));
		Assert.assertEquals(msg, fromDb.getName(), 				OData4JClientUtils.getStringProperty(fromSvc, 	"Name"));
		Assert.assertEquals(msg, fromDb.getScmRevision(), 		OData4JClientUtils.getIntegerProperty(fromSvc,	"ScmRevision", Integer.MIN_VALUE));
		Assert.assertEquals(msg, fromDb.getEntryFunctionName(), OData4JClientUtils.getStringProperty(fromSvc, 	"EntryFunctionName"));
		Assert.assertEquals(msg, OvationModelBase.convertURLToString(fromDb.getScmURL()), OData4JClientUtils.getStringProperty(fromSvc, 	"ScmURL"));
		Assert.assertEquals(msg, fromDb.getSerializedLocation(), OData4JClientUtils.getStringProperty(fromSvc, 	"SerializedLocation"));
		
		assertEquals(msg, fromDb.getProject(), OData4JClientUtils.getSubEntity(svc, fromSvc, "Project"), svc, db);

		assertEquals(msg + "- resourceNames",  OData4JClientUtils.getSubEntities(svc, fromSvc, "AnalysisParameters"), 	fromDb.getAnalysisParameters());
		assertEquals(msg + "- epochs",  OData4JClientUtils.getSubEntities(svc, fromSvc, "Epochs"), 	fromDb.getEpochs());
		
	}

	public static void assertEquals(String msg, DerivedResponse fromDb, OEntity fromSvc, ODataConsumer svc, DataContext db) {
/*
    // DerivedResponse extends ResponseDataBase (extends IOBase implements IResponseDataBase)
    protected static Object getProperty(DerivedResponse obj, PropertyName prop) {
    	switch (prop) {
    		case Description: 			return obj.getDescription();
    		case Epoch:					return obj.getEpoch();
    		case Name:					return obj.getName();
    		case SerializedLocation:	return obj.getSerializedLocation();
    		default: 					return getProperty((IResponseDataBase)obj, prop);
			case DerivationParameters : return Property.makeIterable(obj.getDerivationParameters());
			default: 					return getCollection((IResponseDataBase)obj, col);
		}
    }
 */
		Assert.assertNotNull(msg, fromDb);
		Assert.assertNotNull(msg, fromSvc);
		Assert.assertEquals(msg, fromDb.getURIString(), 		OData4JClientUtils.getStringProperty(fromSvc, 	"URI"));
		Assert.assertEquals(msg, fromDb.getUuid(), 				OData4JClientUtils.getStringProperty(fromSvc, 	"UUID"));
		JUnitUtils.assertEquals(msg, fromDb.isIncomplete(), 	OData4JClientUtils.getBooleanProperty(fromSvc,	"IsIncomplete"));
		Assert.assertEquals(msg, fromDb.getSerializedLocation(),OData4JClientUtils.getStringProperty(fromSvc, 	"SerializedLocation"));
	//	Assert.assertEquals(msg, fromDb.getSerializedName(),	OData4JClientUtils.getStringProperty(fromSvc, 	"SerializedName"));
		
		Assert.assertEquals(msg, fromDb.getDescription(), 		OData4JClientUtils.getStringProperty(fromSvc, 	"Description"));
		Assert.assertEquals(msg, fromDb.getName(), 				OData4JClientUtils.getStringProperty(fromSvc, 	"Name"));
		JUnitUtils.assertEquals(msg, fromDb.getDataBytes(), 	OData4JClientUtils.getByteArrayProperty(fromSvc,"DataBytes"));
		Assert.assertEquals(msg, fromDb.getUnits(), 			OData4JClientUtils.getStringProperty(fromSvc, 	"Units"));
	}

	public static void assertEquals(String msg, Epoch fromDb, OEntity fromSvc, ODataConsumer svc, DataContext db) {
		Assert.assertNotNull(msg, fromDb);
		Assert.assertNotNull(msg, fromSvc);
		Assert.assertEquals(msg, fromDb.getURIString(), 				OData4JClientUtils.getStringProperty(fromSvc, 	"URI"));
		Assert.assertEquals(msg, fromDb.getUuid(), 						OData4JClientUtils.getStringProperty(fromSvc, 	"UUID"));
		JUnitUtils.assertEquals(msg, fromDb.isIncomplete(), 			OData4JClientUtils.getBooleanProperty(fromSvc,	"IsIncomplete"));
		Assert.assertEquals(msg, fromDb.getSerializedLocation(),		OData4JClientUtils.getStringProperty(fromSvc, 	"SerializedLocation"));
	//	Assert.assertEquals(msg, fromDb.getSerializedName(),			OData4JClientUtils.getStringProperty(fromSvc, 	"SerializedName"));
// EPOCH links - [ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Owner,title=Owner,href=Epochs('5f4b1bbd-979d-414c-ad8d-2a9f95e22161')/Owner], ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/EpochGroup,title=EpochGroup,href=Epochs('5f4b1bbd-979d-414c-ad8d-2a9f95e22161')/EpochGroup], ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/PreviousEpoch,title=PreviousEpoch,href=Epochs('5f4b1bbd-979d-414c-ad8d-2a9f95e22161')/PreviousEpoch], ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/NextEpoch,title=NextEpoch,href=Epochs('5f4b1bbd-979d-414c-ad8d-2a9f95e22161')/NextEpoch], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ResourceNames,title=ResourceNames,href=Epochs('5f4b1bbd-979d-414c-ad8d-2a9f95e22161')/ResourceNames], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AnalysisRecords,title=AnalysisRecords,href=Epochs('5f4b1bbd-979d-414c-ad8d-2a9f95e22161')/AnalysisRecords], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Responses,title=Responses,href=Epochs('5f4b1bbd-979d-414c-ad8d-2a9f95e22161')/Responses], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyTags,title=MyTags,href=Epochs('5f4b1bbd-979d-414c-ad8d-2a9f95e22161')/MyTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/DerivedResponseNames,title=DerivedResponseNames,href=Epochs('5f4b1bbd-979d-414c-ad8d-2a9f95e22161')/DerivedResponseNames], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotationGroupTags,title=MyAnnotationGroupTags,href=Epochs('5f4b1bbd-979d-414c-ad8d-2a9f95e22161')/MyAnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Properties,title=Properties,href=Epochs('5f4b1bbd-979d-414c-ad8d-2a9f95e22161')/Properties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/DerivedResponses,title=DerivedResponses,href=Epochs('5f4b1bbd-979d-414c-ad8d-2a9f95e22161')/DerivedResponses], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Tags,title=Tags,href=Epochs('5f4b1bbd-979d-414c-ad8d-2a9f95e22161')/Tags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotations,title=MyAnnotations,href=Epochs('5f4b1bbd-979d-414c-ad8d-2a9f95e22161')/MyAnnotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyProperties,title=MyProperties,href=Epochs('5f4b1bbd-979d-414c-ad8d-2a9f95e22161')/MyProperties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyKeywordTags,title=MyKeywordTags,href=Epochs('5f4b1bbd-979d-414c-ad8d-2a9f95e22161')/MyKeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AnnotationGroupTags,title=AnnotationGroupTags,href=Epochs('5f4b1bbd-979d-414c-ad8d-2a9f95e22161')/AnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Stimuli,title=Stimuli,href=Epochs('5f4b1bbd-979d-414c-ad8d-2a9f95e22161')/Stimuli], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ProtocolParameters,title=ProtocolParameters,href=Epochs('5f4b1bbd-979d-414c-ad8d-2a9f95e22161')/ProtocolParameters], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Annotations,title=Annotations,href=Epochs('5f4b1bbd-979d-414c-ad8d-2a9f95e22161')/Annotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/KeywordTags,title=KeywordTags,href=Epochs('5f4b1bbd-979d-414c-ad8d-2a9f95e22161')/KeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/StimuliNames,title=StimuliNames,href=Epochs('5f4b1bbd-979d-414c-ad8d-2a9f95e22161')/StimuliNames], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ResponseNames,title=ResponseNames,href=Epochs('5f4b1bbd-979d-414c-ad8d-2a9f95e22161')/ResponseNames], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Resources,title=Resources,href=Epochs('5f4b1bbd-979d-414c-ad8d-2a9f95e22161')/Resources]]	
		Assert.assertEquals(msg, fromDb.getDuration(), 					OData4JClientUtils.getDoubleProperty(fromSvc, 	"Duration"));
		JUnitUtils.assertEquals(msg, fromDb.getExcludeFromAnalysis(),	OData4JClientUtils.getBooleanProperty(fromSvc,	"ExcludeFromAnalysis"));
		Assert.assertEquals(msg, fromDb.getProtocolID(),				OData4JClientUtils.getStringProperty(fromSvc, 	"ProtocolID"));
		
		/*
    // Epoch extends TimelineElement
    protected static Object getProperty(Epoch obj, PropertyName prop) {
    	switch (prop) {
    		case Duration:				return obj.getDuration();
    		case EpochGroup:			return obj.getEpochGroup();
    		case ExcludeFromAnalysis:	return obj.getExcludeFromAnalysis();
    		case NextEpoch:				return obj.getNextEpoch();
    		case PreviousEpoch:			return obj.getPreviousEpoch();
    		case ProtocolID:			return obj.getProtocolID();
    		case SerializedLocation:	return obj.getSerializedLocation();
    		default: 					return getProperty((ITimelineElement)obj, prop);
			case AnalysisRecords:		return CollectionUtils.makeIterable(obj.getAnalysisRecords());
			case DerivedResponses:		return obj.getDerivedResponseIterable();
			case DerivedResponseNames:	return CollectionUtils.makeIterable(obj.getDerivedResponseNames());
			case ProtocolParameters:	return Property.makeIterable(obj.getProtocolParameters());
			case Responses:				return obj.getResponseIterable();
			case ResponseNames:			return CollectionUtils.makeIterable(obj.getResponseNames());
			case StimuliNames:			return CollectionUtils.makeIterable(obj.getStimuliNames());
			case Stimuli:   			return obj.getStimulusIterable();
			default: 					return getCollection((ITimelineElement)obj, col);
		}
    }
		 */
	}
	
	public static void assertEquals(String msg, EpochGroup fromDb, OEntity fromSvc, ODataConsumer svc, DataContext db) {
/*
    // EpochGroup extends TimelineElement
    protected static Object getProperty(EpochGroup obj, PropertyName prop) {
    	switch (prop) {
    		case EpochCount:		return obj.getEpochCount();
    		case Experiment:		return obj.getExperiment();
    		case Label:				return obj.getLabel();
    		case ParentEpochGroup:	return obj.getParent();
    		case SerializedLocation:return obj.getSerializedLocation();
    		case Source:			return obj.getSource();
    		default: 				return getProperty((ITimelineElement)obj, prop);
			case ChildLeafGroupDescendants:	return CollectionUtils.makeIterable(obj.getChildLeafDescendants());
			case GroupChildren:				return CollectionUtils.makeIterable(obj.getChildren());
			case Epochs:					return CollectionUtils.makeIterable(obj.getEpochs());
			case EpochsUnsorted:   			return CollectionUtils.makeIterable(obj.getEpochsUnsorted());
			default: 						return getCollection((ITimelineElement)obj, col);
		}
    }
 */
		Assert.assertNotNull(msg, fromDb);
		Assert.assertNotNull(msg, fromSvc);
		Assert.assertEquals(msg, fromDb.getURIString(), 		OData4JClientUtils.getStringProperty(fromSvc, 	"URI"));
		Assert.assertEquals(msg, fromDb.getUuid(), 				OData4JClientUtils.getStringProperty(fromSvc, 	"UUID"));
		JUnitUtils.assertEquals(msg, fromDb.isIncomplete(), 	OData4JClientUtils.getBooleanProperty(fromSvc,	"IsIncomplete"));
		Assert.assertEquals(msg, fromDb.getSerializedLocation(),OData4JClientUtils.getStringProperty(fromSvc, 	"SerializedLocation"));
	//	Assert.assertEquals(msg, fromDb.getSerializedName(),	OData4JClientUtils.getStringProperty(fromSvc, 	"SerializedName"));		- EntityBase has this, but not IEntityBase
// EPOCH_GROUP links - [ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Source,title=Source,href=EpochGroups('0502c24a-1d3c-4d27-9a63-376cd3bdc3ec')/Source], ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Owner,title=Owner,href=EpochGroups('0502c24a-1d3c-4d27-9a63-376cd3bdc3ec')/Owner], ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ParentEpochGroup,title=ParentEpochGroup,href=EpochGroups('0502c24a-1d3c-4d27-9a63-376cd3bdc3ec')/ParentEpochGroup], ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Experiment,title=Experiment,href=EpochGroups('0502c24a-1d3c-4d27-9a63-376cd3bdc3ec')/Experiment], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ResourceNames,title=ResourceNames,href=EpochGroups('0502c24a-1d3c-4d27-9a63-376cd3bdc3ec')/ResourceNames], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyTags,title=MyTags,href=EpochGroups('0502c24a-1d3c-4d27-9a63-376cd3bdc3ec')/MyTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotationGroupTags,title=MyAnnotationGroupTags,href=EpochGroups('0502c24a-1d3c-4d27-9a63-376cd3bdc3ec')/MyAnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Properties,title=Properties,href=EpochGroups('0502c24a-1d3c-4d27-9a63-376cd3bdc3ec')/Properties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotations,title=MyAnnotations,href=EpochGroups('0502c24a-1d3c-4d27-9a63-376cd3bdc3ec')/MyAnnotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Tags,title=Tags,href=EpochGroups('0502c24a-1d3c-4d27-9a63-376cd3bdc3ec')/Tags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyProperties,title=MyProperties,href=EpochGroups('0502c24a-1d3c-4d27-9a63-376cd3bdc3ec')/MyProperties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyKeywordTags,title=MyKeywordTags,href=EpochGroups('0502c24a-1d3c-4d27-9a63-376cd3bdc3ec')/MyKeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AnnotationGroupTags,title=AnnotationGroupTags,href=EpochGroups('0502c24a-1d3c-4d27-9a63-376cd3bdc3ec')/AnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/GroupChildren,title=GroupChildren,href=EpochGroups('0502c24a-1d3c-4d27-9a63-376cd3bdc3ec')/GroupChildren], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Annotations,title=Annotations,href=EpochGroups('0502c24a-1d3c-4d27-9a63-376cd3bdc3ec')/Annotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/KeywordTags,title=KeywordTags,href=EpochGroups('0502c24a-1d3c-4d27-9a63-376cd3bdc3ec')/KeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Epochs,title=Epochs,href=EpochGroups('0502c24a-1d3c-4d27-9a63-376cd3bdc3ec')/Epochs], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/EpochsUnsorted,title=EpochsUnsorted,href=EpochGroups('0502c24a-1d3c-4d27-9a63-376cd3bdc3ec')/EpochsUnsorted], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Resources,title=Resources,href=EpochGroups('0502c24a-1d3c-4d27-9a63-376cd3bdc3ec')/Resources], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ChildLeafGroupDescendants,title=ChildLeafGroupDescendants,href=EpochGroups('0502c24a-1d3c-4d27-9a63-376cd3bdc3ec')/ChildLeafGroupDescendants]]
		Assert.assertEquals(msg, fromDb.getEpochCount(),		OData4JClientUtils.getIntegerProperty(fromSvc, 	"EpochCount", -1));
		Assert.assertEquals(msg, fromDb.getLabel(), 			OData4JClientUtils.getStringProperty(fromSvc, 	"Label"));
		Assert.assertEquals(msg, fromDb.getStartTime(), 		OData4JClientUtils.getDateTimeProperty(fromSvc, "StartTime"));
		Assert.assertEquals(msg, fromDb.getEndTime(), 			OData4JClientUtils.getDateTimeProperty(fromSvc, "EndTime"));
	}

	public static void assertEquals(String msg, ExternalDevice fromDb, OEntity fromSvc, ODataConsumer svc, DataContext db) {
/*
    // ExternalDevice extends AnnotatableEntityBase
    protected static Object getProperty(ExternalDevice obj, PropertyName prop) {
    	switch (prop) {
    		case Experiment:		return obj.getExperiment();
    		case Manufacturer:		return obj.getManufacturer();
    		case Name:				return obj.getName();
    		case SerializedLocation:return obj.getSerializedLocation();
    		default: 				return getProperty((IAnnotatableEntityBase)obj, prop);
			default: return getCollection((IAnnotatableEntityBase)obj, col);
		}
    }
 */
		Assert.assertNotNull(msg, fromDb);
		Assert.assertNotNull(msg, fromSvc);
		Assert.assertEquals(msg, fromDb.getURIString(), 		OData4JClientUtils.getStringProperty(fromSvc, 	"URI"));
		Assert.assertEquals(msg, fromDb.getUuid(), 				OData4JClientUtils.getStringProperty(fromSvc, 	"UUID"));
		JUnitUtils.assertEquals(msg, fromDb.isIncomplete(), 	OData4JClientUtils.getBooleanProperty(fromSvc,	"IsIncomplete"));
		Assert.assertEquals(msg, fromDb.getSerializedLocation(),OData4JClientUtils.getStringProperty(fromSvc, 	"SerializedLocation"));
	//	Assert.assertEquals(msg, fromDb.getSerializedName(),	OData4JClientUtils.getStringProperty(fromSvc, 	"SerializedName"));		- EntityBase has this, but not IEntityBase
		//EXTERNAL_DEVICE links - [ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Owner,title=Owner,href=ExternalDevices('79fe17ea-c63d-4563-8022-a5345a5e76f0')/Owner], ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Experiment,title=Experiment,href=ExternalDevices('79fe17ea-c63d-4563-8022-a5345a5e76f0')/Experiment], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Tags,title=Tags,href=ExternalDevices('79fe17ea-c63d-4563-8022-a5345a5e76f0')/Tags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotations,title=MyAnnotations,href=ExternalDevices('79fe17ea-c63d-4563-8022-a5345a5e76f0')/MyAnnotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyProperties,title=MyProperties,href=ExternalDevices('79fe17ea-c63d-4563-8022-a5345a5e76f0')/MyProperties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ResourceNames,title=ResourceNames,href=ExternalDevices('79fe17ea-c63d-4563-8022-a5345a5e76f0')/ResourceNames], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyKeywordTags,title=MyKeywordTags,href=ExternalDevices('79fe17ea-c63d-4563-8022-a5345a5e76f0')/MyKeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AnnotationGroupTags,title=AnnotationGroupTags,href=ExternalDevices('79fe17ea-c63d-4563-8022-a5345a5e76f0')/AnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyTags,title=MyTags,href=ExternalDevices('79fe17ea-c63d-4563-8022-a5345a5e76f0')/MyTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/KeywordTags,title=KeywordTags,href=ExternalDevices('79fe17ea-c63d-4563-8022-a5345a5e76f0')/KeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Annotations,title=Annotations,href=ExternalDevices('79fe17ea-c63d-4563-8022-a5345a5e76f0')/Annotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotationGroupTags,title=MyAnnotationGroupTags,href=ExternalDevices('79fe17ea-c63d-4563-8022-a5345a5e76f0')/MyAnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Resources,title=Resources,href=ExternalDevices('79fe17ea-c63d-4563-8022-a5345a5e76f0')/Resources], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Properties,title=Properties,href=ExternalDevices('79fe17ea-c63d-4563-8022-a5345a5e76f0')/Properties]]		
		Assert.assertEquals(msg, fromDb.getName(), 				OData4JClientUtils.getStringProperty(fromSvc, 	"Name"));
		Assert.assertEquals(msg, fromDb.getManufacturer(), 		OData4JClientUtils.getStringProperty(fromSvc, 	"Manufacturer"));
	}
	
	public static void assertEquals(String msg, Project fromDb, OEntity fromSvc, ODataConsumer svc, DataContext db) {
		assertEquals(msg, (ITimelineElement)fromDb, fromSvc, svc, db);
		
		//PROJECT links - [ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Owner,title=Owner,href=Projects('9184e918-c69c-4c75-a4b7-6a195ce0986a')/Owner], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Experiments,title=Experiments,href=Projects('9184e918-c69c-4c75-a4b7-6a195ce0986a')/Experiments], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ResourceNames,title=ResourceNames,href=Projects('9184e918-c69c-4c75-a4b7-6a195ce0986a')/ResourceNames], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AnalysisRecords,title=AnalysisRecords,href=Projects('9184e918-c69c-4c75-a4b7-6a195ce0986a')/AnalysisRecords], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyTags,title=MyTags,href=Projects('9184e918-c69c-4c75-a4b7-6a195ce0986a')/MyTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnalysisRecordNames,title=MyAnalysisRecordNames,href=Projects('9184e918-c69c-4c75-a4b7-6a195ce0986a')/MyAnalysisRecordNames], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotationGroupTags,title=MyAnnotationGroupTags,href=Projects('9184e918-c69c-4c75-a4b7-6a195ce0986a')/MyAnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Properties,title=Properties,href=Projects('9184e918-c69c-4c75-a4b7-6a195ce0986a')/Properties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotations,title=MyAnnotations,href=Projects('9184e918-c69c-4c75-a4b7-6a195ce0986a')/MyAnnotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Tags,title=Tags,href=Projects('9184e918-c69c-4c75-a4b7-6a195ce0986a')/Tags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyProperties,title=MyProperties,href=Projects('9184e918-c69c-4c75-a4b7-6a195ce0986a')/MyProperties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyKeywordTags,title=MyKeywordTags,href=Projects('9184e918-c69c-4c75-a4b7-6a195ce0986a')/MyKeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AnnotationGroupTags,title=AnnotationGroupTags,href=Projects('9184e918-c69c-4c75-a4b7-6a195ce0986a')/AnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnalysisRecords,title=MyAnalysisRecords,href=Projects('9184e918-c69c-4c75-a4b7-6a195ce0986a')/MyAnalysisRecords], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AnalysisRecordNames,title=AnalysisRecordNames,href=Projects('9184e918-c69c-4c75-a4b7-6a195ce0986a')/AnalysisRecordNames], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Annotations,title=Annotations,href=Projects('9184e918-c69c-4c75-a4b7-6a195ce0986a')/Annotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/KeywordTags,title=KeywordTags,href=Projects('9184e918-c69c-4c75-a4b7-6a195ce0986a')/KeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Resources,title=Resources,href=Projects('9184e918-c69c-4c75-a4b7-6a195ce0986a')/Resources]]
		Assert.assertEquals(msg, fromDb.getName(), 				OData4JClientUtils.getStringProperty(fromSvc, 	"Name"));
		Assert.assertEquals(msg, fromDb.getSerializedLocation(),OData4JClientUtils.getStringProperty(fromSvc, 	"SerializedLocation"));
		Assert.assertEquals(msg, fromDb.getNotes(), 			OData4JClientUtils.getStringProperty(fromSvc, 	"Notes"));
		Assert.assertEquals(msg, fromDb.getPurpose(), 			OData4JClientUtils.getStringProperty(fromSvc, 	"Purpose"));
		
/*
    protected static Object getProperty(Project obj, PropertyName prop) {
    	switch (prop) {
	    	case Name:					return obj.getName();
	    	case SerializedLocation:	return obj.getSerializedLocation();
	    	case Notes:					return obj.getNotes();		// 2 different interfaces so easier to just handle it here (for now)
	    	case Purpose: 				return obj.getPurpose();
	    	default: 					return getProperty((ITimelineElement)obj, prop); 
    		case AnalysisRecords: 		return obj.getAnalysisRecordIterable();
    		case AnalysisRecordNames:	return CollectionUtils.makeIterable(obj.getAnalysisRecordNames());
    		case Experiments:			return CollectionUtils.makeIterable(obj.getExperiments());
    		case MyAnalysisRecords:		return obj.getMyAnalysisRecordIterable();
    		case MyAnalysisRecordNames:	return CollectionUtils.makeIterable(obj.getMyAnalysisRecordNames());
	    	default: 					return getCollection((ITimelineElement)obj, col); 
    	}
    }
 */
	}
	
	public static void assertEquals(String msg, Resource fromDb, OEntity fromSvc, ODataConsumer svc, DataContext db) {
		assertEquals(msg, (IAnnotatableEntityBase)fromDb, fromSvc, svc, db);
		
		Assert.assertEquals(msg, fromDb.getNotes(), 			OData4JClientUtils.getStringProperty(fromSvc, 	"Notes"));
		Assert.assertEquals(msg, fromDb.getName(), 				OData4JClientUtils.getStringProperty(fromSvc, 	"Name"));
		JUnitUtils.assertEquals(msg, fromDb.getDataBytes(), 			OData4JClientUtils.getByteArrayProperty(fromSvc,"Data"));
		Assert.assertEquals(msg, fromDb.getUti(), 				OData4JClientUtils.getStringProperty(fromSvc, 	"UTI"));

		/*
    // Resource extends AnnotatableEntityBase
    protected static Object getProperty(Resource obj, PropertyName prop) {
    	switch (prop) {
    		case Data:	return obj.getData();
    		case Name:	return obj.getName();
    		case Notes:	return obj.getNotes();
    		case UTI:	return obj.getUti();
    		default: 	return getProperty((IAnnotatableEntityBase)obj, prop);
			default: return getCollection((IAnnotatableEntityBase)obj, col);
		}
    }
		 */
	}
	
	public static void assertEquals(String msg, Response fromDb, OEntity fromSvc, ODataConsumer svc, DataContext db) {
/*
    // Response extends ResponseDataBase
    protected static Object getProperty(Response obj, PropertyName prop) {
    	switch (prop) {
    		case Epoch:				return obj.getEpoch();
    		case SerializedLocation:return obj.getSerializedLocation();
    		case UTI:				return obj.getUTI();
    		default: 				return getProperty((IResponseDataBase)obj, prop);
			case SamplingRates:	return CollectionUtils.makeIterable(obj.getSamplingRates());
			case SamplingUnits: return CollectionUtils.makeIterable(obj.getSamplingUnits());
			default: 			return getCollection((IResponseDataBase)obj, col);
		}
    }
 */
		Assert.assertNotNull(msg, fromDb);
		Assert.assertNotNull(msg, fromSvc);
		Assert.assertEquals(msg, fromDb.getURIString(), 		OData4JClientUtils.getStringProperty(fromSvc, 	"URI"));
		Assert.assertEquals(msg, fromDb.getUuid(), 				OData4JClientUtils.getStringProperty(fromSvc,	"UUID"));
		JUnitUtils.assertEquals(msg, fromDb.isIncomplete(), 	OData4JClientUtils.getBooleanProperty(fromSvc,	"IsIncomplete"));
		Assert.assertEquals(msg, fromDb.getSerializedLocation(),OData4JClientUtils.getStringProperty(fromSvc, 	"SerializedLocation"));
	//	Assert.assertEquals(msg, fromDb.getSerializedName(),	OData4JClientUtils.getStringProperty(fromSvc, 	"SerializedName"));
		
		Assert.assertEquals(msg, fromDb.getUnits(), 			OData4JClientUtils.getStringProperty(fromSvc, 	"Units"));
		Assert.assertEquals(msg, fromDb.getUTI(), 				OData4JClientUtils.getStringProperty(fromSvc, 	"UTI"));
		Assert.assertEquals(msg, fromDb.isIncomplete(), 		OData4JClientUtils.getBooleanProperty(fromSvc, 	"IsIncomplete") == Boolean.TRUE);
		JUnitUtils.assertEquals(msg, fromDb.getDataBytes(), 	OData4JClientUtils.getByteArrayProperty(fromSvc,"Data"));

		
		// Associated single objects
		Epoch 	epoch		= fromDb.getEpoch();
		OEntity svcEpoch 	= OData4JClientUtils.getSubEntity(svc, fromSvc, "Epoch");
		OvationJUnitUtils.assertEquals(msg + " - epoch", epoch, svcEpoch, svc, db);
			
		ExternalDevice 	dev		= fromDb.getExternalDevice();
		OEntity 		svcDev 	= OData4JClientUtils.getSubEntity(svc, fromSvc, "ExternalDevice");
		OvationJUnitUtils.assertEquals(msg + " - device", dev, svcDev, svc, db);

// how to use/test these?		
		NumericDataType dataType		= fromDb.getNumericDataType();
		NumericData 	data 			= fromDb.getData();
		
		Set<String> 	annoGroupTagSet = fromDb.getAnnotationGroupTagSet();
		IAnnotation[] 	annotations 	= fromDb.getAnnotations();
		Map<String,Object> devParams 	= fromDb.getDeviceParameters();
		String[] 		dimLabels 		= fromDb.getDimensionLabels();
		KeywordTag[] 	keywordTags 	= fromDb.getKeywordTags();
		long[] 			matlabShape 	= fromDb.getMatlabShape();
		Set<String> 	myAnnoGroupTags = fromDb.getMyAnnotationGroupTagSet();
		IAnnotation[] 	myAnnotations 	= fromDb.getMyAnnotations();
		KeywordTag[]  	myKeywordTags 	= fromDb.getMyKeywordTags();

		
	    /**
	static Enumerable<OEntity> 	getAllEntities(Entity type) 				{ return OData4JClientUtils.getAllEntities(_odataClient, type._setName); }
	static OEntity 				getSubEntity(OEntity root, String name) 	{ return OData4JClientUtils.getSubEntity(_odataClient, root, name); }
	static Enumerable<OEntity> 	getSubEntities(OEntity root, String name) 	{ return OData4JClientUtils.getSubEntities(_odataClient, root, name); }
	static <T> List<T> 			getAllFromDB(Class<T> type) 				{ return OvationUtils.getAllFromDB(_dbContext, type); }
	static <T> T 				getFromDB(String uri) 						{ return OvationUtils.getFromDB(_dbContext, uri); }
	
RESPONSE links - [
ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Owner,title=Owner,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/Owner], 
ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/NumericDataType,title=NumericDataType,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/NumericDataType], 
ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ExternalDevice,title=ExternalDevice,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/ExternalDevice], 
ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Epoch,title=Epoch,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/Epoch],
 
ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/SamplingUnits,title=SamplingUnits,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/SamplingUnits], 
ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ResourceNames,title=ResourceNames,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/ResourceNames], 
ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/SamplingRates,title=SamplingRates,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/SamplingRates], 
ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/FloatingPointData,title=FloatingPointData,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/FloatingPointData], 
ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyTags,title=MyTags,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/MyTags], 
ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/FloatData,title=FloatData,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/FloatData], 
ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/DeviceParameters,title=DeviceParameters,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/DeviceParameters], 
ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotationGroupTags,title=MyAnnotationGroupTags,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/MyAnnotationGroupTags], 
ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Properties,title=Properties,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/Properties], 
ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Tags,title=Tags,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/Tags], 
ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotations,title=MyAnnotations,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/MyAnnotations], 
ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Shape,title=Shape,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/Shape], 
ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyProperties,title=MyProperties,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/MyProperties], 
ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyKeywordTags,title=MyKeywordTags,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/MyKeywordTags], 
ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MatlabShape,title=MatlabShape,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/MatlabShape], 
ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/IntData,title=IntData,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/IntData], 
ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AnnotationGroupTags,title=AnnotationGroupTags,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/AnnotationGroupTags], 
ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/IntegerData,title=IntegerData,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/IntegerData], 
ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/DoubleData,title=DoubleData,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/DoubleData], 
ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/KeywordTags,title=KeywordTags,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/KeywordTags], 
ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/DimensionLabels,title=DimensionLabels,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/DimensionLabels], 
ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Annotations,title=Annotations,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/Annotations], 
ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Resources,title=Resources,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/Resources]]

	     * *** EntityBase (MyProperties:<String,Object>[], Owner:User, Properties:<String,Object[]>[], ResourceNames:String[], Resources:Resource[], URI:String, UUID:String, IsComplete:bool)
	     * **** KeywordTag (Tag:String, Tagged:TaggableEntityBase[])
	     * **** TaggableEntityBase (KeywordTags:KeywordTag[], MyKeywordTags:KeywordTag[], MyTags:String[], Tags:String[]) 
	     * ***** AnnotatableEntityBase (AnnotationGroupTags:String[], Annotations:IAnnotation[], MyAnnotationGroupTags:String[], MyAnnotations:IAnnotation[]) 
	     * ****** AnalysisRecord (AnalysisParameters:<String,Object>[], EntryFunctionName:String, Epochs:Epoch[], Name:String, Notes:String, Project:Project, ScmRevision:String, ScmURL:String, SerializedLocation:String)
	     * ****** ExternalDevice (Experiment:Experiment, Manufacturer:String, Name:String, SerializedLocation:String)
	     * ****** Source (AllEpochGroups:EpochGroup[], AllExperiments:Experiment[], ChildLeafDescendants:Source[], Children:Source[], EpochGroups:EpochGroup[], Experiments:Experiment[], Label:String, Parent:Source, ParentRoot:Source, SerializedLocation:String)
	     * ****** Resource (Data:byte[], Name:String, Notes:String, Uti:String)
	     * ******* URLResource (URL:String)
	     * ****** IOBase (DeviceParameters:<String,Object>[], DimensionLabels:String[], ExternalDevice:ExternalDevice, Units:String)
	     * ******* Stimulus (Epoch:Epoch, PluginID:String, SerializedLocation:String, StimulusParameters:<String,Object>[])
	     * ******* ResponseDataBase (Data:NumericData, DataBytes:byte[], DoubleData:double[], FloatData:float[], FloatingPointData:double[], IntData:int[], IntegerData:int[], MatlabShape:long[], NumericDataType:NumericDataType, Shape:long[])
	     * ******** Response (Epoch:Epoch, SamplingRates:double[], SamplingUnits:String[], SerializedLocation:String, UTI:String)
	     * 
	     * ******** DerivedResponse (DerivationParameters:<String,Object>[], Description:String, Epoch:Epoch, Name:String, SerializedLocation:String)
	     * ****** TimelineElement (EndTime:DateTime, StartTime:DateTime)
	     * ******* EpochGroup (ChildLeafDescendants:EpochGroup[], Children:EpochGroup[], EpochCount:int, Epochs:Epoch[], EpochsUnsorted:Epoch[], Experiment:Experiement, Label:String, Parent:EpochGroup, SerializedLocation:String, Source:Source)
	     * ******* Epoch (AnalysisRecords:AnalysisRecord[], DerivedResponses:DerivedResponse[], DerivedResponseNames:String[], Duration:double, EpochGroup:EpochGroup, ExcludeFromAnalysis:bool, MyDerivedResponseNames:String[], MyDerivedResponses:DerivedResponse[], NextEpoch:Epoch, PreviousEpoch:Epoch, ProtocolID:String, ProtocolParameters:<String,Object>[], Responses:Response[], ResponseNames:String[], SerializedLocation:String, StimuliNames:String[], Stimuli:Stimulus[])
	     * ******* PurposeAndNotesEntity[IOwnerNotes,IScientificPurpose] (Notes:String, Purpose:String)
	     * ******** Experiment (EpochGroups:EpochGroup[], Epochs:Epoch[], ExternalDevices:ExternalDevice[], Projects:Project[], SerializedLocation:String, Sources:Source[])
	     * ******** Project (AnalysisRecords:AnalysisRecord[], AnalysisRecordNames:String[], Experiments:Experiment[], MyAnalysisRecords:AnalysisRecord[], MyAnalysisRecordNames:String[], Name:String, SerializedLocation:String)
	     */
		
			
			long[] mshape = fromDb.getMatlabShape();
			NumericDataType ndtype = fromDb.getNumericDataType();
			long[] shape = fromDb.getShape();

			// Collections
			//	JUnitUtils.assertEquals(fromDb.getSamplingUnits(), 	OData4JClientUtils.getStringArrayProperty(entity, "SamplingUnits"));
			//	JUnitUtils.assertEquals(fromDb.getSamplingRates(), 	OData4JClientUtils.getDoubleArrayProperty(entity, "SamplingRates"));
				fromDb.getDeviceParameters();
			// TaggableEntityBase
				fromDb.getKeywordTags();
				fromDb.getMyKeywordTags();
				fromDb.getMyTags();
				fromDb.getTags();
			// EntityBase
				fromDb.getMyProperties();		// String,Object
				fromDb.getProperties();			// String,Object[]
				fromDb.getResourcesIterable();
			
		/*				
		links - [
		 
		ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/NumericDataType,title=NumericDataType,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/NumericDataType], 
		ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ExternalDevice,title=ExternalDevice,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/ExternalDevice], 
		ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Epoch,title=Epoch,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/Epoch], 
		ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/SamplingUnits,title=SamplingUnits,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/SamplingUnits], 
		ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ResourceNames,title=ResourceNames,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/ResourceNames], 
		ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/SamplingRates,title=SamplingRates,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/SamplingRates], 
		ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/FloatingPointData,title=FloatingPointData,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/FloatingPointData], 
		ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyTags,title=MyTags,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/MyTags], 
		ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/FloatData,title=FloatData,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/FloatData], 
		ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/DeviceParameters,title=DeviceParameters,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/DeviceParameters], 
		ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotationGroupTags,title=MyAnnotationGroupTags,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/MyAnnotationGroupTags], 
		ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Properties,title=Properties,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/Properties], 
		ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Tags,title=Tags,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/Tags], 
		ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotations,title=MyAnnotations,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/MyAnnotations], 
		ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Shape,title=Shape,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/Shape], 
		ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyProperties,title=MyProperties,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/MyProperties], 
		ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyKeywordTags,title=MyKeywordTags,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/MyKeywordTags],
		ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MatlabShape,title=MatlabShape,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/MatlabShape], 
		ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/IntData,title=IntData,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/IntData], 
		ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AnnotationGroupTags,title=AnnotationGroupTags,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/AnnotationGroupTags], 
		ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/IntegerData,title=IntegerData,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/IntegerData], 
		ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/DoubleData,title=DoubleData,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/DoubleData], 
		ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/KeywordTags,title=KeywordTags,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/KeywordTags], 
		ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/DimensionLabels,title=DimensionLabels,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/DimensionLabels], 
		ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Annotations,title=Annotations,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/Annotations], 
		ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Resources,title=Resources,href=Responses('4cc5f992-be59-4bc0-8956-d0ed59e2ff71')/Resources]]
		*/
		
		
		//	User owner2 = 
		//	fromDb.getData();
		//	fromDb.getDoubleData();
		//	fromDb.getFloatData();
		//	fromDb.getFloatingPointData();
		//	fromDb.getIntegerData();
	}

	public static void assertEquals(String msg, Source fromDb, OEntity fromSvc, ODataConsumer svc, DataContext db) {
		Assert.assertNotNull(msg, fromDb);
		Assert.assertNotNull(msg, fromSvc);
		Assert.assertEquals(msg, fromDb.getURIString(), 		OData4JClientUtils.getStringProperty(fromSvc, 	"URI"));
		Assert.assertEquals(msg, fromDb.getUuid(), 				OData4JClientUtils.getStringProperty(fromSvc, 	"UUID"));
		JUnitUtils.assertEquals(msg, fromDb.isIncomplete(), 	OData4JClientUtils.getBooleanProperty(fromSvc,	"IsIncomplete"));
		Assert.assertEquals(msg, fromDb.getSerializedLocation(),OData4JClientUtils.getStringProperty(fromSvc, 	"SerializedLocation"));
	//	Assert.assertEquals(msg, fromDb.getSerializedName(),	OData4JClientUtils.getStringProperty(fromSvc, 	"SerializedName"));
// SOURCE links - [ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Owner,title=Owner,href=Sources('c47bcd1c-e863-46ea-a674-ab21fe9a6348')/Owner], ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ParentRoot,title=ParentRoot,href=Sources('c47bcd1c-e863-46ea-a674-ab21fe9a6348')/ParentRoot], ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ParentSource,title=ParentSource,href=Sources('c47bcd1c-e863-46ea-a674-ab21fe9a6348')/ParentSource], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Experiments,title=Experiments,href=Sources('c47bcd1c-e863-46ea-a674-ab21fe9a6348')/Experiments], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ResourceNames,title=ResourceNames,href=Sources('c47bcd1c-e863-46ea-a674-ab21fe9a6348')/ResourceNames], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/EpochGroups,title=EpochGroups,href=Sources('c47bcd1c-e863-46ea-a674-ab21fe9a6348')/EpochGroups], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/SourceChildren,title=SourceChildren,href=Sources('c47bcd1c-e863-46ea-a674-ab21fe9a6348')/SourceChildren], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyTags,title=MyTags,href=Sources('c47bcd1c-e863-46ea-a674-ab21fe9a6348')/MyTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ChildLeafSourceDescendants,title=ChildLeafSourceDescendants,href=Sources('c47bcd1c-e863-46ea-a674-ab21fe9a6348')/ChildLeafSourceDescendants], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotationGroupTags,title=MyAnnotationGroupTags,href=Sources('c47bcd1c-e863-46ea-a674-ab21fe9a6348')/MyAnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Properties,title=Properties,href=Sources('c47bcd1c-e863-46ea-a674-ab21fe9a6348')/Properties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Tags,title=Tags,href=Sources('c47bcd1c-e863-46ea-a674-ab21fe9a6348')/Tags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotations,title=MyAnnotations,href=Sources('c47bcd1c-e863-46ea-a674-ab21fe9a6348')/MyAnnotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyProperties,title=MyProperties,href=Sources('c47bcd1c-e863-46ea-a674-ab21fe9a6348')/MyProperties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AllEpochGroups,title=AllEpochGroups,href=Sources('c47bcd1c-e863-46ea-a674-ab21fe9a6348')/AllEpochGroups], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyKeywordTags,title=MyKeywordTags,href=Sources('c47bcd1c-e863-46ea-a674-ab21fe9a6348')/MyKeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AllExperiments,title=AllExperiments,href=Sources('c47bcd1c-e863-46ea-a674-ab21fe9a6348')/AllExperiments], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AnnotationGroupTags,title=AnnotationGroupTags,href=Sources('c47bcd1c-e863-46ea-a674-ab21fe9a6348')/AnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Annotations,title=Annotations,href=Sources('c47bcd1c-e863-46ea-a674-ab21fe9a6348')/Annotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/KeywordTags,title=KeywordTags,href=Sources('c47bcd1c-e863-46ea-a674-ab21fe9a6348')/KeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Resources,title=Resources,href=Sources('c47bcd1c-e863-46ea-a674-ab21fe9a6348')/Resources]]		
		Assert.assertEquals(msg, fromDb.getLabel(), 			OData4JClientUtils.getStringProperty(fromSvc, 	"Label"));
/*
    // Source extends AnnotatableEntityBase
    protected static Object getProperty(Source obj, PropertyName prop) {
    	switch (prop) {
    		case Label:				return obj.getLabel();
    		case ParentSource: 		return obj.getParent();
    		case ParentRoot: 		return obj.getParentRoot();
    		case SerializedLocation:return obj.getSerializedLocation();
    		default: 				return getProperty((IAnnotatableEntityBase)obj, prop);
			case AllEpochGroups: 			return CollectionUtils.makeIterable(obj.getAllEpochGroups());
			case AllExperiments: 			return CollectionUtils.makeIterable(obj.getAllExperiments());
			case ChildLeafSourceDescendants:return CollectionUtils.makeIterable(obj.getChildLeafDescendants());
			case SourceChildren: 			return CollectionUtils.makeIterable(obj.getChildren());
			case EpochGroups: 				return CollectionUtils.makeIterable(obj.getEpochGroups());
			case Experiments: 				return CollectionUtils.makeIterable(obj.getExperiments());
			default: 						return getCollection((IAnnotatableEntityBase)obj, col);
		}
    }
 */
	}
	
	public static void assertEquals(String msg, Stimulus fromDb, OEntity fromSvc, ODataConsumer svc, DataContext db) {
/*
    // Stimulus extends IOBase 
    protected static Object getProperty(Stimulus obj, PropertyName prop) {
    	switch (prop) {
    		case Epoch:				return obj.getEpoch();
    		case PluginID:			return obj.getPluginID();
    		case SerializedLocation:return obj.getSerializedLocation();
    		default: 				return getProperty((IIOBase)obj, prop);
			case StimulusParameters: 	return Property.makeIterable(obj.getStimulusParameters());
			default: 					return getCollection((IIOBase)obj, col);
		}
    }
 */
		Assert.assertNotNull(msg, fromDb);
		Assert.assertNotNull(msg, fromSvc);
		Assert.assertEquals(msg, fromDb.getURIString(), 		OData4JClientUtils.getStringProperty(fromSvc, 	"URI"));
		Assert.assertEquals(msg, fromDb.getUuid(), 				OData4JClientUtils.getStringProperty(fromSvc, 	"UUID"));
		JUnitUtils.assertEquals(msg, fromDb.isIncomplete(), 	OData4JClientUtils.getBooleanProperty(fromSvc,	"IsIncomplete"));
		Assert.assertEquals(msg, fromDb.getSerializedLocation(),OData4JClientUtils.getStringProperty(fromSvc, 	"SerializedLocation"));
	//	Assert.assertEquals(msg, fromDb.getSerializedName(),	OData4JClientUtils.getStringProperty(fromSvc, 	"SerializedName"));
// STIMULUS links - [ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Owner,title=Owner,href=Stimuli('4685068c-0a84-4012-8fbf-0b5921eb4650')/Owner], ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ExternalDevice,title=ExternalDevice,href=Stimuli('4685068c-0a84-4012-8fbf-0b5921eb4650')/ExternalDevice], ORelatedEntityLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Epoch,title=Epoch,href=Stimuli('4685068c-0a84-4012-8fbf-0b5921eb4650')/Epoch], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/ResourceNames,title=ResourceNames,href=Stimuli('4685068c-0a84-4012-8fbf-0b5921eb4650')/ResourceNames], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/DeviceParameters,title=DeviceParameters,href=Stimuli('4685068c-0a84-4012-8fbf-0b5921eb4650')/DeviceParameters], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyTags,title=MyTags,href=Stimuli('4685068c-0a84-4012-8fbf-0b5921eb4650')/MyTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotationGroupTags,title=MyAnnotationGroupTags,href=Stimuli('4685068c-0a84-4012-8fbf-0b5921eb4650')/MyAnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Properties,title=Properties,href=Stimuli('4685068c-0a84-4012-8fbf-0b5921eb4650')/Properties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyAnnotations,title=MyAnnotations,href=Stimuli('4685068c-0a84-4012-8fbf-0b5921eb4650')/MyAnnotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Tags,title=Tags,href=Stimuli('4685068c-0a84-4012-8fbf-0b5921eb4650')/Tags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyProperties,title=MyProperties,href=Stimuli('4685068c-0a84-4012-8fbf-0b5921eb4650')/MyProperties], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/MyKeywordTags,title=MyKeywordTags,href=Stimuli('4685068c-0a84-4012-8fbf-0b5921eb4650')/MyKeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/StimulusParameters,title=StimulusParameters,href=Stimuli('4685068c-0a84-4012-8fbf-0b5921eb4650')/StimulusParameters], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/AnnotationGroupTags,title=AnnotationGroupTags,href=Stimuli('4685068c-0a84-4012-8fbf-0b5921eb4650')/AnnotationGroupTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/DimensionLabels,title=DimensionLabels,href=Stimuli('4685068c-0a84-4012-8fbf-0b5921eb4650')/DimensionLabels], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Annotations,title=Annotations,href=Stimuli('4685068c-0a84-4012-8fbf-0b5921eb4650')/Annotations], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/KeywordTags,title=KeywordTags,href=Stimuli('4685068c-0a84-4012-8fbf-0b5921eb4650')/KeywordTags], ORelatedEntitiesLink[rel=http://schemas.microsoft.com/ado/2007/08/dataservices/related/Resources,title=Resources,href=Stimuli('4685068c-0a84-4012-8fbf-0b5921eb4650')/Resources]]		
		Assert.assertEquals(msg, fromDb.getUnits(), 			OData4JClientUtils.getStringProperty(fromSvc, 	"Units"));
		Assert.assertEquals(msg, fromDb.getPluginID(), 			OData4JClientUtils.getStringProperty(fromSvc, 	"PluginID"));
	}

	public static void assertEquals(String msg, URLResource fromDb, OEntity fromSvc, ODataConsumer svc, DataContext db) {
		Assert.assertNotNull(msg, fromDb);
		Assert.assertNotNull(msg, fromSvc);
		Assert.assertEquals(msg, fromDb.getURIString(), 		OData4JClientUtils.getStringProperty(fromSvc, 	"URI"));
		Assert.assertEquals(msg, fromDb.getUuid(), 				OData4JClientUtils.getStringProperty(fromSvc, 	"UUID"));
		JUnitUtils.assertEquals(msg, fromDb.isIncomplete(), 	OData4JClientUtils.getBooleanProperty(fromSvc,	"IsIncomplete"));
		Assert.assertEquals(msg, fromDb.getSerializedLocation(),OData4JClientUtils.getStringProperty(fromSvc, 	"SerializedLocation"));
	//	Assert.assertEquals(msg, fromDb.getSerializedName(),	OData4JClientUtils.getStringProperty(fromSvc, 	"SerializedName"));
		
		Assert.assertEquals(msg, fromDb.getNotes(), 			OData4JClientUtils.getStringProperty(fromSvc, 	"Notes"));
		Assert.assertEquals(msg, fromDb.getName(), 				OData4JClientUtils.getStringProperty(fromSvc, 	"Name"));
		Assert.assertEquals(msg, fromDb.getUti(), 				OData4JClientUtils.getStringProperty(fromSvc, 	"UTI"));
/*
    // URLResource extends Resource
    protected static Object getProperty(URLResource obj, PropertyName prop) {
    	switch (prop) {
    		case URL: return obj.getURLString();
    		default: return getProperty((Resource)obj, prop);
			default: return getCollection((Resource)obj, col);
		}
    }
 */
	}
	public static void assertEquals(String msg, User fromDb, OEntity fromSvc, ODataConsumer svc, DataContext db) {
		Assert.assertNotNull(msg, fromDb);
		Assert.assertNotNull(msg, fromSvc);
		Assert.assertEquals(msg, fromDb.getURIString(), 		OData4JClientUtils.getStringProperty(fromSvc, 	"URI"));
		Assert.assertEquals(msg, fromDb.getUuid(), 				OData4JClientUtils.getStringProperty(fromSvc, 	"UUID"));
		JUnitUtils.assertEquals(msg, fromDb.isIncomplete(), 	OData4JClientUtils.getBooleanProperty(fromSvc,	"IsIncomplete"));
	//	Assert.assertEquals(msg, fromDb.getSerializedLocation(),OData4JClientUtils.getStringProperty(fromSvc, 	"SerializedLocation"));	- EntityBase has this, but not IEntityBase (but AnalysisRecord does)
	//	Assert.assertEquals(msg, fromDb.getSerializedName(),	OData4JClientUtils.getStringProperty(fromSvc, 	"SerializedName"));		- EntityBase has this, but not IEntityBase

/*
    // User extends TaggableEntityBase
    protected static Object getProperty(User obj, PropertyName prop) {
    	switch (prop) {
	    	case Username:	return obj.getUsername();
	    	default: 		return getProperty((ITaggableEntityBase)obj, prop); 
	    	default: 					return getCollection((ITaggableEntityBase)obj, col); 
    	}
    }
 */
		User 			owner = fromDb.getOwner();
		
		
		Set<KeywordTag> tags = fromDb.getTagSet();
		
		/* User		
		"Owner" : 			{"uri" : "http://localhost:8080/ovodata/Ovodata.svc/Users('ron')/Owner"}, 
		"Tags" : 			{"uri" : "http://localhost:8080/ovodata/Ovodata.svc/Users('ron')/Tags"}, 
		"MyProperties" :  	{"uri" : "http://localhost:8080/ovodata/Ovodata.svc/Users('ron')/MyProperties"}, 
		"ResourceNames" : 	{"uri" : "http://localhost:8080/ovodata/Ovodata.svc/Users('ron')/ResourceNames"	
		"MyKeywordTags" : "uri" : "http://localhost:8080/ovodata/Ovodata.svc/Users('ron')/MyKeywordTags"
		"MyTags" : "uri" : "http://localhost:8080/ovodata/Ovodata.svc/Users('ron')/MyTags"
		"KeywordTags" : "uri" : "http://localhost:8080/ovodata/Ovodata.svc/Users('ron')/KeywordTags"
		"Resources" : "uri" : "http://localhost:8080/ovodata/Ovodata.svc/Users('ron')/Resources"
		"Properties" : "uri" : "http://localhost:8080/ovodata/Ovodata.svc/Users('ron')/Properties"
*/			
		
		
		Assert.assertEquals(msg, fromDb.getUsername(), 			OData4JClientUtils.getStringProperty(fromSvc, 	"Username"));
	}

	static <K,V> void assertEquals(String msg, Enumerable<OEntity> fromSvc, Map<K,V> fromDb) {
		Assert.assertNotNull(msg, fromDb);
		Assert.assertNotNull(msg, fromSvc);
		Assert.assertEquals(fromDb.size(), fromSvc.count());
		for (Iterator<OEntity> iter = fromSvc.iterator(); iter.hasNext(); ) {
			OEntity nameEntity = iter.next();
//			System.out.println(nameEntity);
		}

	}
	
	static <T> void assertEquals(String msg, Enumerable<OEntity> fromSvc, T... fromDb) {
		Assert.assertNotNull(msg, fromDb);
		Assert.assertNotNull(msg, fromSvc);
		Assert.assertEquals(fromDb.length, fromSvc.count());
		for (Iterator<OEntity> iter = fromSvc.iterator(); iter.hasNext(); ) {
			OEntity nameEntity = iter.next();
//			System.out.println(nameEntity);
		}

	}
	
}
