#!/bin/bash


export MINISOCS=${PWD}

cd $MINISOCS/minisocs-frontend/adapter-driving-port-forrequestingminisocs-rest-jaxrs/
./run_integration_tests.sh
result=$?
if ! [ "$result" = 0 ]; then
    echo -e "\033[0;31mSome problems in the integration tests... Do not continue.\033[0m"
    exit 1
fi

cd $MINISOCS/anomaly-detection/adapter-driving-port-fordetecting-amqp-rabbitmq/
./run_integration_tests.sh
result=$?
if ! [ "$result" = 0 ]; then
    echo -e "\033[0;31mSome problems in the integration tests... Do not continue.\033[0m"
    exit 1
fi

exit 0