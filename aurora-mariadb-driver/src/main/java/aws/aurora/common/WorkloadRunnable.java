package aws.aurora.common;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class WorkloadRunnable implements Runnable {
    private final RWCounters rwCounters;
    private final DataSource dataSource;
    private final boolean    callSetReadOnly;

    public WorkloadRunnable(RWCounters rwCounters, DataSource dataSource, boolean callSetReadOnly) {
        this.rwCounters = rwCounters;
        this.dataSource = dataSource;
        this.callSetReadOnly = callSetReadOnly;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try (Connection conn = dataSource.getConnection()) {
                if (callSetReadOnly) {
                    conn.setReadOnly(true);
                }
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery("select @@aurora_server_id");
                rwCounters.increment(getServerId(resultSet));
                statement.close();

            } catch (SQLException e) {
                if (e.getCause() != null) {
                    if (e.getCause().getClass() == InterruptedException.class) {
                        return;
                    }
                }

                e.printStackTrace();
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                return;
            }
        }

        System.out.println("Thread " + Thread.currentThread().getId() + " has been interrupted");
    }

    private String getServerId(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            return resultSet.getString(1);
        }
        throw new SQLException("No results?");
    }
}
