import java.io.IOException;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class LeaderElection implements Watcher {
    private static final String ZOOKEPER_ADDRESS = "localhost:2181";
    private static final int SESSION_TIMEOUT = 4000;
    private ZooKeeper zooKeeper;

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("LeaderElection");
        LeaderElection leaderElection = new LeaderElection();
        leaderElection.connectToZookeper();
        leaderElection.run();
        leaderElection.close();
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
