import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.util.Map;
import java.util.Random;

/**
 * Created by wangjj17 on 2019/2/22.
 */
public class DataSourceSpout extends BaseRichSpout {

    private SpoutOutputCollector collector;

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
    }

    public static final String[] words = new String[]{"apple", "orange", "pineapple", "bannaer"};

    @Override
    public void nextTuple() {
        Random random = new Random();
        String word = words[random.nextInt(words.length)];

        this.collector.emit(new Values(word));

        System.out.println("word:" + word);

        Utils.sleep(1000);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

        declarer.declare(new Fields("line")
        );
    }
}
