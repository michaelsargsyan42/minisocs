#!/bin/bash

podman rm -f rabbitmq > /dev/null 2>&1
podman run -d --name rabbitmq -p 5672:5672 -p 15672:15672 docker.io/library/rabbitmq:4.1.4-management
for i in $(seq 1 60); do podman logs rabbitmq 2>&1 | grep -q "Server startup complete" && break; sleep 1; done

(cd ../../anomaly-detection/hexagon; ./start_derby_database.sh localhost 1621)
sleep 3

pkill -f MainAnomalyDetection
../../anomaly-detection/startup/start_anomalydetection.sh "amqp://localhost:5672"
for i in $(seq 1 30); do podman exec rabbitmq rabbitmqctl list_bindings -q 2>/dev/null | grep -q minisocs-frontend.anomaly-detection && break; sleep 1; done

mvn test-compile failsafe:integration-test failsafe:verify
result=$?

pkill -f MainAnomalyDetection
(cd ../../anomaly-detection/hexagon; ./stop_derby_database.sh localhost 1621)
podman stop rabbitmq
podman rm rabbitmq

if ! [ "$result" = 0 ]; then
    echo -e "\033[0;31mSome problems in the integration tests.\033[0m"
    exit 1
fi

exit 0
