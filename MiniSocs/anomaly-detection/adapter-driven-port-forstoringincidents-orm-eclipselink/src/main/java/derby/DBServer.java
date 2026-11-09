package derby;

import java.io.PrintWriter;
import java.net.InetAddress;

import static anomalydetection.common.Log.ANOMALY;

import org.apache.derby.drda.NetworkServerControl;

/**
 * This class is for running a Derby data base server.
 */
public class DBServer {
	/**
	 * the network server control.
	 */
	private NetworkServerControl server;

	/**
	 * constructs a server.
	 * 
	 * @param host the host.
	 * @param port the port.
	 */
	public DBServer(final String host, final int port) {
		try {
			server = new NetworkServerControl(InetAddress.getByName(host), port);
		} catch (Exception ex) {
			throw new RuntimeException(ex.getLocalizedMessage());
		}
	}

	/**
	 * starts the server.
	 */
	public void start() {
		try {
			server.start(new PrintWriter(System.out));
		} catch (Exception ex) {
			throw new RuntimeException(ex.getLocalizedMessage());
		}
	}

	/**
	 * sets the tracing: on or off.
	 * 
	 * @param onoff the new value.
	 */
	public void trace(boolean onoff) {
		try {
			server.trace(onoff);
		} catch (Exception ex) {
			ANOMALY.info("{}", () -> "pb when setting the tracing onoff: " + ex);
		}
	}

	/**
	 * pings the network data server.
	 * 
	 * @throws Exception in case of problem.
	 */
	public void ping() throws Exception {
		server.ping();
	}

	/**
	 * shutdowns the network data bas server.
	 * 
	 * @throws Exception in case of problem.
	 */
	public void shutdown() throws Exception {
		server.shutdown();
	}
}
