package NetworkScanner;

public class NetworkScanTask {
    String network;
    int startPort;
    int endPort;

    public NetworkScanTask(String network, int startPort, int endPort) {
        this.network = network;
        this.startPort = startPort;
        this.endPort = endPort;
    }

    public String getNetwork() {
        return network;
    }

    public int getStartPort() {
        return startPort;
    }

    public int getEndPort() {
        return endPort;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public void setStartPort(int startPort) {
        this.startPort = startPort;
    }

    public void setEndPort(int endPort) {
        this.endPort = endPort;
    }
}
