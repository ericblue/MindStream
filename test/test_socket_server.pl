#!/usr/bin/env perl

use IO::Socket::INET;
 
# auto-flush on socket
$| = 1;
 
$SIG{'PIPE'} = 'IGNORE'; 

# creating a listening socket
my $socket = new IO::Socket::INET (
    LocalHost => '0.0.0.0',
    LocalPort => '13854',
    Proto => 'tcp',
    Listen => 5,
    Reuse => 1
);
die "cannot create socket $!\n" unless $socket;
print "server waiting for client connection on port 13854\n";
 
open(EEG, "eegdata.txt") or die ("Can't open EEG data source!");
 
while(1) {
    # waiting for a new client connection
    my $client_socket = $socket->accept();
 
    # get information about a newly connected client
    my $client_address = $client_socket->peerhost();
    my $client_port = $client_socket->peerport();
    print "connection from $client_address:$client_port\n";
 
    # write response data to the connected client
    while (<EEG>) {
        $client_socket->send($_);
        sleep(1);
        
    }
 
    # notify client that response has been sent
    shutdown($client_socket, 1);
}
 
close(EEG);
$socket->close();