import java.io.*;
import java.util.ArrayList;

public class FileManager {
    private static File file;
    private static FileReader fr;
    private static BufferedReader br;
    private static FileWriter fw;
    private static BufferedWriter bw;

    public static ArrayList<Request> readInHistory() throws IOException {
        file = new File("history.txt");
        if (!(file.exists())) {
            file.createNewFile();
        }
        fr = new FileReader(file);
        br = new BufferedReader(fr);
        ArrayList<Request> history = new ArrayList<>();
        String nextLine = br.readLine();
        while (nextLine != null) {
            String[] words = nextLine.split("XXX");
            history.add(new Request(Long.valueOf(words[0]), words[1], words[2], words[3]));
            nextLine = br.readLine();
        }
        return history;
    }

    public static void saveHistory(ArrayList<Request> history) throws IOException {
        fw = new FileWriter(file);
        bw = new BufferedWriter(fw);
        for (Request r : history) {
            bw.write(r.getTime() + "XXX" + r.getIp() + "XXX" + r.getAction() + "XXX" + r.getArgument());
            bw.newLine();
        }
        bw.close();
        fw.close();
    }
}
