#!/bin/bash

if [ $# -lt 2 ]; then
    echo "usage: $0 host portnumber"
    exit 1
fi

host=$1
port=$2

ROOTPATH="$(cd $(dirname "$0") && pwd)"

ARGS=$*

export PATHSEP=':'

CLASSPATH=${CLASSPATH}${PATHSEP}${ROOTPATH}/target/dependency/*${PATHSEP}${ROOTPATH}/target/classes

CLASS=org.apache.derby.drda.NetworkServerControl

# Start the Derby database server
java --add-opens=java.base/java.lang=ALL-UNNAMED -cp ${CLASSPATH} ${CLASS} start -h $1 -p $2 &
sleep 1

# Configure trace on
java --add-opens=java.base/java.lang=ALL-UNNAMED -cp ${CLASSPATH} ${CLASS} trace on -h $1 -p $2 &
sleep 1

exit 0
