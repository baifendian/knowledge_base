import logging
from logging.handlers import TimedRotatingFileHandler
import settings

def set_logger(filename):
    log = logging.getLogger()
    fmt_str = '[%(levelname)s %(asctime)s @ %(process)d] (%(pathname)s/%(funcName)s:%(lineno)d) - %(message)s'
    formatter = logging.Formatter(fmt_str)
    fileTimeHandler = TimedRotatingFileHandler(filename, "D", 1, 20) 

    fileTimeHandler.suffix = "%Y%m%d"  
    fileTimeHandler.setFormatter(formatter)
    logging.basicConfig(level = logging.INFO)
    log.handlers = []
    log.addHandler(fileTimeHandler)
    if settings.DEBUG:
        log.setLevel(logging.DEBUG)
    else:
        log.setLevel(logging.WARN)
        #log.setLevel(logging.INFO)

def get_logger(name, filename, fmt_str = '[%(levelname)s %(asctime)s @ %(process)d] - %(message)s'):
    log = logging.getLogger(name)
    formatter = logging.Formatter(fmt_str)
    fileTimeHandler = TimedRotatingFileHandler(filename, "D", 1, 20) 

    fileTimeHandler.suffix = "%Y%m%d" 
    fileTimeHandler.setFormatter(formatter)
    logging.basicConfig(level = logging.INFO)
    log.handlers = []
    log.addHandler(fileTimeHandler)
    log.setLevel(logging.DEBUG)
    return log
