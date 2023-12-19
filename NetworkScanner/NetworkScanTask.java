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
}
