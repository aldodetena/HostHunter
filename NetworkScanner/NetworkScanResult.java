package NetworkScanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;

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

    // Método para exportar los datos a HTML
    public void exportHostToHTML(String hostAddress) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // Elemento raíz HTML
            Element html = doc.createElement("html");
            doc.appendChild(html);

            // Head con estilos CSS
            Element head = doc.createElement("head");
            html.appendChild(head);
            Element style = doc.createElement("style");
            style.appendChild(doc.createTextNode("body { font-family: Arial, sans-serif; } h1 { color: navy; } table { width: 100%; border-collapse: collapse; } th, td { border: 1px solid #ddd; padding: 8px; text-align: left; } th { background-color: #f2f2f2; }"));
            head.appendChild(style);

            // Body
            Element body = doc.createElement("body");
            html.appendChild(body);
            Element h1 = doc.createElement("h1");
            h1.appendChild(doc.createTextNode("Informe de escaneo de red del Host: " + hostAddress));
            body.appendChild(h1);

            if (reachableHosts.contains(hostAddress)) {
                Element table = doc.createElement("table");
                body.appendChild(table);

                // Crear cabecera de tabla
                Element trHead = doc.createElement("tr");
                table.appendChild(trHead);
                Element thPort = doc.createElement("th");
                thPort.appendChild(doc.createTextNode("Puerto"));
                trHead.appendChild(thPort);
                Element thService = doc.createElement("th");
                thService.appendChild(doc.createTextNode("Detalles del Servicio"));
                trHead.appendChild(thService);

                // Puertos y servicios
                List<Integer> ports = openPorts.getOrDefault(hostAddress, new ArrayList<>());
                for (Integer port : ports) {
                    Element tr = doc.createElement("tr");
                    table.appendChild(tr);

                    Element tdPort = doc.createElement("td");
                    tdPort.appendChild(doc.createTextNode(port.toString()));
                    tr.appendChild(tdPort);

                    Element tdDetails = doc.createElement("td");
                    Map<String, String> serviceDetails = getServiceDetails(hostAddress).getOrDefault(port, new HashMap<>());
                    tdDetails.appendChild(doc.createTextNode(serviceDetails.toString()));
                    tr.appendChild(tdDetails);
                }
            }

            // Guardar el contenido en un archivo HTML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "html");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(hostAddress + "_report.html"));

            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}