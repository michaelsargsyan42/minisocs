package anomalydetection.adapter.orm;

import static anomalydetection.common.Log.ANOMALY;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import anomalydetection.api.port.driven.forstoringincidents.api.ForStoringIncidentsAPI;
import anomalydetection.api.port.driven.forstoringincidents.model.AnomalyDetectionIncident;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.spi.PersistenceUnitTransactionType;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.TargetServer;



public class OrmIncidentsAdapter implements ForStoringIncidentsAPI {

    public static final int maximumlength = 255;
    private final EntityManagerFactory entity_factory;

    public OrmIncidentsAdapter(final String host, final int port, final String localPath, final String clientDriver,
                               final String persistenceUnitName) {
        entity_factory = Persistence.createEntityManagerFactory(persistenceUnitName,
                configureJPA(host, port, localPath, clientDriver));
    }

    private static Map<String, String> configureJPA(final String host, final int port, final String localPath,
                                                    final String clientDriver) {
        Map<String, String> properties = new HashMap<>();
        properties.put(PersistenceUnitProperties.TRANSACTION_TYPE,
                PersistenceUnitTransactionType.RESOURCE_LOCAL.name());
        properties.put(PersistenceUnitProperties.JDBC_DRIVER, clientDriver);
        properties.put(PersistenceUnitProperties.JDBC_URL,
                "jdbc:derby://" + host + ":" + port + localPath + ";create=true");
        properties.put(PersistenceUnitProperties.JDBC_USER, "test");
        properties.put(PersistenceUnitProperties.JDBC_PASSWORD, "test");
        properties.put(PersistenceUnitProperties.CONNECTION_POOL_MIN, "1");
        properties.put(PersistenceUnitProperties.LOGGING_LEVEL, "FINE");
        properties.put(PersistenceUnitProperties.LOGGING_PARAMETERS, "true");
        properties.put(PersistenceUnitProperties.TARGET_SERVER, TargetServer.None);
        properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.CREATE_ONLY);
        properties.put(PersistenceUnitProperties.DDL_GENERATION_MODE,
                PersistenceUnitProperties.DDL_DATABASE_GENERATION);
        properties.put(PersistenceUnitProperties.WEAVING, "static");
        return properties;
    }

    @Override
    public void storeIncident(final AnomalyDetectionIncident incident) {
        validate(incident);
        var em = entity_factory.createEntityManager();
        em.getTransaction().begin();
        em.persist(incident);
        em.getTransaction().commit();
        em.close();
        ANOMALY.info("{}", () -> "stored " + incident);
    }

    @Override
    public List<AnomalyDetectionIncident> getIncidents() {
        var em = entity_factory.createEntityManager();
        em.getTransaction().begin();
        var query = em.createQuery("select i from AnomalyDetectionIncident i", AnomalyDetectionIncident.class);
        var incidents = query.getResultList();
        em.getTransaction().commit();
        em.close();
        return incidents;
    }

    @Override
    public List<AnomalyDetectionIncident> getIncidentsOfUser(final String pseudo) {
        var em = entity_factory.createEntityManager();
        em.getTransaction().begin();
        var query = em.createQuery("SELECT i FROM AnomalyDetectionIncident i WHERE i.pseudo = :pseudo",
                AnomalyDetectionIncident.class);
        query.setParameter("pseudo", pseudo);
        var incidents = query.getResultList();
        em.getTransaction().commit();
        em.close();
        return incidents;
    }

    @Override
    public List<AnomalyDetectionIncident> getIncidentsInSocialNetwork(final String snName) {
        var em = entity_factory.createEntityManager();
        em.getTransaction().begin();
        var query = em.createQuery("SELECT i FROM AnomalyDetectionIncident i WHERE i.snname = :snname",
                AnomalyDetectionIncident.class);
        query.setParameter("snname", snName);
        var incidents = query.getResultList();
        em.getTransaction().commit();
        em.close();
        return incidents;
    }


    public void deleteAllIncidents() {
        var em = entity_factory.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("delete from AnomalyDetectionIncident").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }


    public void close() {
        entity_factory.close();
    }

    private static void validate(final AnomalyDetectionIncident incident) {
        if (incident == null) {
            throw new IllegalArgumentException("incident can't be null");
        }
        if (incident.getPseudo() == null || incident.getPseudo().isBlank()) {
            throw new IllegalArgumentException("pseudo can't be null or blank");
        }
        if (incident.getSnname() == null || incident.getSnname().isBlank()) {
            throw new IllegalArgumentException("name of the social network can't be null or blank");
        }
        if (incident.getInstant() == null || incident.getInstant().isBlank()) {
            throw new IllegalArgumentException("instant can't be null or blank");
        }
        if (incident.getType() == null || incident.getType().isBlank()) {
            throw new IllegalArgumentException("type can't be null or blank");
        }
        if (incident.getContent() == null) {
            throw new IllegalArgumentException("content can't be null");
        }
        for (var value : List.of(incident.getPseudo(), incident.getSnname(), incident.getInstant(),
                incident.getType(), incident.getContent())) {
            if (value.length() > maximumlength) {
                throw new IllegalArgumentException("value longer than " + maximumlength + " characters");
            }
        }
    }
}