#!/usr/bin/env python
# -*- coding: utf-8 -*-
import logging, traceback
import os, sys, uwsgi

import mobile_handler
import settings
import utils


#import log_conf
#logger = logging.getLogger("serverlog")

from set_logger import set_logger
set_logger(os.path.dirname(os.path.abspath(__file__)) + '/../log/server.log')

debug_flag = settings.DEBUG


def default_handler(request):
    status = '200 OK'
    header = [('Content-Type', 'text/html')]
    req_method = request['REQUEST_METHOD']
    path_info = request['PATH_INFO']
    #only for post
    env_param = utils.get_param(request)
    method = path_info[path_info.rindex('/')+1:]
    logging.info("req_method=%s, path_info=%s, param=%s, method=%s", req_method, path_info, env_param, method)
    # get the method
    # method is '' or None
    print method
    if not method:
        ret_val = [1, "Empty Method"]
        status = '405 Method Not Allowed'
        return status, header, ret_val 
    return mobile_handler.mobile_handler(request, env_param, method)



def application(environ, start_response):
    '''uwsgi enter'''
    try:
        logging.debug('environ: %s', environ)
        status, header, body = default_handler(environ)
        logging.info('status: %s,header: %s,body: %s', status,header,body)
        start_response(status, header)
        yield body
    except Exception, e:
        #捕获入口函数异常，避免uwsgi无回应的问题(flup无此问题)
        logging.fatal(traceback.format_exc())
        logging.fatal('environ: %s', environ)
        start_response('200 OK', [])
        if debug_flag:
            yield traceback.format_exc()
        else:
            yield "[4, \"Server Error!\"]"

