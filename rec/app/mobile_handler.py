# -*- coding: utf-8 -*-
import copy
import json
import logging,os,sys
import time
import traceback
import simplejson
import settings
import UserHistory_pb2
import urllib
from urllib2 import *
import gzip
import math
reload(sys) 
sys.setdefaultencoding('utf-8') 

cpath = os.getcwd()
logpath = cpath+"/../log/mobile_handle.log"
logging.basicConfig(filename=logpath, level=logging.DEBUG, format='[%(asctime)s %(levelname)s %(process)d %(filename)s %(lineno)d] - %(message)s')
logger = logging.getLogger()
import BfdCodis as codis
topicclient = codis.BfdCodis("172.24.2.35:2181,172.24.2.36:2181,172.24.2.37:2181", "/zk/codis/db_mini2/proxy", "usertopic")
hisclient = codis.BfdCodis("172.24.2.35:2181,172.24.2.36:2181,172.24.2.37:2181", "/zk/codis/db_mini2/proxy", "useraction")
hotclient = codis.BfdCodis("172.24.2.35:2181,172.24.2.36:2181,172.24.2.37:2181", "/zk/codis/db_mini2/proxy", "hackthon_item")
def config_handler(params):
    header = [("Content-Type", "application/json"),]
    dict = {}
    dict["mSessionMillis"] = "30000"
    dict["mReportPolicy"] = "by_interval"
    dict["mInterval"] = "30000"
    dict["mRequestType"] =  "1"
    dict["cached_request_limit"] = "2000"
    dict["arp_limit"] = "0"
    dict["gid"] = "98e7f51fb7da"
    dict["installed_app_limit"] = 20
    dict["mEnc"] = 4
    str = json.dumps(dict)
    result = '[0, "OK",' + str + ']'
    #result = [0, "OK", '{"mSessionMillis": "30000","mReportPolicy": "by_interval","mInterval": "30000","mRequestType": "1","cached_request_limit": "2000","arp_limit": "0","gid": "98e7f51fb7da","installed_app_limit": 20,"mEnc": "4"}']
    status = '200 OK'
    return status, header,result



def multi_handler(env, params, mDebug):
    header = [("Content-Type", "application/json"),]
    status = '200 OK'
    info = dict(params)
    ret_val = '[0, "OK"]'
    return status, header, ret_val


def rec_handler(env, params, mDebug):
    header = [("Content-Type", "application/json"),]
    num = int(params.get('num', "1"))
    gid = params.get('gid',None)
    status = '200 OK'
    if not gid:
        ret_val = '[2, "no gid"]'
        return status, header, json.dumps(ret_val)
    fmt = json.loads(params.get('fmt','{"iid":"$iid"}'))
    query = ""
    docs = []
    topic = gettopic(gid,params)
    c_iid = params.get('iid',None)
    if c_iid:
        logger.debug(c_iid)
        query = "iid:" + c_iid
        qstr = "http://172.24.2.64:9000/solr/hackthon_importnew/select?q=" + query + "&wt=json&indent=true"
        logger.debug(qstr)
        docs = getsolr(qstr)
        if len(docs) < 1:
            ret_val = '[1, "no response"]'
            return status, header, json.dumps(ret_val)
    else:
        logger.debug("no iid")
        #时间过滤
        filter_time = filtertime()
        logger.debug("filter_time:%s"%str(filter_time))
        query = "ptime:[" + str(filter_time) + " TO *]"
        #历史过滤
        siids = filterhistory(gid)
        for iid in siids:
            if len(query)<1:
                query = "!iid:" + iid
            else:
                query = query + " or !iid:" + iid
              
        if len(topic) >= 0:
            cnum = int(math.floor(num/len(topic)))
            for tp in topic:
                cquery = ""
                if len(query)<1 :
                    cquery = "topic:" + tp
                else:
                    cquery = query + " and topic:" + tp
                cquery = urllib.quote(str(cquery)) 
                qstr = "http://172.24.2.64:9000/solr/hackthon_importnew/select?q=" + cquery + "&rows=" + str(cnum) + "&wt=json&indent=true"
                doc = getsolr(qstr)
                for dd in doc:
                    docs.append(dd)
        if len(query) <1:
            query = "*:*"
        query = urllib.quote(str(query))                           
        qstr = "http://172.24.2.64:9000/solr/hackthon_importnew/select?q=" + query + "&rows=" + str(num) + "&wt=json&indent=true"
        logger.debug(qstr)
        doc = getsolr(qstr)
        for dd in doc:
            docs.append(dd)
            
        
    ciids = set()
    items = {}
    ret = []
    for doc in docs:
        iid = doc.get("iid")
        if iid not in ciids:
            ciids.add(iid)
            if c_iid:
                list = format(doc,fmt)
                #items[iid] = list
                ret.append(list)
            else:
                list = formatlist(doc,fmt)
                items[iid] = list
                #ret.append(list)
        if len(ciids) >= num:
            break
    if not c_iid:
        ret = sortbyhot(items)
        qstr = "http://172.24.2.64:9000/solr/hackthon_importnew/select?q=cid%3Ahackthon_toutiao&rows=1&wt=json&indent=true"
        docs = getsolr(qstr)
        for doc in docs:
            list = formatlist(doc,fmt)
            ret.append(list)
        qstr = "http://172.24.2.64:9000/solr/hackthon_importnew/select?q=cid%3Ahackthon_runoob&rows=1&wt=json&indent=true"
        docs = getsolr(qstr)
        for doc in docs:
            list = formatlist(doc,fmt)
            ret.append(list)
    status = '200 OK'
    ret_val = []
    ret_val.append(0)
    ret_val.append("ok")
    ret_val.append("testuuid")
    ret_val.append(ret)
    return status, header, json.dumps(ret_val)

def filtertime():
    filter_time = 0
    try:
        now = int(time.time())#时间过滤
        time_before = settings.FILTER_TIME
        filter_time = int(now-time_before)
        return filter_time
    except: 
        return 0

def gettopic(gid,params):
    topic = []
    try:
        if params.get('topic',None):
            logger.debug(params.get('topic',None))
            topic.append(params.get('topic',None))#如果有主题取当前主题
        else:
            vtopic = topicclient.smembers(gid)#否则取codis中的用户主题
            for top in vtopic:
                topic.append(top)
        return topic
    except:
        logger.error('get topic data error: %s' % traceback.format_exc())
        return topic

def filterhistory(gid):
    key = "G:UserAction:" + gid + ":Chackthon:MVisit"
    iids = []
    try:
        value = hisclient.lrange(key,0,-1)
        for v in value:
            tmp = UserHistory_pb2.ItemTimePair()
            tmp.ParseFromString(v)
            logger.debug(tmp.iid)
            iids.append(tmp.iid)
        key = "G:UserAction:" + gid + ":Chackthon:MFeedBack"
        value = hisclient.lrange(key,0,-1)
        feed_dict = {}
        for v in value:
            tmp = UserHistory_pb2.ItemTimePair()
            tmp.ParseFromString(v)
            if tmp.iid not in feed_dict:
                feed_dict[tmp.iid] = 1
            else:
                feed_dict[tmp.iid] +=1
        for iid,v in feed_dict.items():
            if v > 2:
                iids.append(iid)
        result = set(iids)
        return result
    except:
        logger.error('filter history error: %s' % traceback.format_exc())
        return set()
def format(doc,fmt):
    list = {}
    for item in fmt:
        value = fmt[item]
        value = value.strip()[1:]
        v = doc.get(value,None)
        if not v:
            list[item] = None
            continue
        if value == "topic":
            list[item] = v[0]
        else:
            list[item] = v 
    return list

def formatlist(doc,fmt):
    list = {}
    iid = doc.get("iid",None)
    if iid:
        qstr = "http://172.24.2.64:9000/solr/hackthon_list/select?q=iid:" + iid + "&wt=json&indent=true"
        docs = getsolr(qstr)
        if len(docs)>0:
            return format(docs[0],fmt) 
    return list
def sortbyhot(items):
    iids = {}
    weight = {}
    list = []
    for iid in items:
        iids[iid] = hotclient.get(iid)
    dict= sorted(iids.iteritems(), key=lambda d:d[1], reverse = True)
    for item in dict:
        list.append(items[item[0]])
        logger.debug("item:%s,value:%s"%(item,items[item[0]]))
    return list

    

def getsolr(qstr):
    try:
        doc = []
        conn = urlopen(qstr)
        rsp = simplejson.load(conn)
        result = rsp.get('response', None)
        if result:
            doc = result["docs"]
            return doc
    except:
        logger.error('get solr data error: %s' % traceback.format_exc())
        return doc
def uzip(env, env_param):
    encoding = env.get('HTTP_CONTENT_ENCODING', None)
    status = 0
    data = None
    msg = None
    logging.debug("encoding:%s", encoding)
    if encoding == 'gzip':
        buf = StringIO(env_param)
        try:
            gz_file = gzip.GzipFile(fileobj = buf)
            data = gz_file.read()
        except IOError, e:
            status = 1
            msg = 'request params should be compressed with gzip.'
            return [status, data, msg]
    elif encoding is None:
        data = env_param
    else:
        status = 2
        msg = 'only support gzip encoding.'
    return [status, data, msg]

def mobile_handler(env, env_param, method):
    header = [("Content-Type", "application/json"),]
    req_str = env_param
    if not method == "rec":
        uzip_status, req_str, uzip_msg = uzip(env, env_param)
    params = None
    try:
        params = json.loads(req_str)
    except Exception, e:
        status = '400 Bad Request'
        logging.error('not json format! Exception:%s'%(str(e)))
        ret_val = '[3, "not json format"]'
        return status, header, ret_val

    if  'OnlineConfig.do' == method:
        return config_handler(params)

    elif 'Multi.do' == method:
        mDebug = env.get('mDebug', False)
        return multi_handler(env, params, mDebug)
    elif 'rec' == method:
        mDebug = env.get('mDebug', False)
        return rec_handler(env, params, mDebug)
    else:
        status = '405 Bad Request'
        ret_val = '[3, "method not allowed"]'
        return status, header, ret_val
