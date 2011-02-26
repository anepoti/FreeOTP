#!/bin/bash

############################################################
remote_user='anepoties'
remote_host='otphost'
remote_path='/tmp/etc/otpstate'
############################################################

if [ $# -lt 2 ]

then
    echo 'Usage : $0 [otpstate line] $1 [common name]'
exit
fi

typeset LP_FILE=$2

# Remove the target file if any
rm -f ${LP_FILE}

# Dump the data values to the file
printf "%-10s" $1 >> ${LP_FILE}
# Add '\n' if you want a newline character: printf "%-8s\n"

#scp $2 $remote_user@$remote_host:$remote_path
cp $2 $remote_path/$2 
rm ./$2