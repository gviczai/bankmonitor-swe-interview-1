## Bankmonitor REST+JPA demo refactored version

After starting the application use http://localhost:3000/swagger-ui/index.html to test its features.

Set and export the Spring Profiles environment variable before runnig the application (or set it in the run configuration in an IDE) so the in-memory h2 test database is used, when running the application locally for testing:

    SPRING_PROFILES_ACTIVE=memdb

> Tip: Run *mvn install* first, so the openapi maven plugin will generate the necessary API source files.