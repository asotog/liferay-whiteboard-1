liferay-whiteboard
==================

Collaboration whiteboard app in real time for multiple users for Liferay 7.

## Application container configuration
On tomcat there is a limit on the max buffer per message allowed, default size is 8192 bytes, this is configurable with `org.apache.tomcat.websocket.textBufferSize` on web.xml, otherwise if message is exceeded a popup is shown.

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
