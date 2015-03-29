#! /bin/bash 
set -e

DIR=$(cd `dirname $0`;pwd)
YESTERDAY="$(date --date='1 day ago' +%Y-%m-%d)"

curl -s "http://hadoop.corp.mediav.com/dailybill?date=$YESTERDAY"  > dailybill.$YESTERDAY

cat $DIR/email.header > dailybill.$YESTERDAY.html
cat dailybill.$YESTERDAY >> dailybill.$YESTERDAY.html

cat dailybill.$YESTERDAY.html |sendmail -t
