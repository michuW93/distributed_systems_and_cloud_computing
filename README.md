# distributed_systems_and_cloud_computing
following tutorial of Michael Pogrebinsky

an ephemeral Znode is deleted automaticall as soon as its creator process disconnects from Zookeeper. Using ephemeral znode we can detect that a process died or disconnected from the zookeeper service.

a persistent znode stays within zookeeper until it is explicity deleted. Using a persistent znode we can store date in between sessions.

1. add logs folder in apache-zookeeper main folder. Rename zoo_sample.cfg to zoo.cfg
2. Run zkServer.cmd
3. Run zkCli.cmd and write `create /election ""` then `ls /` and `get /election` to check if it was created.
4. after running application in logs should be: `Succesfully connected to Zookeeper`
5. At this point I am switching to linux so instead of zkServer.cmd, need to run `./zkServer.sh start`. Be careful with: https://stackoverflow.com/a/58712936/4952262
6. `mvn clean package` and `ava -jar distributed_systems_and_cloud_computing-1.0-SNAPSHOT-jar-with-dependencies.jar`
