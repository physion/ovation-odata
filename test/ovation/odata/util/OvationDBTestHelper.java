package ovation.odata.util;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import ovation.DataContext;
import ovation.Epoch;
import ovation.EpochGroup;
import ovation.Experiment;
import ovation.ExternalDevice;
import ovation.NumericData;
import ovation.Ovation;
import ovation.OvationException;
import ovation.Project;
import ovation.Source;
import ovation.UserAuthenticationException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class OvationDBTestHelper {
	private static final Logger _log = Logger.getLogger(OvationDBTestHelper.class);

	/** caller's responsible for closing DataContext when done
	 * @throws Exception - if there's a user-auth or other Ovation failure (abstracted to just Exception to reduce import needs of client)
	 */
	public static DataContext getContext(String uid, String passwd) throws Exception {
		return getContext(uid, passwd, System.getProperty("dataContext.file", "/var/lib/ovation/db/dev.connection"));
	}
	public static DataContext getContext(String uid, String passwd, String dbConFile) throws OvationException, UserAuthenticationException {
		return Ovation.connect(dbConFile, uid, passwd);
	}

	public static class ProjectData {
		private static int _instance = 0;
		private String 						_name 		= "No Name Project " + ++_instance;
		private String 						_purpose 	= "No Purpose Project";
		private DateTime 					_startDate 	= new DateTime();
		private DateTime 					_endDate 	= null;
		private final List<ExperimentData> 	_experiments = Lists.newArrayList();

		public ProjectData name(String name) 		{ _name = name; return this; }
		public ProjectData purpose(String purp) 	{ _purpose = purp; return this; }
		public ProjectData start(DateTime start) 	{ _startDate = start; return this; }
		public ProjectData end(DateTime end) 		{ _endDate = end; return this; }
		public ProjectData add(ExperimentData exp)	{ _experiments.add(exp); return this; }
	}

	public static class ExperimentData {
		private static int _instance = 0;
		private String 						_purpose 	= "No Purpose Experiement " + ++_instance;
		private DateTime 					_startDate 	= new DateTime();
		private DateTime 					_endDate 	= null;
		private final List<SourceData> 		_sources 	= Lists.newArrayList();
		private final List<DeviceData> 		_devices	= Lists.newArrayList();

		public ExperimentData start(DateTime start) 	{ _startDate = start; return this; }
		public ExperimentData end(DateTime end) 		{ _endDate = end; return this; }
		public ExperimentData add(SourceData source)	{ _sources.add(source); return this; }
		public ExperimentData add(DeviceData dev)		{ _devices.add(dev); return this; }
	}

	public static class SourceData {
		private static int _nextId = 0;
		private String _label = "Cell " + ++_nextId;
		private final List<EpochGroupData> 	_epochGroups= Lists.newArrayList();

		public SourceData label(String label) 		{ _label = label; return this; }
		public SourceData add(EpochGroupData group)	{ _epochGroups.add(group); return this; }
	}

	public static class EpochGroupData {
		private static int _nextId = 0;
		private String 					_label 		= "EpochGroup " + ++_nextId;
		private DateTime				_startDate 	= new DateTime();
		private DateTime 				_endDate 	= null;
		private final List<EpochData>	_epochs 	= Lists.newArrayList();

		public EpochGroupData label(String label) 	{ _label = label; return this; }
		public EpochGroupData start(DateTime start) { _startDate = start; return this; }
		public EpochGroupData end(DateTime end) 	{ _endDate = end; return this; }
		public EpochGroupData add(EpochData epoch)	{ _epochs.add(epoch); return this; }
	}

	public static class DeviceData {
		private static int _instance;
		private String _name 			= "Device " + ++_instance;
		private String _manufacturer 	= "No Manufacturer";

		public DeviceData name(String name) 		{ _name = name; return this; }
		public DeviceData manufacturer(String name) { _manufacturer = name; return this; }
	}

	public static class EpochData {
		private class StimulusResponsePair {
			final StimulusData _stimulus;
			final ResponseData _response;
			private StimulusResponsePair(StimulusData stim, ResponseData resp) {
				_stimulus = stim;
				_response = resp;
			}
		}
		private DateTime					_startDate 	= new DateTime();
		private DateTime 					_endDate 	= new DateTime();
		private String						_protocolId	= "Protocol not specified";
		private final Map<String,Object> 	_params 	= Maps.newHashMap();
		private final Map<String,StimulusResponsePair> 	_devStimRespPairs	= Maps.newHashMap();
		private final List<String>			_tags		= Lists.newArrayList();

		public EpochData start(DateTime date)				{ if (date != null) _startDate = date; return this; }
		public EpochData end(DateTime date) 				{ if (date != null) _endDate = date; return this; }
		public EpochData protocolId(String id)				{ _protocolId = id; return this; }
		public EpochData param(String name, Object value) 	{ _params.put(name,  value); return this; }
		public EpochData tag(String tag) 					{ _tags.add(tag); return this; }
		public EpochData addPair(String dev, StimulusData stim, ResponseData resp) {
			_devStimRespPairs.put(dev, new StimulusResponsePair(stim, resp));
			return this;
		}
	}

	public static class StimulusData {
		private static int _instance = 0;
		private String 						_pluginId 		= "Stimulus Plugin " + ++_instance;
		private String						_units			= "unitless";
		private String[]					_dimLabels		= {"dimless"};
		private final Map<String, Object> 	_deviceParams 	= Maps.newHashMap();
		private final Map<String, Object> 	_params 		= Maps.newHashMap();

		public StimulusData pluginId(String id)				{ _pluginId = id; return this; }
		public StimulusData units(String units)				{ _units = units; return this; }
		public StimulusData devParam(String key, Object val){ _deviceParams.put(key,  val); return this; }
		public StimulusData param(String key, Object val)	{ _params.put(key,  val); return this; }
	}
	public static class ResponseData {
		private static final double[] _defaultTestData = new double[10000];
		static {
			for (int j = 0; j < _defaultTestData.length; j++) {
				_defaultTestData[j] = Math.sin(j)/10000;
			}
		}

		private double[]					_data 			= _defaultTestData;
		private String						_units			= "unitless";
		private double						_sampleRate		= 42.0;
		private String						_sampleRateUnits= "eons";
		private String						_dimLabel		= "dimless";
		private String						_dataUti		= "dUti";
		private final Map<String, Object> 	_deviceParams 	= Maps.newHashMap();

		public ResponseData data(double[] data, String units)		{ _data = data; _units = units; return this; }
		public ResponseData sampleRate(double rate, String units)	{ _sampleRate = rate; _sampleRateUnits = units; return this; }

		public ResponseData devParam(String key, Object val){ _deviceParams.put(key,  val); return this; }
	}

	/**
	 * adds the specified list of projects and all their associated data into the DataContext
	 * @param context
	 * @param projects
	 */
	public static void insertFixture(DataContext context, List<ProjectData> projects) {
		for (ProjectData proj : projects) {
			Project p = context.insertProject(proj._name, proj._purpose, proj._startDate, proj._endDate);
			for (ExperimentData ex : proj._experiments) {
				Experiment exp = p.insertExperiment(ex._purpose, ex._startDate, ex._endDate);

				List<ExternalDevice> devices = Lists.newArrayList();
				for (DeviceData dev : ex._devices) {
					devices.add(exp.externalDevice(dev._name, dev._manufacturer));
				}

				for (SourceData c : ex._sources) {
					Source cell = context.insertSource(c._label);
					for (EpochGroupData g : c._epochGroups) {
						context.beginTransaction();
						boolean transactionCommited = false;
						try {
							EpochGroup group = exp.insertEpochGroup(cell, g._label, g._startDate, g._endDate);
							for (EpochData e : g._epochs) {
								Epoch epoch = group.insertEpoch(e._startDate, e._endDate, e._protocolId, e._params);

								for (ExternalDevice dev : devices) {
									EpochData.StimulusResponsePair pair = e._devStimRespPairs.get(dev.getName());
									if (pair != null) {
										StimulusData stim = pair._stimulus;
										if (stim != null) {
											epoch.insertStimulus(dev, stim._deviceParams, stim._pluginId, stim._params, stim._units, stim._dimLabels);
										}
										ResponseData resp = pair._response;
										if (resp != null) {
											epoch.insertResponse(dev, resp._deviceParams, new NumericData(resp._data), resp._units, resp._dimLabel, resp._sampleRate, resp._sampleRateUnits, resp._dataUti);
										}
									}
								}
								for (String tag : e._tags) {
									epoch.addTag(tag);
								}
							}
							// only commit if nothing went wrong
							context.commitTransaction();
							transactionCommited = true;
						} finally {
							// make sure we either commit or roll back
							if (transactionCommited == false) {
								context.abortTransaction();
							}
						}
					}
				}
			}
		}
	}
}
