package persistence;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Loads database settings from environment variables or persistence.properties.
 */
public final class PersistenceConfig {

    private static final String PU_NAME = "uno-pu";
    private static EntityManagerFactory entityManagerFactory;

    private PersistenceConfig() {}

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            entityManagerFactory = createEntityManagerFactory(defaultSettings());
        }
        return entityManagerFactory;
    }

    public static synchronized void resetForTests(String jdbcUrl) {
        close();
        Map<String, String> settings = defaultSettings();
        settings.put("jakarta.persistence.jdbc.url", jdbcUrl);
        entityManagerFactory = createEntityManagerFactory(settings);
    }

    public static synchronized void close() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
    }

    static Map<String, String> defaultSettings() {
        Properties props = loadProperties();
        Map<String, String> settings = new HashMap<>();
        settings.put("jakarta.persistence.jdbc.url", resolveSetting("UNO_DB_URL", props, "db.url", "jdbc:h2:file:./data/uno"));
        settings.put("jakarta.persistence.jdbc.user", resolveSetting("UNO_DB_USER", props, "db.user", "sa"));
        settings.put("jakarta.persistence.jdbc.password", resolveSetting("UNO_DB_PASSWORD", props, "db.password", ""));
        return settings;
    }

    private static EntityManagerFactory createEntityManagerFactory(Map<String, String> settings) {
        Map<String, Object> overrides = new HashMap<>(settings);
        return Persistence.createEntityManagerFactory(PU_NAME, overrides);
    }

    private static String resolveSetting(String envName, Properties props, String propKey, String defaultValue) {
        String fromEnv = System.getenv(envName);
        if (fromEnv != null && !fromEnv.isEmpty()) {
            return fromEnv;
        }
        return props.getProperty(propKey, defaultValue);
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = PersistenceConfig.class.getResourceAsStream("/persistence.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException ignored) {
        }
        return props;
    }
}
