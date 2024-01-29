package NetworkScanner;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkScanResult {
    String network;
    List<String> reachableHosts;
    Map<String, List<Integer>> openPorts;
    Map<String, String> hostNames; // Almacenar los nombres de los hosts
    Map<String, Map<Integer, Map<String, String>>> serviceDetails; // Almacenar detalles de servicios

    public NetworkScanResult(String network) {
        this.network = network;
        this.reachableHosts = new ArrayList<>();
        this.openPorts = new HashMap<>();
        this.hostNames = new HashMap<>();
        this.serviceDetails = new HashMap<>();
    }

    public void addReachableHost(String host) {
        reachableHosts.add(host);
        openPorts.put(host, new ArrayList<>()); // Preparar para almacenar puertos abiertos
        serviceDetails.put(host, new HashMap<>()); // Preparar para almacenar detalles de servicios
        // Obtener y almacenar el nombre del host
        String hostName = getHostName(host);
        hostNames.put(host, hostName);
    }

    public void addOpenPort(String host, int port) {
        openPorts.getOrDefault(host, new ArrayList<>()).add(port);
    }

    // Método para añadir detalles de servicio
    public void addServiceDetails(String host, int port, Map<String, String> serviceInfo) {
        if (!serviceDetails.containsKey(host)) {
            serviceDetails.put(host, new HashMap<>());
        }
        serviceDetails.get(host).put(port, serviceInfo);
    }

    // Método para obtener los detalles del servicio para un host específico
    public Map<Integer, Map<String, String>> getServiceDetails(String host) {
        return serviceDetails.getOrDefault(host, new HashMap<>());
    }

    private String getHostName(String ipAddress) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            return inetAddress.getCanonicalHostName();
        } catch (Exception e) {
            e.printStackTrace();
            return ipAddress; // Devuelve la dirección IP si no se puede resolver el nombre
        }
    }

    // Método para compilar y obtener la información del host
    public List<String> getHostInfo(String hostAddress) {
        List<String> hostInfo = new ArrayList<>();
        String hostName = hostNames.getOrDefault(hostAddress, "Nombre desconocido");
        hostInfo.add("Host: " + hostAddress);
        hostInfo.add("Nombre del Host: " + hostName);

        return hostInfo;
    }
}