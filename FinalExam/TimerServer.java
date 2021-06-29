package FinalExam;
import java.net.*;  
import java.io.*;  

public class TimerServer {

    private static class Timer extends Thread {
        int duration;
        RequestHandler handler = null;
        // boolean hasPassed = false;

        public Timer(int duration) {
            this.duration = duration;
            System.out.println("New timer thread created for " + duration + " seconds.");
        }
        public Timer(int duration, RequestHandler handler) {
            this.duration = duration;
            this.handler = handler;
            System.out.println("New timer thread created for " + duration + " seconds. Requested by client "
                    + handler.getConnectionID() + " with the message = \"" + handler.getUserMessage() + "\".");
        }

        @Override
        public void run() {
            try {
                System.out.println("Starting timer thread of ID = " + Thread.currentThread().getId() + " for " + duration + " seconds. Requested by client "
                        + handler.getConnectionID() + " with the message = \"" + handler.getUserMessage() + "\".\n");


                /*
                INEFFICIENT METHOD - Using while is more accurate but consumes more resources.

                long beginAt = System.currentTimeMillis();
                long endAt = beginAt + duration * 1000;
                while (!hasPassed) {
                    if (System.currentTimeMillis() >= endAt) {
                        hasPassed = true;
                    }
                }
                */

                // Using SLEEP the current thread to suspend execution for a specified period -> less accurate but it frees processor resources.
                Thread.sleep(duration * 1000);

            }
            catch (Exception e){

                // Interruptions, etc, go here.
                System.out.println("Thread of ID = " +  Thread.currentThread().getId() + " interrupted. Requested by client "
                        + handler.getConnectionID() + " with the message = \"" + handler.getUserMessage() + "\".\n");
            }

            // If there are no errors, and the countdown finishes successfully, print this.
            System.out.println("Timer of ID = " + Thread.currentThread().getId() + " has ended. Requested by client "
                    + handler.getConnectionID() + " with the message = \"" + handler.getUserMessage() + "\".\n");

            // If RequestHandler is passed, use sendMessage to inform the client.
            if (handler != null) {
                try {
                    handler.sendMessage("Message: " + handler.getUserMessage());
                    handler.sendMessage("Timer that lasts for " + duration + " seconds has ended.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    static class RequestHandler extends Thread
    {
        // Encapsulate everything for server's privacy.
        private final Socket socket;
        private DataInputStream din;
        private DataOutputStream dout;

        // Counter refers to the number of concurrent connections.
        private static int counter = 0;

        // UniqueConnectionNumber serves as the ID of the current connection thread. Unlike counter, it doesn't decrease when a connection is closed.
        private static int connectionIdCounter = 0;
        private int connectionId;

        // User's personalized timer message.
        private String userMessage;

        public RequestHandler(Socket socket)
        {
            this.socket = socket;
        }

        public void sendMessage(String message) throws IOException {
            dout.writeUTF(message);
            dout.flush();
        }

        public String getUserMessage() {
            return this.userMessage;
        }

        public int getConnectionID(){
            return this.connectionId;
        }

        @Override
        public void run()
        {
            try
            {
                String toReceive = "";

                // One connection -> +1 to Counter and +1 to connectionIdCounter. Before closing, -1 to Counter.
                counter++; connectionIdCounter++;
                // Applies the current connection ID.
                connectionId = connectionIdCounter;

                System.out.println("Connection established with client " + connectionId + ". Number of concurrent connections: " + counter + "\n");

                din = new DataInputStream(socket.getInputStream());
                dout = new DataOutputStream(socket.getOutputStream());

                sendMessage("Welcome to the Timer program, where you can set timers with messages!");

                while(!toReceive.equals("EXIT")){

                    // Reset message.
                    this.userMessage = "None";

                    sendMessage("Type EXIT to close connection. Input how long you want the timer to be (format = ([duration],optional: [message])): ");

                    toReceive = din.readUTF();
                    try{
                        // Split the received message. Expecting [0] -> duration, [1] -> message.
                        String[] splitInput = toReceive.split(",");

                        // Convert to int and pass the integer to the Countdown threading method.
                        int howLong = Integer.parseInt(splitInput[0]);

                        // If user passes no string, we just assume it will be 'None'. Sophisticated versions with ifs are available, but let's keep this simple.
                        if (splitInput.length > 1) {
                            this.userMessage = splitInput[1];
                        }

                        // Prints how long the timer the client asks for.
                        System.out.println("Client " + connectionId + " requests a timer for " + howLong + " seconds with a message = \"" + this.userMessage + "\".");

                        // Sends back the timer information.
                        sendMessage("Timer with the message \"" + this.userMessage + "\" began for " + toReceive + " seconds.");

                        Thread newTimer = new TimerServer.Timer(howLong, this);
                        newTimer.start();
                        newTimer.join();
                    }
                    catch(NumberFormatException | ArrayIndexOutOfBoundsException e){

                        // Asks the client for a valid number.
                        sendMessage("Please recheck your input and follow the correct format!");
                    }
                }

                counter--;

                // Close the connection with the client.
                din.close();
                dout.close();
                socket.close();

                System.out.println( "Connection with client " + connectionId + " closed.\n");

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static class SocketServer extends Thread
    {
        private ServerSocket Socket;
        private int port;
        private boolean isRunning = false;

        // No parameter? Just default to 7500, else use parameter.
        public SocketServer()
        {
            this.port = 7500;
        }

        public SocketServer(int port)
        {
            this.port = port;
        }

        public void startServer()
        {
            try
            {
                Socket = new ServerSocket(port);
                this.start();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        public void stopServer()
        {
            isRunning = false;
            this.interrupt();
        }

        @Override
        public void run()
        {
            isRunning = true;
            while(isRunning)
            {
                System.out.println("From Socket " + port + ": Listening for a connection..." );

                // Accept every incoming call, then pass it to a new RequestHandler.
                Socket socket = null;
                try {
                    socket = Socket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                RequestHandler requestHandler = new RequestHandler(socket);
                requestHandler.start();
            }
        }

        public static void main( String[] args )
        {
            System.out.println( "Server has been started on port " + 7500 + ".");

            SocketServer server = new SocketServer(7500);
            server.startServer();
        }
    }
}

