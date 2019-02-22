import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;

/**
 * Created by wangjj17 on 2019/2/22.
 */
public class WordCountHbaseStormTopology {

    public static void main(String[] args) {
        try {
            Config config = new Config();
//            config.setDebug(true);

            TopologyBuilder builder = new TopologyBuilder();
            builder.setSpout("DataSourceSpout", new DataSourceSpout());
            builder.setBolt("SplitBolt", new SplitBolt()).shuffleGrouping("DataSourceSpout");
            builder.setBolt("CountBolt", new CountBolt()).shuffleGrouping("SplitBolt");
            builder.setBolt("MyHBaseBolt", new MyHBaseBolt()).shuffleGrouping("CountBolt");

            if (args != null && args.length > 0) {
                config.setNumWorkers(2);
                StormSubmitter.submitTopology(args[0], config, builder.createTopology());
            } else {
                LocalCluster cluster = new LocalCluster();
                cluster.submitTopology("LocalWordCountStormTopology", config, builder.createTopology());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
