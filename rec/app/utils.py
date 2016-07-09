#!/usr/bin/env python
# -*- coding: utf-8 -*-
import base64
import gzip
import hashlib
import os, logging, sys
import logging.handlers
import uuid
import md5
import time
import json

import settings

#import log_conf
#logger = logging.getLogger("serverlog")


def get_param(env):
    ''' support: only POST request '''
    req_method = env['REQUEST_METHOD']
    if req_method.upper() == 'POST':
        try:
            request_body_size = int(env.get('CONTENT_LENGTH', 0))
        except:
            request_body_size = 0
            logging.error('get content length error: %s' % (traceback.format_exc()))
        return env['wsgi.input'].read(request_body_size)

