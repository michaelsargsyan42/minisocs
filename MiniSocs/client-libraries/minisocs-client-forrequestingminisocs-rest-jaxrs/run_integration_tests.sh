#!/bin/bash

pkill -f MainMiniSocs
sleep 1
../../minisocs-frontend/startup/start_minisocs.sh http://localhost:8083/MiniSocs http://localhost:8084/MySseServer
for i in $(seq 1 30); do curl -s http://localhost:8083/MiniSocs/isready > /dev/null && break; sleep 1; done

mvn test-compile failsafe:integration-test failsafe:verify
result=$?

pkill -f MainMiniSocs

if ! [ "$result" = 0 ]; then
    echo -e "\033[0;31mSome problems in the integration tests.\033[0m"
    exit 1
fi

exit 0
