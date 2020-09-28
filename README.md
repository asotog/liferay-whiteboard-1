liferay-whiteboard
==================

Collaboration whiteboard app in real time for multiple users for Liferay 7.

## Configuration
There is a limit per websocket message sent to server, for example in Tomcat, `org.apache.tomcat.websocket.textBufferSize`  defaults to 8192 bytes, however portlet will display a message to the user if this limit is exceededing whatever defaults or configured. For whiteboard portlet this can be overwritten using a custom config from portal-ext.properties called
`com.rivetlogic.whiteboard.realtime.textBufferSize=16384`

## Docker local environment startup
### Prerequisites
- Java 8 +
- [Docker](https://docs.docker.com/docker-for-mac/install/)

### Startup
- Run `./gradlew createDockerContainer`, this creates container and build modules for deployment
- Run `docker container start CONTAINER_ID`
- Run `./gradlew logsDockerContainer`

### Other useful commands
- Run `./gradlew cleanDockerImage` to remove data, container and clean image
- Run `./gradlew tasks`  to list all the gradlew commands, listed tasks names might be different based on your wrapper version
- Run `./gradlew dockerDeploy` deploys project (modules, plugins, etc) to docker
- Run `docker container start CONTAINER_ID` to start previously created container
- Run `docker container stop CONTAINER_ID` to stop previously started container


### Logic
