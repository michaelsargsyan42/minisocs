Here follows the list of tasks, organised by milestone.

In this list, please tick the box to explicitly indicate which tasks have been completed:
- [] for not completed,
- [X] for completed.

Milestone 1:
- [X] ▢ REST: Use cases “create a user”, “create a social network”, and
  “post a message”.
- [X] ◇ REST: The other use cases, which are implemented in the
  skeleton.
- [X] ▢ REST: A JUnit unitary test class that demonstrates a client
  calling the application's endpoints (use cases) via REST.

Milestone 2:
- [X] ▢ AMQP: Use cases “add an anomaly detection type” and “perform
  anomaly detection for a new message”.
- [X] ▢ AMQP: Forward an incident to the administrators.
- [X] ◇ AMQP: Integrate the MiniSocs Frontend microservice with the
  Anomaly Detection microservice.
- [X] ▢ AMPQ: Integration test exercising only the Anomaly Detector
  microservice (events consumption and production)
- [X] ◇ AMPQ: Integration test exercising the communication between the
  MiniSocs Frontend and the Anomaly Detection microservices.

Milestone 3:
- [X] ▢ SSE: Use case “add consumer for new messages” of the MiniSocs
  Frontend microservice.
- [X] ◇ SSE: Use case “add consumer for moderation requests” of the
  MiniSocs Frontend microservice.
- [X] ▢ SSE: A JUnit unitary test class that demonstrates a client
  receiving new messages via SSE.
- [X] ◇ SSE: A JUnit unitary test class that demonstrates a client
  receiving moderation requests via SSE.

Milestone 4:
- [X] ▢ ORM: Store the incidents in a database
- [X] ▢ ORM: A JUnit unitary test class that demonstrates the storage of incidents
- [X] ▢ ORM: The Anomaly Detection microservice with incident storage

Milestone 5:
- [X] ▢ P&A: MiniSocs Frontend microservice, driven port adapter for
  notifying (new messages...)
- [X] ▢ P&A: MiniSocs Frontend microservice, driven port adapter for
  detecting anomalies
- [X] ▢ P&A: MiniSocs Frontend microservice, driving port adapter for
  requesting the microservice
- [X] ▢ P&A: MiniSocs Frontend microservice, starting and configuring
- [X] ◇ P&A: Anomaly Detection microservice, driven port adapter for
  alerting the administrators
- [X] ◇ P&A: Anomaly Detection microservice, driven port adapter for
  storing incidents
- [X] ◇ P&A: Anomaly Detection microservice, driving port adapter for
  detecting anomalies
- [X] ◇ P&A: Anomaly Detection microservice, starting and configuring

Milestone 6:
- [X] ▢ Client library of the MiniSocs Frontend microservice
- [X] ▢ System test
- [X] ◇ Client library of the Anomaly Detection microservice