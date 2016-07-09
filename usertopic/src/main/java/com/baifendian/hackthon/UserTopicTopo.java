
/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.baifendian.hackthon;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;
import storm.kafka.*;

/**
 * Created by yangzheming on 16/7/7.
 */
public class UserTopicTopo {

    public static void main(String[] args) throws Exception {

        TopologyBuilder builder = new TopologyBuilder();

        // storm-kafka config
        String zks = "172.24.5.45:2181";
        String zkpath = "/kafka08/brokers";
        String topic = "DS.Input.All.Gtest_hackthon";
        String zkroot = "/storm_data";
        String id = "2016-07-09";

        BrokerHosts brokerHosts = new ZkHosts(zks, zkpath);
        SpoutConfig spoutConf = new SpoutConfig(brokerHosts, topic, zkroot, id);
        spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());
        spoutConf.forceFromStart = true;

        // topology config
        builder.setSpout("kafkaSpout", new KafkaSpout(spoutConf),1);
        builder.setBolt("writeCodis", new WriteCodisBolt()).shuffleGrouping("kafkaSpout");
        builder.setBolt("print", new PrintBolt()).shuffleGrouping("writeCodis");

        Config conf = new Config();
        conf.setDebug(false);

        // cluster mode
        if (args != null && args.length > 0) {
            conf.setNumWorkers(3);
            StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
        }

        // local mode
        else {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("test", conf, builder.createTopology());
            Utils.sleep(100000);
            cluster.killTopology("test")   ;
            cluster.shutdown();
        }
    }
}
