#!/usr/bin/perl

use JSON;
use Data::Dumper;
use strict;

if (!$ARGV[0]) {
	print "Usage: $0 <eeg_file.txt>\n";
    exit(-1);
}

my $eeg_file=$ARGV[0];

open(JSON, $eeg_file) or die "Can't open file $eeg_file!";
my @json_text = <JSON>;
close(JSON);

foreach (@json_text) {

    my $json = from_json($_);
    print Dumper $json; 
    
    
}

