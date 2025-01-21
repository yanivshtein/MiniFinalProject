package logic;

/**
 * Represents the information of a client, including the client's IP address and host name.
 */
public class ClientInfo {
    private String ipAddress;
    private String hostName;

    /**
     * Constructs a ClientInfo object with the specified IP address and host name.
     * @param ipAddress The IP address of the client.
     * @param hostName The host name of the client.
     */
    public ClientInfo(String ipAddress, String hostName) {
        this.ipAddress = ipAddress;
        this.hostName = hostName;
    }

    /**
     * Gets the IP address of the client.
     * @return The IP address of the client.
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Gets the host name of the client.
     * @return The host name of the client.
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * Returns a string representation of the client information in the format:
     * "IP = <ipAddress>, Host Name = <hostName>".
     * @return A string representation of the client information.
     */
    @Override
    public String toString() {
        return "IP = " + ipAddress + ", Host Name = " + hostName;
    }
}