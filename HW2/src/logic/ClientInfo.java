package logic;

public class ClientInfo {
    private String ipAddress;
    private String hostName;

    public ClientInfo(String ipAddress, String hostName) {
        this.ipAddress = ipAddress;
        this.hostName = hostName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getHostName() {
        return hostName;
    }

    @Override
    public String toString() {
        return "IP = " + ipAddress + ", Host Name = " + hostName;
    }
}