package anomalydetection.startup;

import org.apache.logging.log4j.Level;

import anomalydetection.AnomalyDetection;
import anomalydetection.adapter.amqp.ConsumeAnomalyDetectionAdapter;
import anomalydetection.adapter.amqp.ProduceIncidentAdapter;
import anomalydetection.adapter.orm.OrmIncidentsAdapter;
import anomalydetection.api.constants.ORMEclipseLink;
import anomalydetection.common.Log;

/**
 * This class contains the static main method to start up the anomaly detection
 * microservice.
 */
public final class MainAnomalyDetection {
	private MainAnomalyDetection() {
		// nop
	}

	/**
	 * the entry point. The Derby database server must already be running
	 * (start_derby_database.sh).
	 *
	 * @param args the arguments: the broker URI.
	 * @throws Exception in case of problem.
	 */
	public static void main(final String[] args) throws Exception {
		if (args.length != 1) {
			throw new IllegalArgumentException("missing arguments: broker URI");
		}
		if (args[0] == null || args[0].isEmpty()) {
			throw new IllegalArgumentException("broker URI cannot be null or empty");
		}
		var brokerURI = args[0];
		Log.setLevel(Log.AMQP, Level.INFO);
		Log.setLevel(Log.ANOMALY, Level.INFO);

		// driven adapters first, then the facade, then the driving adapter (Fig. 1)
		var produceAdapter = new ProduceIncidentAdapter(brokerURI);
		var ormAdapter = new OrmIncidentsAdapter("localhost", ORMEclipseLink.NETWORKSERVER_PORT,
				ORMEclipseLink.LOCAL_PATH, ORMEclipseLink.DERBY_CLIENT_DRIVER, ORMEclipseLink.PERSISTENCE_UNIT_NAME);
		var facade = new AnomalyDetection(produceAdapter, ormAdapter);
		var consumeAdapter = new ConsumeAnomalyDetectionAdapter(brokerURI, facade);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				consumeAdapter.close();
				produceAdapter.close();
				ormAdapter.close();
			} catch (Exception problem) {
				Log.AMQP.error("problem while shutting down: {}", problem.getMessage());
			}
		}));

		Log.ANOMALY.info("anomaly detection microservice started, broker at {}", brokerURI);
		Thread.currentThread().join();
	}
}