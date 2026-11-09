package derby;

import static anomalydetection.common.Log.ANOMALY;

import java.sql.SQLException;

public class Util {

	private Util() {
		// nop
	}

	/**
	 * Prints details of an SQLException chain in the console. Details included are
	 * SQL state, error code, exception message.
	 * 
	 * <p>
	 * It unwraps the entire exception chain to unveil the real cause of the
	 * exception.
	 *
	 * 
	 * @param ex the SQLException from which to print details.
	 */
	public static void printSQLException(SQLException ex) {
		var exToPrint = ex;
		while (exToPrint != null) {
			final var e = exToPrint;
			ANOMALY.warn("{}", () -> "\n----- SQLException -----");
			ANOMALY.warn("{}", () -> "  SQL State:  " + e.getSQLState());
			ANOMALY.warn("{}", () -> "  Error Code: " + e.getErrorCode());
			ANOMALY.warn("{}", () -> "  Message:    " + e.getMessage());
			// for stack traces, refer to derby.log or uncomment this:
			// exToPrint.printStackTrace(System.err);
			exToPrint = exToPrint.getNextException();
		}
	}

}
