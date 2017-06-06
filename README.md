# aurora-read-loadbalancing

This is a sample code how to use MariaDB driver for read-traffic load balancing.

Make sure jdbc.properties are in a working directory.

The sample uses three popular connection pools: C3P0, Commons-DBCP, HikariCP.

For each connection pool we have implicit and explicit examples. 

Explicit example uses writable connection pool, but sets setReadOnly() for each connection in the application. 

Implicit example doesn't use setReadOnly() in the application, but configures connections to be read-only on conenction-pool side.

Applications are printing which instances of Aurora cluster they hit and how many times.

Applications are not altering any data, the select @@aurora_server_id value to find which server they are talking to.
