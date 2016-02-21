import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class T {
    private static int THE_HOLY_NUMBER = 9001;
    private static ArrayList<Request> history = new ArrayList<>();
    private static String melding = "";
    private static ServerSocket serverSocket = null;
    private static BufferedReader in;
    private static DataOutputStream out;

    public static void main(String[] args) {
        setupSocket();
        loadHistory();

        while (true) {
            System.out.println("T is running...");
            try {
                Socket connectionSocket = waitAndPrepareConnection();
                if(!(connectionSocket == null)) {
                    melding = in.readLine();
                    String ip = connectionSocket.getInetAddress().getHostName();
                    System.out.println("New request from " + ip + ": " + melding);

                    String[] words = melding.split(" ");
                    if (words.length == 1) {
                        switch (words[0]) {
                            case "GET":
                                handleGetRequest(ip);
                                break;
                            case "HISTORY":
                                handleHistoryRequest(ip);
                                break;
                            case "KILL":
                                handleKillRequest(connectionSocket, ip);
                                break;
                            default:
                                handleDefault();
                                break;
                        }
                    } else if (words.length == 2) {
                        switch (words[0]) {
                            case "ADD":
                                handleAddRequest(ip, words);
                                break;
                            case "SUB":
                                handleSubRequest(ip, words);
                                break;
                            default:
                                handleDefault();
                                break;
                        }
                    }
                }
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

    /**
     * Setter opp socketen til serveren
     */
    private static void setupSocket(){
        try {
            serverSocket = new ServerSocket(7001);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Leser inn historien fra fil
     */
    private static void loadHistory(){
        try {
            history = FileManager.readInHistory();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Venter til en klient kobler seg til og setter deretter opp socket og ut-og inn strømmer
     * @return Socketen til klienten
     */
    private static Socket waitAndPrepareConnection(){
        try {
            Socket connectionSocket = serverSocket.accept();
            in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            out = new DataOutputStream(connectionSocket.getOutputStream());
            return connectionSocket;
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sender feilmelding til klienten om at forespørselsen ikke fulgte formatet til protokollen
     */
    private static void handleDefault(){
        try {
            out.writeBytes("ERROR: Request didn't succeed. Invalid argument.\n");
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Returner V
     * @param ip IP-en til klienten
     */
    private static void handleGetRequest(String ip){
        try {
            history.add(new Request(ip, "GET", "NULL"));
            out.writeBytes("OK: The number is now: " + THE_HOLY_NUMBER + "\n");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Sender historien over forespørsler som er gjort til serveren
     * @param ip IP-en til klienten
     */
    private static void handleHistoryRequest(String ip){
        try {
            history.add(new Request(ip, "HISTORY", "NULL"));
            String historyMessage = "";
            for (Request r : history) {
                historyMessage += formatDate(r.getTime()) + " : " + r.getIp() + " : " + r.getAction() + " : " + r.getArgument() + "NEW_LINE";
            }
            out.writeBytes(historyMessage + "\n");
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Stenger strømmer og tilkobling til klienten før server-socketen stenges
     * @param connectionSocket Socketen til klienten
     * @param ip IP-en til klienten
     */
    private static void handleKillRequest(Socket connectionSocket, String ip){
        history.add(new Request(ip, "KILL", "NULL"));
        try {
            FileManager.saveHistory(history);
            in.close();
            out.close();
            connectionSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Legger nummeret som spesifiseres til V dersom nummeret er et tall og sender respons tilbake til klienten
     * @param ip IP-en til klienten
     * @param words Ordene som ble sendt i forespørselen
     */
    private static void handleAddRequest(String ip, String[] words){
        try {
            int newNumber = -1;
            try {
                newNumber = Integer.parseInt(words[1]);
            } catch (NumberFormatException e) {
                out.writeBytes("ERROR: Request didn't succeed. Invalid format.\n");
            }
            if (newNumber != -1) {
                THE_HOLY_NUMBER += newNumber;
                history.add(new Request(ip, "ADD", "" + newNumber));
                out.writeBytes("OK\n");
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Trekker nummeret som spesifiseres fra V dersom nummeret er et tall og sender respons tilbake til klienten
     * @param ip IP-en til klienten
     * @param words Ordene som ble sendt i forespørselsen
     */
    private static void handleSubRequest(String ip, String[] words){
        try {
            int newNumber = -1;
            try {
                newNumber = Integer.parseInt(words[1]);
            } catch (NumberFormatException e) {
                out.writeBytes("ERROR: Request didn't succeed. The number to add must be a number.\n");
            }
            if (newNumber != -1) {
                THE_HOLY_NUMBER -= newNumber;
                history.add(new Request(ip, "SUB", "" + newNumber));
                out.writeBytes("OK\n");
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Denne metoden konverterer et Unix tidsstempel til lesbar dato og klokkeslett
     * @param time Tiden som skal konverteres
     * @return Streng med data og klokkeslett
     */
    private static String formatDate(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        return sdf.format(date);
    }
}
