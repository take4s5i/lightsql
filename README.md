# light-sql
light-sql is a sql client for JDBC.
output query result as json to stdout.

## Install & usage
This product uses `leiningen`.
the JDBC driver for your DBMS on classpath before execute.

``` sh
$ git clone 'https://github.com/take4s5i/lightsql.git'
$ cd lightsql
$ lein uberjar
$ cd target
$ java -jar lightsql-0.1.0-SNAPSHOT-standalone.jar "jdbc:oracle:thin:user/pass@localhost:1521:sid" path/to/query.sql param1 param2 ...
```

## SQL parameters
Parameter place holder is `?`.(equal to `PreparedStatement`)

``sql
select * from my_table where id = ?
```


## License

Copyright © 2014 @take4s5i

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
