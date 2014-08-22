#!/bin/bash

PACKAGE_DIR=./package

echo "install JDK ..."
if ! command -v java > /dev/null; then
    sudo apt-get install -y default-jdk
fi

echo "install ant ..."
if ! command -v ant > /dev/null; then
    sudo apt-get install -y ant
fi

echo "install MYSQL ..."
if ! command -v mysql > /dev/null; then
    sudo apt-get install -y mysql-server
    echo "create database openacs" | mysql -uroot
fi

echo "install JBoss ..."
if [ ! -f "/opt/jboss/bin/run.jar" ]; then
    if ! command -v wget > /dev/null; then
        sudo apt-get install -y wget
    fi
    wget http://sourceforge.net/projects/jboss/files/JBoss/JBoss-4.2.3.GA/jboss-4.2.3.GA-jdk6.zip -O tmp.zip
    unzip tmp.zip > /dev/null
    sudo mkdir -p /opt/jboss
    sudo mv jboss-4.2.3.GA/* /opt/jboss
    rm -rf tmp.zip jboss-4.2.3.GA
fi

echo "install mysql connector java ..."
if [ ! -f "/usr/share/java/mysql-connector-java.jar" ]; then
    sudo apt-get install -y libmysql-java
fi
if [ ! -f "/opt/jboss/server/default/lib/mysql-connector-java.jar" ]; then
    sudo ln -sf /usr/share/java/mysql-connector-java.jar /opt/jboss/server/default/lib/
fi

echo "install openacs configure file ..."
if [ ! -f "/opt/jboss/server/default/deploy/openacs-ds.xml" ]; then
    cp $PACKAGE_DIR/users.properties $PACKAGE_DIR/roles.properties /opt/jboss/server/default/conf
    cp $PACKAGE_DIR/openacs-ds.xml /opt/jboss/server/default/deploy
fi

echo "================================================================================"
echo "check java version ..."
java -version

echo "check ant version ..."
ant -version

echo "================================================================================"
echo "web url: http://localhost:8080/openacs"
echo "user: admin"
echo "password: openacs"

