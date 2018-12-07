Game server/client

Need to set Java home correctly. On Lab machines use
set JAVA_HOME="C:\Program Files\Java\jdk1.8.0_112"

To build: gradlew installDist
To start server: build\install\RockPaperScissors\bin\game-server
To start client: build\install\RockPaperScissors\bin\game-client <server ip>

Have to make gradle executable
Have to find IP of computer that's running the server. The other client computer must put the IP address after running the client command.