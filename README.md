# messaging-blue-green-cli

1 arg: queue
2 arg: deployment version
3 arg: route version

mvn compile exec:java -Dexec.mainClass="Producer" -Dexec.args="quote-storage.indexation v1 v1"
mvn compile exec:java -Dexec.mainClass="Consumer" -Dexec.args="quote-storage.indexation v1 v1"
