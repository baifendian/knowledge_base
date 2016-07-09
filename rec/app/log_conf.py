# -*- coding: utf-8 -*-
import logging.config
import os
home_path = os.path.dirname(os.path.abspath(__file__))
print home_path

logging.config.dictConfig({
    'version': 1,
    'disable_existing_loggers': True,
    'formatters': {
        'verbose': {
            'format': "[%(levelname)s %(asctime)s @ %(process)d %(filename)s (%(funcName)s):%(lineno)d] - %(message)s",
            'datefmt': "%Y-%m-%d %H:%M:%S"
        },
        'backupfmt': {
            'format': "%(message)s",
            'datefmt': "%Y-%m-%d %H:%M:%S"
        }
    },
    'handlers': {
        'null': {
            'level': 'DEBUG',
            'class': 'logging.NullHandler'
        },
        'serverfile': {
            # 如果没有使用并发的日志处理类，在多实例的情况下日志会出现缺失
            'class': 'cloghandler.ConcurrentRotatingFileHandler',
            # 当达到3GB时分割日志
            'maxBytes': 1024 * 1024 * 1024 * 3,
            # 最多保留30份文件
            'backupCount': 30,
            # If delay is true,
            # then file opening is deferred until the first call to emit().
            'delay': True,
            'filename': home_path+'/../log/server.log',
            'formatter': 'verbose'
        },
        'backupfile': {
            # 如果没有使用并发的日志处理类，在多实例的情况下日志会出现缺失
            'class': 'cloghandler.ConcurrentRotatingFileHandler',
            # 当达到3GB时分割日志
            'maxBytes': 1024 * 1024 * 1024 * 3,
            # 最多保留30份文件
            'backupCount': 30,
            # If delay is true,
            # then file opening is deferred until the first call to emit().
            'delay': True,
            'filename': home_path+'/../log/backup.log',
            'formatter': 'backupfmt'
        },
        'workerfile': {
            # 如果没有使用并发的日志处理类，在多实例的情况下日志会出现缺失
            'class': 'cloghandler.ConcurrentRotatingFileHandler',
            # 当达到3GB时分割日志
            'maxBytes': 1024 * 1024 * 1024 * 3,
            # 最多保留30份文件
            'backupCount': 30,
            # If delay is true,
            # then file opening is deferred until the first call to emit().
            'delay': True,
            'filename': home_path+'/../log/worker.log',
            'formatter': 'verbose'
        }
      
    },
    'loggers': {
        'serverlog': {
            'handlers': ['serverfile'],
            'level': 'DEBUG'
        },
        'backuplog': {
            'handlers': ['backupfile'],
            'level': 'DEBUG'
        },
        'workerlog': {
            'handlers': ['workerfile'],
            'level': 'DEBUG'
        }
    }
})
