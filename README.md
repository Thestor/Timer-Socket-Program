# Timer-Socket-Program

Timer Socket Program is a Java Timer program that utilizes socket programming to allow communication between the server and the client. Every client can request a timer with a personalized message to the server and is entitled to one timer at a time to prevent spamming. <br />

Libraries used: java.net and java.io.


## General Information

The program is designed to run over Port 7500 and is able to accommodate more than one client connection at a single time through multithreading. When a client connects to the Socket Server, they will be redirected to the RequestHandler thread, which will process and communicate with them. Every client is designated with a *connectionId*, which automatically increments every time a client tries to establish a connection. The number of connections in the pool can be seen when a client successfully connects.

For example:
> Connection established with client 4. Number of concurrent connections: 2



## Client

For this project, I uploaded two files called TimerClient.java and TimerClient2.java, which are exactly the same. I duplicated the file to check whether the multithreading works. The client itself is just a simple client file with nothing spectacular. It listens to Port 7500 and replies to its messages. When the server sends a message ending with a ": ", we assume the server is asking for an input. Otherwise, the client will only listen. As for how the client can set up the timer, there are two distinct ways:

> Format: [duration in seconds],optional:[message]

Example 1: 15,Mamamia <br />
Example 2: 30,Olalia 

Alternatively, the client can input the following as the message part will default to "None": <br />
Example 3: 45 <br />
Example 4: 75



## Server

For this project, the server half is quite complicated. 

### SocketServer

First of all, there is a *SocketServer* class, which will hold our *SocketServer* object. By supplying a port number to the constructor, we can create a *SocketServer* over any port, but for demonstration, I used Port 7500. After that, the server may go online and await connections from the client. When a client attempts to connect, the *SocketServer* class will route it to a new thread of *RequestHandler* for it to communicate with the client. Using threads allow us to establish connections with more than one client at a time. We have an unused method **stopServer()**, which will stop the server, but currently it is meaningless for the demonstration.

### RequestHandler
The class *RequestHandler* is mostly encapsulated to keep the server's secrets safe. The class has multiple variables for communication such as *DataInputStream* and *DataOutputStream* as well as variables such as *connectionId* and *counter*. *counter*, as a static variable, will ultimately count how many concurrent connections there are at a time, whereas *connectionId* is a non-static variable that is attached to every instance of *RequestHandler*, starting from 1 to the maximum integer.

When a connection is established, the connection will have a unique *connectionId*. The handler then welcomes the client and asks them for their timer preferences (refer to Client). After retrieving the parameters, the server splits the input string and passes the arguments plus the *RequestHandler* object to the *Timer* class, which extends the *Thread* class. In other words, we are performing another multithreading with the *Timer* class. Then, this thread joins the *Timer* thread and waits until the countdown is finished before proceeding to ask the client for another timer. The client can terminate the connection by typing "EXIT" during the inquiry phase.

### Timer
The class *Timer* accepts two types of constructors, but only one is used. One of them handles when a *RequestHandler* object gets passed as the second argument, whereas the other handles only integer input (duration). By using these values, the Timer class goes to sleep for **duration * 1000 milliseconds**. We use the **sleep()** method instead of **while()** because **while()**, despite being more accurate, consumes more processor resources. At the end of the sleep, the class prints a message in the server console and notifies the client via the handler's **sendMessage(String)** method alongside the client's message. This also implies that the thread has closed.

For visual aid, every timer will have their own Thread ID, which will be sent in the console, so we can differentiate one Timer thread from another:
> New timer thread created for 16 seconds. Requested by client 5 with the message = "I love everybody!". <br />
> Starting timer thread of ID 31 for 16 seconds. Requested by client 5 with the message = "I love everybody!". <br />

## Others

We implement various input validations and try-catch brackets to capture the errors and print them in the console. Shutting down the client without first closing the connection with the server is one of the example cases that will raise an exception, but it is caught and therefore will not stop the program.


# Installation Guide

To install the program, it is only necessary to download the FinalExam folder as the Java classes have their package set to FinalExam. Before running the code, ensure that the classpath is correct and that it points to the **main()** methods. Boot the program by first running the TimerServer.java then the client java files. Otherwise, running the client java files will result in errors saying that connection is refused. If successful, the server will say "Listening for Connection...", and whenever the client connects, it will greet the client with a welcome message.



# Contributors
