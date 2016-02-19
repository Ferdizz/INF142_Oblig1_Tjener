public class Request {
    private long time;
    private String ip;
    private String action;
    private String argument;

    public Request(String ip, String action, String argument) {
        this.time = System.currentTimeMillis();
        this.ip = ip;
        this.action = action;
        this.argument = argument;
    }

    public Request(long time, String ip, String action, String argument) {
        this.time = time;
        this.ip = ip;
        this.action = action;
        this.argument = argument;
    }

    public long getTime() {
        return time;
    }

    public String getIp() {
        return ip;
    }

    public String getAction() {
        return action;
    }

    public String getArgument() {
        return argument;
    }
}
