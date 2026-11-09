package anomalydetection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import anomalydetection.adapter.orm.OrmIncidentsAdapter;
import anomalydetection.api.constants.AnomalyType;
import anomalydetection.api.constants.ORMEclipseLink;
import anomalydetection.api.port.driven.forstoringincidents.model.AnomalyDetectionIncident;
import derby.DBServer;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;



class TestOrmIncidentsAdapter {
    private static final String HOST = "localhost";
    private static DBServer derbyServer;
    private static OrmIncidentsAdapter adapter;

    @BeforeAll
    static void setUp() {
        Awaitility.setDefaultPollDelay(50, TimeUnit.MILLISECONDS);
        Awaitility.setDefaultPollInterval(100, TimeUnit.MILLISECONDS);
        Awaitility.setDefaultTimeout(5, TimeUnit.SECONDS);
        System.out.println("Using JDBC driver: " + ORMEclipseLink.DERBY_CLIENT_DRIVER);
        derbyServer = new DBServer(HOST, ORMEclipseLink.NETWORKSERVER_PORT);
        derbyServer.start();
        Awaitility.await().until(() -> {
            try {
                derbyServer.ping();
            } catch (Exception ex) {
                return false;
            }
            return true;
        });
        System.out.println("Derby Network Server now running");
        adapter = new OrmIncidentsAdapter(HOST, ORMEclipseLink.NETWORKSERVER_PORT, ORMEclipseLink.LOCAL_PATH,
                ORMEclipseLink.DERBY_CLIENT_DRIVER, ORMEclipseLink.PERSISTENCE_UNIT_NAME);
        adapter.deleteAllIncidents();
    }

    @AfterAll
    static void tearDown() throws Exception {
        adapter.deleteAllIncidents();
        assertEquals(0, adapter.getIncidents().size());
        adapter.close();
        adapter = null;
        derbyServer.shutdown();
        derbyServer = null;
    }

    @Test
    void storeIncidents() {
        adapter.storeIncident(incident("mike", "csc5002", AnomalyType.INAPPROPRIATE_VOCABULARY, "unsuitable"));
        adapter.storeIncident(incident("mike", "csc5002", AnomalyType.LONG_MESSAGE, "word ".repeat(40)));
        adapter.storeIncident(incident("notMike", "other", AnomalyType.LONG_MESSAGE, "word ".repeat(40)));
        var incidents = adapter.getIncidents();
        assertEquals(3, incidents.size());
        assertEquals(3, incidents.stream().map(AnomalyDetectionIncident::getId).distinct().count());
        assertEquals(2, adapter.getIncidentsOfUser("mike").size());
        assertEquals(1, adapter.getIncidentsInSocialNetwork("other").size());
        var bobIncident = adapter.getIncidentsOfUser("notMike").getFirst();
        assertEquals("other", bobIncident.getSnname());
        assertEquals(AnomalyType.LONG_MESSAGE.name(), bobIncident.getType());
        adapter.deleteAllIncidents();
        assertEquals(0, adapter.getIncidents().size());
    }

    @Test
    void storeIncompleteIncident() {
        var size = adapter.getIncidents().size();
        assertThrows(IllegalArgumentException.class, () -> adapter.storeIncident(new AnomalyDetectionIncident()));
        assertEquals(size, adapter.getIncidents().size());
    }

    @Test
    void storeTooLongContent() {
        var size = adapter.getIncidents().size();
        var content = "x".repeat(OrmIncidentsAdapter.maximumlength + 1);
        assertThrows(IllegalArgumentException.class,
                () -> adapter.storeIncident(incident("mike", "csc5002", AnomalyType.LONG_MESSAGE, content)));
        assertEquals(size, adapter.getIncidents().size());
    }

    private static AnomalyDetectionIncident incident(final String pseudo, final String snName, final AnomalyType type,
                                                     final String content) {
        var incident = new AnomalyDetectionIncident();
        incident.setPseudo(pseudo);
        incident.setSnname(snName);
        incident.setInstant(Instant.now().toString());
        incident.setType(type.name());
        incident.setContent(content);
        return incident;
    }
}