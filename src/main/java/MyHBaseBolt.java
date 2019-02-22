import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

import java.io.IOException;
import java.util.Map;

/**
 * Created by wangjj17 on 2019/2/22.
 */
public class MyHBaseBolt extends BaseBasicBolt {
    private Connection connection;
    private Table table;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", "10.110.181.39,10.110.181.40,10.110.181.41");
//        config.set("hbase.zookeeper.property.clientPort", "2181");
//        config.set("hbase.master", "10.110.181.39:60000");
        config.set("zookeeper.znode.parent", "/hbase-unsecure");
//        config.set("hbase.rootdir", "hdfs://10.110.181.39:8020/apps/hbase/data");
//        config.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
//        config.set("fs.file.impl", "org.apache.hadoop.fs.LocalFileSystem");
        try {
            connection = ConnectionFactory.createConnection(config);
            //示例都是对同一个table进行操作，因此直接将Table对象的创建放在了prepare，在bolt执行过程中可以直接重用。
            table = connection.getTable(TableName.valueOf("wc"));
        } catch (IOException e) {
            //do something to handle exception
            e.printStackTrace();
        }
    }
    @Override
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        String word = tuple.getStringByField("word");
        Integer count = tuple.getIntegerByField("count");
        try {
            //以各个单词作为row key
            Put put = new Put(Bytes.toBytes(word));
            //将被计数的单词写入cf:words列
            put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("word"), Bytes.toBytes(word));
            //将单词的计数写入cf:counts列
            put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("count"), Bytes.toBytes(count));
            table.put(put);
        } catch (IOException e) {
            //do something to handle exception
            e.printStackTrace();
        }
    }
    @Override
    public void cleanup() {
        //关闭table
        try {
            if(table != null) table.close();
        } catch (Exception e){
            //do something to handle exception
        } finally {
            //在finally中关闭connection
            try {
                connection.close();
            } catch (IOException e) {
                //do something to handle exception
            }
        }
    }
    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        //示例中本bolt不向外发射数据，所以没有再做声明
    }
}
