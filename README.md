# About

POC to manage external resources with testcontainers.

## Modules

- `dev-resource-server`: A web application that manages testcontainers lifecycle.
- `dev-resource-client`: A client that provides a `PropertySource`.
- `sample-app`: A sample Spring Boot application.

## Key points

- Server web application manages testcontainer lifecycle via http.
- Client provides a `PropertySource` that talks to the Server
- Integrate with Spring Boot/Framework by property source.

# Run

- Start the `DevResourceServerApplication` in `dev-resource-server`.
- Then, run the `SampleApplication` in `sample-app`.
- While the sample app tries to resolve `spring.datasource.[url|username|password]` properties, it makes a remote http call to the server which starts the testcontainer and receive its connection info, then use the started testcontainer database.
- The started testcontainer could be shutdown when server app is shutdown or send a DELETE http request to `http://localhost:8080/containers/postgres:14` (e.g. ` http DELETE :8080/containers/postgres:14`).

# Further items to consider

- Gradle/maven plugin integration to the developer's app development process.
  - CDS(Class Data Sharing)
  - Clean classpath
- Detect when to enable the feature
  - presence of the dependency, profile, property, etc.
