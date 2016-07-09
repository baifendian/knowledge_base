
/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.baifendian.hackthon;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import com.wandoulabs.jodis.BfdJodis;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPoolConfig;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by yangzheming on 16/7/7.
 */
public class WriteCodisBolt extends BaseBasicBolt implements Serializable {

    public static final Logger LOG = LoggerFactory.getLogger(WriteCodisBolt.class);
    private JedisPoolConfig config;
    private BfdJodis bfdjodis;
    private String gid,topic,ret;

    /*
    * initialize codis
    */
    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        config = new JedisPoolConfig();
        config.setMaxTotal(1000);
        config.setMaxIdle(1000);
        bfdjodis = new BfdJodis("172.24.2.35:2181,172.24.2.36:2181,172.24.2.37:2181", 3000, "/zk/codis/db_mini2/proxy",
                config, 3000, "usertopic");
        LOG.info("Init codis config success!");
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        String sentence = (String) tuple.getValue(0);
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(sentence);

            /*
            * add topic
            *
            * */
            if(jsonObject.containsKey("method") && "MAddTopic".equals(jsonObject.get("method").toString())){
                System.out.println("invoke MAddTopic method!");
                if(jsonObject.containsKey("topic") && jsonObject.containsKey("gid")){
                    topic = jsonObject.get("topic").toString();
                    gid = jsonObject.get("gid").toString();
                    bfdjodis.sadd(gid,topic);
                    ret = "MAddTopic success! gid : "+gid+" , topic : "+ topic;
                    collector.emit(new Values(ret));
                    LOG.info("After mAdd :"+bfdjodis.smembers(gid));

                }
            }

            /*
            * del topic
            *
            * */
            if(jsonObject.containsKey("method") && "MDelTopic".equals(jsonObject.get("method").toString())){
                System.out.println("invoke MDelTopic method!");
                if(jsonObject.containsKey("topic") && jsonObject.containsKey("gid")){
                    topic = jsonObject.get("topic").toString();
                    gid = jsonObject.get("gid").toString();
                    bfdjodis.srem(gid,topic);
                    ret = "MDelTopic success! gid : "+gid+" , topic : "+ topic;
                    collector.emit(new Values(ret));
                    LOG.info("After mRem :"+bfdjodis.smembers(gid));
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("excl_sentence"));
    }

}
