**hbase shell**

create 'wc','cf'

list

scan 'wc'

**run topology**

storm jar storm-hbase-example-1.0-SNAPSHOT.jar WordCountHbaseStormTopology hbase-example

storm kill hbase-example