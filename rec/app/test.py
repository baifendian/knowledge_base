# -*- coding: utf-8 -*-
import simplejson
from urllib2 import *
import json
import BfdCodis as codis
import UserHistory_pb2
client = codis.BfdCodis("172.24.2.35:2181,172.24.2.36:2181,172.24.2.37:2181", "/zk/codis/db_mini2/proxy", "useraction")#用户历史
topclient = codis.BfdCodis("172.24.2.35:2181,172.24.2.36:2181,172.24.2.37:2181", "/zk/codis/db_mini2/proxy", "usertopic")#用户历史
key = "98e7f51fb7da"
value = topclient.smembers(key)
for v in value:
    print v
def format(doc,fmt):
    list = {}
    for item in fmt:
        value = fmt[item]
        value = value.strip()[1:]
        v = doc.get(value,None)
        print v
        list[item] = v
    return list
key = "G:UserAction:98e7f51fb7da:Ctest_hackthon:MFeedBack"
value = client.lrange(key,0,-1)
for v in value:
    tmp = UserHistory_pb2.ItemTimePair()
    tmp.ParseFromString(v)
    print tmp

num = "4"
qstr = "http://172.24.2.64:9000/solr/Ctoutiao/select?q=*:*&rows=" + num + "&wt=json&indent=true"
conn = urlopen(qstr)
rsp = simplejson.load(conn)
    
result = rsp["response"]
fmt = json.loads('{"iid":"$iid"}')

docs = result["docs"]
ret = []
for doc in docs:
    #list =[]
    list =json.dumps(format(doc,fmt))
    ret.append(list)
    print "----------------------------------"

print json.dumps(ret)
#print docs
for s in rsp:
    print s
    print "******************************"
    print rsp[s]
    
#    print "---------------------------------------------------------"
