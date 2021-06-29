# Timer-Socket-Program

Timer Socket Program is a Java Timer program that utilizes socket programming to allow communication between the server and the client. Every client can request a timer with a personalized message to the server and is entitled to one timer at a time to prevent spamming.

## General Information

The program is designed to run over Port 7500 and is able to accommodate more than one client connection at a single time through multithreading. When a client connects to the Socket Server, they will be redirected to the RequestHandler thread, which will process and communicate with them. Every client is designated with a *connectionId*, which automatically increments every time a client tries to establish a connection. The number of connections in the pool can be seen when a client successfully connects.

For example:
> Connection established with client 4. Number of concurrent connections: 2

## Client

T
