package com.baifendian.hackthon;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

/**
 * Created by yangzheming on 16/7/7.
 */
public class PrintBolt extends BaseBasicBolt {

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        String rec = tuple.getString(0);
        System.out.println("String recieved: " + rec);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // do nothing
    }

}
