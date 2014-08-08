OpenACS
=======

Automatic configuration server (ACS) implementing CPE configuration protocol CWMP as specified in TR-069.

## Build

    cd openacs
    ant -f b.xml clean make

## Run

    cd openacs
    ant -f b.xml clean deploy
    /opt/jboss/bin/run.sh -c default -b 0.0.0.0

## Dependencies

- JDK
- Java application server JBoss
- Apache Ant
- MySQL
