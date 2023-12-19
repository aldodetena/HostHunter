package NetworkScanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class NetworkScanResult {
    String network;
    List<String> reachableHosts;
    Map<String, List<Integer>> openPorts;

    public NetworkScanResult(String network) {
        this.network = network;
        this.reachableHosts = new ArrayList<>();
        this.openPorts = new HashMap<>();
    }

    public void addReachableHost(String host) {
        reachableHosts.add(host);
        openPorts.put(host, new ArrayList<>()); // Preparar para almacenar puertos abiertos
    }

    public void addOpenPort(String host, int port) {
        if (openPorts.containsKey(host)) {
            openPorts.get(host).add(port);
        }
    }
}