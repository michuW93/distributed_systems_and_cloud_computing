import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class LeaderElection implements Watcher {
    private static final String ZOOKEPER_ADDRESS = "localhost:2181";
    private static final int SESSION_TIMEOUT = 4000;
    private static final String ELECTION_NAMESPACE = "/election";
    private ZooKeeper zooKeeper;
    private String currentZnodeName;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        System.out.println("LeaderElection");
        LeaderElection leaderElection = new LeaderElection();
        leaderElection.volunteerForLeadership();
        leaderElection.electLeader();
        leaderElection.connectToZookeper();
        leaderElection.run();
        leaderElection.close();
    }

    public void volunteerForLeadership() throws InterruptedException, KeeperException {
        String znodePrefix = ELECTION_NAMESPACE + "/c_";
        String znodeFullPath = zooKeeper.create(znodePrefix, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("znode name: " + znodeFullPath);
        currentZnodeName = znodeFullPath.replace(ELECTION_NAMESPACE + "/", "");
    }

    public void electLeader() throws InterruptedException, KeeperException {
        List<String> children = zooKeeper.getChildren(ELECTION_NAMESPACE, false);
        Collections.sort(children);
        String smallestChild = children.get(0);

        if (smallestChild.equals(currentZnodeName)){
            System.out.println("I'm leader");
        }
        System.out.println(smallestChild + " is the leader, I am not");
    }

    public void connectToZookeper() throws IOException {
        this.zooKeeper = new ZooKeeper(ZOOKEPER_ADDRESS, SESSION_TIMEOUT, this);
    }

    public void run() throws InterruptedException {
        synchronized (zooKeeper){
            zooKeeper.wait();
        }
    }

    public void close() throws InterruptedException {
        zooKeeper.close();
        System.out.println("Disconected from Zookeeper");
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        switch (watchedEvent.getType()){
            case None:
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    System.out.println("Succesfully connected to Zookeeper");
                } else {
                    synchronized (zooKeeper){
                        System.out.println("Got disconected event");
                        zooKeeper.notifyAll();
                    }
                }
        }
    }
}
