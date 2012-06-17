package ovation.odata.test;

import java.util.HashMap;

import org.joda.time.DateTime;

import ovation.DataContext;
import ovation.Epoch;
import ovation.EpochGroup;
import ovation.Experiment;
import ovation.ExternalDevice;
import ovation.NumericData;
import ovation.Ovation;
import ovation.Project;
import ovation.Source;

public class PopulateTestDBData {
	public static final String USAGE =
			"ARGS: \n" +
			" --dbFile=<obj DB file>\n" +
			" --dbUser=<DB user name>\n" +
			" --dbPassword=<DB user's password>\n" +
			"[--projects=<number of projects to generate>]\n" +
			"[--experiments=<number of experiements per project>]\n" +
			"[--sources=<number of sources per experiement>]\n"  +
			"[--epochGroups=<number of epoch groups per source>]\n" +
			"[--epochs=<number of epochs per group>]";

	public static void main(String[] args) {
		String dbFile = null, dbUser = null, dbPassword = null;
		int numProjects = 1, expFactor = 1, srcFactor = 1, epochGroupFactor = 1, epochFactor = 1;
		if (args.length == 0) {
			System.out.println(USAGE);
			return;
		}
		for (String arg : args) {
			if (arg.startsWith("--dbFile=")) 		{ dbFile = arg.substring("--dbFile=".length()); continue; }
			if (arg.startsWith("--dbUser=")) 		{ dbUser = arg.substring("--dbUser=".length()); continue; }
			if (arg.startsWith("--dbPassword="))	{ dbPassword = arg.substring("--dbPassword=".length()); continue; }
			if (arg.startsWith("--projects=")) 	 	{ numProjects 		= Integer.parseInt(arg.substring("--projects=".length())); continue; }
			if (arg.startsWith("--experiments=")) 	{ expFactor 		= Integer.parseInt(arg.substring("--experiments=".length())); continue; }
			if (arg.startsWith("--sources=")) 		{ srcFactor 		= Integer.parseInt(arg.substring("--sources=".length())); continue; }
			if (arg.startsWith("--epochGroups="))	{ epochGroupFactor 	= Integer.parseInt(arg.substring("--epochGroups=".length())); continue; }
			if (arg.startsWith("--epochs=")) 		{ epochFactor 		= Integer.parseInt(arg.substring("--epochs=".length())); continue; }
			if (arg.equals("--help")) { System.out.println(USAGE); }
			System.err.println("Unrecognized arg '" + arg + "' - aborted.");
			return;
		}
		if (dbFile == null || dbUser == null || dbPassword == null) {
			if (dbFile == null) { System.err.println("missing required dbFile"); }
			if (dbUser == null) { System.err.println("missing required dbUser"); }
			if (dbPassword == null) { System.err.println("missing required dbPassword"); }
			return;
		}
		DataContext context = null;
		try {
			context = Ovation.connect(dbFile, dbUser, dbPassword);
			insertFixture(context, numProjects, expFactor, srcFactor, epochGroupFactor, epochFactor);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try { context.close(); } catch (Exception ignore) {}
		}
	}

	public static void insertFixture(DataContext context,
		int nProjects,
		int expFactor,
		int srcFactor,
		int epochGroupFactor,
		int epochFactor)
	{

		for(int proj = 0; proj < nProjects; proj++) {
			Project p = context.insertProject("Test", "OData Fixture", new DateTime());

			for(int ex = 0; ex < expFactor; ex++) {
				Experiment exp = p.insertExperiment("OData Fixture", new DateTime());

				ExternalDevice dev = exp.externalDevice("dev", "dev");

				double[] rawData = new double[10000];
				for (int j = 0; j < rawData.length; j++) {
					rawData[j] = Math.sin(j)/10000;
				}

				for (int c = 1; c <= srcFactor; c++) {
					System.out.println("Cell " + c + " of " + srcFactor + "...");

					Source cell = context.insertSource("Cell " + c);

					for (int g = 1; g <= epochGroupFactor; g++) {
						context.beginTransaction();
						try
						{
							System.out.println("    group " + g + " of " + epochGroupFactor + "...");

							EpochGroup group = exp.insertEpochGroup(cell, "Group " + g, new DateTime());

							for (int e = 1; e <= epochFactor; e++) {

								HashMap<String, Object> params = new HashMap<String, Object>();

								params.put("key1", 1.0);
								params.put("epochNumber", e);

								DateTime start = new DateTime(e);
								DateTime end = new DateTime(e+1);

								Epoch epoch = group.insertEpoch(start,
									new DateTime(end),
									"insertionStress",
									params);

								params = new HashMap<String, Object>();
								params.put("k1", "v1");
								params.put("k2", 3.2);

								String[] dimensionLabels = {};
								epoch.insertStimulus(dev,
									params,
									"insertionStress.stimulus",
									params,
									"units",
									dimensionLabels);	// FIXME

								NumericData data = new NumericData(rawData);
								epoch.insertResponse(dev,
									params,
									data,
									"V",
									"dimensionUnit",	// FIXME
									100,
									"Hz",
									"dataUti");			// FIXME

								epoch.addTag("hello");
								epoch.addTag("OData!");

							}
						} catch (Exception e) {
							context.abortTransaction();
						}
						// commit after abort?
						context.commitTransaction();
					}
				}
			}
		}
	}
}
