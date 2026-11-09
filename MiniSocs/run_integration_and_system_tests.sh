#!/bin/bash

export MINISOCS=${PWD}

for dir in minisocs-frontend/adapter-driving-port-forrequestingminisocs-rest-jaxrs \
           anomaly-detection/adapter-driving-port-fordetecting-amqp-rabbitmq \
           client-libraries/anomaly-detection-client-fordetecting-amqp-rabbitmq \
           client-libraries/minisocs-client-forrequestingminisocs-rest-jaxrs; do
    cd $MINISOCS/$dir
    ./run_integration_tests.sh
    if ! [ "$?" = 0 ]; then
        echo -e "\033[0;31mSome problems in the integration tests of $dir... Do not continue.\033[0m"
        exit 1
    fi
done

cd $MINISOCS/system-test
./run_system_tests.sh
if ! [ "$?" = 0 ]; then
    echo -e "\033[0;31mSome problems in the system tests.\033[0m"
    exit 1
fi

exit 0
