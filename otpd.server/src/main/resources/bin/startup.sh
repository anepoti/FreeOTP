#!/bin/sh
# OS specific support.  $var _must_ be set to either true or false.
clear

cygwin=false
os400=false
darwin=false
case "`uname`" in
CYGWIN*) cygwin=true;;
OS400*) os400=true;;
Darwin*) darwin=true;;
esac

PRGDIR=`dirname $0`


JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Home/bin export JAVA_HOME

# Only set OTPD_HOME if not already set
[ -z "$OTPD_HOME" ] && OTPD_HOME=`cd "$PRGDIR/.." ; pwd`

(cd `dirname $0`; classpath="..:../lib"
for jar in ../lib/*.jar; do
  classpath="$classpath:$jar" 
done

CLASSPATH="$classpath" $JAVA_HOME/java  otpd.server.service.StartUpOtpdServices)  >/dev/null 2>&1 &
