import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangjj17 on 2019/2/22.
 */
public class CountBolt extends BaseRichBolt {

    private OutputCollector collector;
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    Map<String, Integer> map = new HashMap<String, Integer>();
    @Override
    public void execute(Tuple input) {
        String word = input.getStringByField("word");
        Integer count = map.get(word);
        if(count == null){
            count = 0;
        }
        count ++;
        map.put(word, count);

        this.collector.emit(new Values(word, map.get(word)));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word", "count"));
    }
}
