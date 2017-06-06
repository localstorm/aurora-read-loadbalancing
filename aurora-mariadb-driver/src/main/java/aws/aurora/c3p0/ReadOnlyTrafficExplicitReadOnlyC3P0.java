package aws.aurora.c3p0;

import aws.aurora.common.Context;
import aws.aurora.common.WorkloadRunnable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import aws.aurora.common.RWCounters;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class ReadOnlyTrafficExplicitReadOnlyC3P0 {

    public static void main(String[] args) throws Exception {
        Context.setup();

        ApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");
        DataSource readOnly = (DataSource) context.getBean("c3p0RWDataSource");

        RWCounters rwCounters = new RWCounters();

        List<Thread> threads = new ArrayList<Thread>();
        for (int i=0; i < 100; i++) {
            /**
             * Here we explicitly set ReadOnly (see false param), so even if we use non-read-only pool
             * requests are routed to readers
             */

            Thread t = new Thread(new WorkloadRunnable(rwCounters, readOnly, true));
            t.start();
            threads.add(t);
        }

        while(System.in.available() == 0) {
            Thread.sleep(1000);
            rwCounters.printStats();
        }

        System.out.println("Shutting down the workload...");
        for (Thread t: threads) {
            if (t.isAlive()) {
                t.interrupt();
            }
        }

        for (Thread t: threads) {
            if (t.isAlive()) {
                t.join();
            }
        }

        System.out.println("Done...");
    }

}
