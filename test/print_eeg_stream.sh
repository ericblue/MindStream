#!/bin/sh


cat $1 | perl -e '$|=1;while(<STDIN>){sleep 1;print $_}'
