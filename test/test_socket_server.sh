#!/bin/sh

ncat -l 13854 -e 'print_eeg_stream.sh eegdata_mobile.txt'
