#!/bin/bash


DETECTION=../../anomaly-detection/startup
DERBY=../../anomaly-detection/hexagon

free_frontend_ports() {
    for port in 8083 8084; do
        procNumber="$(lsof -t -i tcp:$port)"
        if [ -n "$procNumber" ]; then
            echo "removing the application in proc $procNumber"
            kill -9 $procNumber
        fi
    done
}

stop_everything() {
    free_frontend_ports
    pkill -f MainAnomalyDetection
    (cd $DERBY; ./stop_derby_database.sh localhost 1621)
    podman rm -f rabbitmq > /dev/null 2>&1
}

podman rm -f rabbitmq > /dev/null 2>&1
podman run -d --name rabbitmq -p 5672:5672 -p 15672:15672 docker.io/library/rabbitmq:4.1.4-management
echo "waiting for RabbitMQ..."
ready=no
for i in $(seq 1 60); do
    if podman logs rabbitmq 2>&1 | grep -q "Server startup complete"; then
        ready=yes
        break
    fi
    sleep 1
done
if [ "$ready" != yes ]; then
    echo "RabbitMQ did not start within 60 seconds"
    podman rm -f rabbitmq > /dev/null 2>&1
    exit 1
fi
echo "RabbitMQ is ready"
sleep 2

(cd $DERBY; ./start_derby_database.sh localhost 1621)
sleep 3


pkill -f MainAnomalyDetection
(cd $DETECTION; ./start_anomalydetection.sh "amqp://localhost:5672")
echo "waiting for the Anomaly Detection microservice..."
for i in $(seq 1 30); do
    if podman exec rabbitmq rabbitmqctl list_bindings -q 2>/dev/null | grep -q "minisocs-frontend.anomaly-detection"; then
        echo "Anomaly Detection is consuming"
        break
    fi
    sleep 1
done

free_frontend_ports

mvn test-compile failsafe:integration-test failsafe:verify
result=$?

stop_everything

if ! [ "$result" = 0 ]; then
    echo -e "\033[0;31mSome problems in the integration tests.\033[0m"
    exit 1
fi

exit 0
