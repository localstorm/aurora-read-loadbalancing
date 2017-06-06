package aws.aurora.c3p0;

import com.mchange.v2.c3p0.ConnectionCustomizer;

import java.sql.Connection;

public class ReadOnlyConnectionCustomizer implements ConnectionCustomizer {

    @Override
    public void onAcquire(Connection connection, String s) throws Exception {

    }

    @Override
    public void onDestroy(Connection connection, String s) throws Exception {

    }

    @Override
    public void onCheckOut(Connection connection, String s) throws Exception {
        connection.setReadOnly(true);
    }

    @Override
    public void onCheckIn(Connection connection, String s) throws Exception {

    }
}
