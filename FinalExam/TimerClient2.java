package FinalExam;
import java.net.*;  
import java.io.*; 

public class TimerClient2 {
    public static void main(String args[]) throws Exception{

        // Create the socket connection.
        Socket s = new Socket("localhost",7500);
        DataInputStream din = new DataInputStream(s.getInputStream());
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String toReceive = "";
        String toSend = "";

        while(!toSend.equals("EXIT")){

            toReceive = din.readUTF();
            System.out.println(toReceive);

            // If toReceive string ends with ": ", we need to respond."
            if (toReceive.endsWith(": ")) {
                toSend = br.readLine();
                dout.writeUTF(toSend);
                dout.flush();
            }

            // Otherwise, we listen to the server again.
            else {
                continue;
            }
        }  
        
        dout.close();  
        s.close();  
    }
}  

