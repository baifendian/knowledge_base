#!/bin/bash

HOME=`dirname $(cd "$(dirname "$0")"; pwd)`

while getopts "h" Option
do
case $Option in
h) echo "Version: `cat $HOME/VERSION`"
   echo "Usage: $0 <start|reload|stop>"
   exit
   ;;
esac
done
shift $(($OPTIND - 1))

case $1 in
start)       /opt/Python-2.7/bin/uwsgi --python-path $HOME/app/ --pidfile $HOME/log/uwsgi.pid --daemonize $HOME/log/uwsgi.log --log-maxsize 1000000000 --log-backupname ${HOME}/log/uwsgi.log.old -x $HOME/etc/mobileapi_new.xml;;
reload)      /opt/Python-2.7/bin/uwsgi --reload $HOME/log/uwsgi.pid;;
stop)        /opt/Python-2.7/bin/uwsgi --stop $HOME/log/uwsgi.pid; rm -f $HOME/log/uwsgi.pid;;
*)   echo "Usage: $0 <start|reload|stop>";;
esac

