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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Almacena y gestiona los resultados de un escaneo de red, incluyendo hosts alcanzables, puertos abiertos,
 * y detalles de servicios para cada host.
 */
public class NetworkScanResult {
    String network;
    List<String> reachableHosts;
    Map<String, List<Integer>> openPorts;
    Map<String, String> hostNames; // Almacenar los nombres de los hosts
    Map<String, Map<Integer, Map<String, String>>> serviceDetails; // Almacenar detalles de servicios

    /**
     * Constructor para crear un resultado de escaneo de red con una red específica.
     * @param network La red sobre la que se realizó el escaneo.
     */
    public NetworkScanResult(String network) {
        this.network = network;
        this.reachableHosts = new ArrayList<>();
        this.openPorts = new HashMap<>();
        this.hostNames = new HashMap<>();
        this.serviceDetails = new HashMap<>();
    }

    /**
     * Añade un host alcanzable al resultado del escaneo y prepara sus contenedores de datos relacionados.
     * @param host La dirección IP del host alcanzable.
     */
    public void addReachableHost(String host) {
        reachableHosts.add(host);
        openPorts.put(host, new ArrayList<>()); // Preparar para almacenar puertos abiertos
        serviceDetails.put(host, new HashMap<>()); // Preparar para almacenar detalles de servicios
        // Obtener y almacenar el nombre del host
        String hostName = getHostName(host);
        hostNames.put(host, hostName);
    }

    /**
     * Registra un puerto abierto para un host específico.
     * @param host La dirección IP del host.
     * @param port El número de puerto abierto.
     */
    public void addOpenPort(String host, int port) {
        openPorts.getOrDefault(host, new ArrayList<>()).add(port);
    }

    /**
     * Añade detalles de un servicio asociados a un puerto específico de un host.
     * @param host La dirección IP del host.
     * @param port El puerto en el cual el servicio está corriendo.
     * @param serviceInfo Un mapa con la información del servicio.
     */
    public void addServiceDetails(String host, int port, Map<String, String> serviceInfo) {
        if (!serviceDetails.containsKey(host)) {
            serviceDetails.put(host, new HashMap<>());
        }
        serviceDetails.get(host).put(port, serviceInfo);
    }

    /**
     * Obtiene los detalles de servicio para un puerto específico de un host.
     * @param host La dirección IP del host.
     * @return Un mapa con los detalles del servicio por puerto.
     */
    public Map<Integer, Map<String, String>> getServiceDetails(String host) {
        return serviceDetails.getOrDefault(host, new HashMap<>());
    }

    /**
     * Obtiene el nombre canónico del host dado su dirección IP.
     * @param ipAddress La dirección IP del host.
     * @return El nombre canónico del host o la dirección IP si el nombre no puede ser resuelto.
     */
    public static String getHostName(String ipAddress) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            String hostName = inetAddress.getHostName();
            if (hostName.equals(ipAddress)) {
                // Si getHostName() devuelve la misma IP, intentar con getCanonicalHostName()
                hostName = inetAddress.getCanonicalHostName();
            }
            return hostName;
        } catch (UnknownHostException e) {
            System.err.println("No se pudo resolver el nombre de host para la dirección IP: " + ipAddress);
            return ipAddress; // Devuelve la dirección IP si no se puede resolver el nombre
        }
    }

    public static String identifyOS(String ipAddress) {
        String osType = "Desconocido";
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("ping", "-c", "1", ipAddress);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("ttl=")) {
                    int ttlIndex = line.indexOf("ttl=");
                    String ttlValue = line.substring(ttlIndex + 4, line.indexOf(" ", ttlIndex));
                    int ttl = Integer.parseInt(ttlValue);

                    if (ttl <= 64) {
                        osType = "Unix/Linux";
                    } else if (ttl <= 128) {
                        osType = "Windows";
                    } else if (ttl > 128) {
                        osType = "Otros";
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return osType;
    }

    /**
     * Compila y retorna la información básica del host, incluyendo su dirección IP y nombre de host.
     * @param hostAddress La dirección IP del host.
     * @return Una lista de cadenas representando la información del host.
     */
    public List<String> getHostInfo(String hostAddress) {
        List<String> hostInfo = new ArrayList<>();
        String hostName = hostNames.getOrDefault(hostAddress, "Nombre desconocido");
        hostInfo.add("Host: " + hostAddress);
        hostInfo.add("Nombre del Host: " + hostName);

        return hostInfo;
    }

    /**
     * Exporta los resultados del escaneo de un host a un archivo HTML.
     * @param hostAddress La dirección IP del host cuyos detalles se exportarán.
     */
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