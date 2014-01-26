#!/bin/sh

# Note: to be run from linux or MacOS.  Connects to a remote host running the NeuroSky Thinkgear app
# enables JSON output, and saves to a text file 

HOST=yourpc
PORT=13854

echo '{"enableRawOutput": false, "format": "Json"}' | nc $HOST $PORT | tee eegdata.txt
