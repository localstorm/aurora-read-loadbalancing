package aws.aurora.common;

import org.springframework.util.StringUtils;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Context {

    public static void setup() {
        setJDBCProperties();
        setDatabase();
    }

    private static void setJDBCProperties() {
        setProperties("jdbc.properties");
    }

    private static void setProperties(String filename) {
        Properties props = new Properties();
        try (FileReader fr = new FileReader(filename)) {
            props.load(fr);

            for (Object key : props.keySet()) {
                System.setProperty(key.toString(), "" + props.get(key));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static void setDatabase() {
        String dbName = System.getProperty("jdbc.database");
        String url = System.getProperty("jdbc.url");
        verifyPropertiesForDatabaseSetup(dbName, url);

        try (Connection conn = DriverManager.getConnection(url, System.getProperty("jdbc.username"), System.getProperty("jdbc.password"))){
            conn.setCatalog(dbName);
            Statement statement = conn.createStatement();
            statement.executeQuery("CREATE TABLE IF NOT EXISTS entries" +
                    " (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    " time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");
            statement.close();
        } catch (SQLException e) {
            System.err.println("SQL exception: " + e.getErrorCode() + " | " + e.getMessage());
        }
    }

    private static void verifyPropertiesForDatabaseSetup(String dbName, String url) {
        if (StringUtils.isEmpty(dbName)) {
            throw new IllegalArgumentException("No database has been defined in the jdbc.properties file.");
        }

        if (url.equals("jdbc:mysql:aurora://")) {
            throw new IllegalArgumentException("No URL has been defined in the jdbc.properties file.");
        }
    }

}
