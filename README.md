# Demo for Temporal Framework

Demo project for ordering stuff with a simple Ordering Saga utilizing  [Temporal](https://temporal.io) system and [Temporal Java SDK](https://github.com/temporalio/sdk-java) to code for possible compensation in case ordering process fails mid-step.

* By [Maxym Mykhalchuk](https://blog.maxym.dp.ua), see [license](LICENSE)
* Uses parts of [Temporal Docker Cluster images](https://github.com/temporalio/docker-compose), see [license](TEMPORAL-DOCK-LICENSE)
* Built on Spring Boot 3 / Spring Framework 6
* REST microservices (no CQRS/Event-Sourcing)
* Uses MongoDB as storage

### To run
* Start Temporal server locally, see [Temporal Guide](https://docs.temporal.io/application-development/foundations)
* Start MongoDB locally on port 27017 with no password
* All above can be done using `docker-compose up -d`
* Then start all the services `./gradlew bootRun --parallel --max-workers 4`
* You can monitor Saga progress by opening Temporal Web UI at http://127.0.0.1:8080
* Open the services
  * [Shipment Service](http://localhost:8082) -- TODO: it's not used in Saga yet
  * [Inventory Service](http://localhost:8084) -- TODO: it's not used in Saga yet
  * [Payment Service](http://localhost:8083) to see remaining user credit
  * Main [Order Service](http://localhost:8081) to create orders and employ the Saga that uses Temporal