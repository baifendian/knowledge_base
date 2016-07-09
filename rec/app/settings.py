#!/usr/bin/env python
# -*- coding: utf-8 -*-
DEBUG = False

PARAM_IP = "ip"
PARAM_REF_PAGE = "ref_page"
PARAM_UUID = "uuid"
PARAM_AGENT = "user_agent"

CONF_DATA_CENTER_SERVERS = "M@InputService"
CONF_DATA_ICE_LOCATOR = "toutiao/Locator:tcp -h 172.24.2.43 -p 7893"
ICE_LOCATOR_CACHE_TIMEOUT = "1800"
REC_LOCATOR= "bfdcloud/Locator:tcp -h 192.168.50.16 -p 7893:tcp -h 192.168.50.17 -p 7893:tcp -h 192.168.50.18 -p 7893:tcp -h 192.168.50.19 -p 7893"

ZK_ADDR = "bjlg-zk1:2181,bjlg-zk2:2181,bjlg-zk3:2181,bjlg-zk4:2181,bjlg-zk5:2181"
TASK_QUEUE_HOST="bjlg-40p218-mterminal01"

SECRET="jleboroolRlxFnqqgoxlpavvhszxaxnmikiquu2zifm4yzatarvyijh@qkqussrz"

configmap = {'session_timeout_millis': 'mSessionMillis',
             'report_policy': 'mReportPolicy',
             'report_interval_millis': 'mInterval'}

configparams = {'mEnc':'4',
                'mRequestType':'1'}
MOBILE_ENCRYPT = 'mEnc'

FILTER_TIME = 50000000
