#!/bin/bash

if [ $# -lt 1 ]; then
    echo "usage: $0 brokerURI"
    exit 1
fi

ROOTPATH="$(cd $(dirname "$0") && pwd)"

ARGS=$*

export PATHSEP=':'

CLASSPATH=${CLASSPATH}${PATHSEP}${ROOTPATH}/target/dependency/*${PATHSEP}${ROOTPATH}/target/classes

CLASS=anomalydetection.startup.MainAnomalyDetection

# Start the client
CMD="java --add-opens=java.base/java.lang=ALL-UNNAMED -cp ${CLASSPATH} ${CLASS} ${ARGS}"

# this script is launched by sourcing => & and export
$CMD &

exit 0
