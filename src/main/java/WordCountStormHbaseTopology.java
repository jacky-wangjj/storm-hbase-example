import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.hbase.bolt.HBaseBolt;
import org.apache.storm.hbase.bolt.mapper.SimpleHBaseMapper;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangjj17 on 2019/2/22.
 */
public class WordCountStormHbaseTopology {

    private static String tableName = "wc";

    public static void main(String[] args) {
        try {
            Config config = new Config();
//            config.setDebug(true);
            //使用storm-hbase包中的api，直接使用storm-hbase中的HBaseBolt
            Map<String, Object> hbaseConf = new HashMap<String, Object>();
            hbaseConf.put("hbase.zookeeper.quorum", "10.110.181.39,10.110.181.40,10.110.181.41");
            hbaseConf.put("zookeeper.znode.parent", "/hbase-unsecure");
            config.put("hbase.conf", hbaseConf);

            SimpleHBaseMapper mapper = new SimpleHBaseMapper()
                    .withRowKeyField("word")
                    .withColumnFields(new Fields("word"))
                    .withCounterFields(new Fields("count"))
                    .withColumnFamily("cf");

            HBaseBolt hBaseBolt = new HBaseBolt(tableName, mapper)
                    .withConfigKey("hbase.conf");

            TopologyBuilder builder = new TopologyBuilder();
            builder.setSpout("DataSourceSpout", new DataSourceSpout());
            builder.setBolt("SplitBolt", new SplitBolt()).shuffleGrouping("DataSourceSpout");
            builder.setBolt("CountBolt", new CountBolt()).shuffleGrouping("SplitBolt");
            builder.setBolt("HBaseBolt", hBaseBolt).fieldsGrouping("CountBolt", new Fields("word"));

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
