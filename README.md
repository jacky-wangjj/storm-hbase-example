**hbase shell**

创建表：
create 'wc','cf'

查看所有表：
list

查看'wc'表中的所有记录：
scan 'wc'

**run topology**

自己实现HBaseBolt：
storm jar storm-hbase-example-1.0-SNAPSHOT.jar WordCountHbaseStormTopology hbase-example

使用storm-hbase包中的HBaseBolt：
storm jar storm-hbase-example-1.0-SNAPSHOT.jar WordCountStormHbaseTopology hbase-example

干掉集群中提交的topology：
storm kill hbase-example