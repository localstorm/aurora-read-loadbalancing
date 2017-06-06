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
        String url = System.getProperty("jdbc.url");
        verifyPropertiesForDatabaseSetup(url);
    }

    private static void verifyPropertiesForDatabaseSetup(String url) {
        if (url.equals("jdbc:mysql:aurora://")) {
            throw new IllegalArgumentException("No URL has been defined in the jdbc.properties file.");
        }
    }

}
