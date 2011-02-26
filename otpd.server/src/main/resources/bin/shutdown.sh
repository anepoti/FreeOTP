#!/bin/sh
kill `ps -ef | grep otpd.server.service.StartUpOtpdServices | grep -v grep | awk '{ print $2 }'` >/dev/null
