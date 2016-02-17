import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Ferdinand on 17.02.2016.
 */
public class Server {

    public static void main(String[] args) {

        String melding = "";
        String nyMelding = "Hei hei";
        ServerSocket serverSocket = null;


        try {
            serverSocket = new ServerSocket(7001);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

        while (true) {
            System.out.println("Server is running...");
            try {
                Socket connectionSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream());

                melding = in.readLine();
                System.out.println("Melding fra klient: " + melding);

                out.writeBytes(nyMelding + "\n");
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
