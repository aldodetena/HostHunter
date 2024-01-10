package NetworkScanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class ServiceIdentifier {

    public static void getServiceInfo(String host, int port, NetworkScanResult result) {
        Map<String, String> serviceInfo;

        // Identificar el servicio basado en el puerto
        switch (port) {
            case 21:
                // Protocolo FTP
                serviceInfo = identifyFTPService(host, port);
                break;
            case 22:
                // Protocolo SSH
                serviceInfo = identifySSHService(host, port);
                break;
            case 23:
                // Protocolo Telnet
                serviceInfo = identifyTelnetService(host, port);
                break;
            case 25:
                // Protocolo SMTP
                serviceInfo = identifySMTPService(host, port);
                break;
            case 53:
                // Protocolo DNS
                serviceInfo = identifyDNSService(host, port);
                break;
            case 67:
            case 68:
                // Protocolo DHCP
                serviceInfo = identifyDHCPService(host, port);
                break;
            case 69:
                // Protocolo TFTP
                serviceInfo = identifyTFTPService(host, port);
                break;
            case 80:
                // Protocolo HTTP
                serviceInfo = identifyHTTPService(host, port);
                break;
            case 110:
                // Protocolo POP3
                serviceInfo = identifyPOP3Service(host, port);
                break;
            case 111:
                // Protocolo RPC
                serviceInfo = identifyRPCService(host, port);
                break;
            case 119:
                // Protocolo NNTP
                serviceInfo = identifyFTPService(host, port);
                break;
            case 123:
                // Protocolo NTP
                serviceInfo = identifyFTPService(host, port);
                break;
            case 135:
                // Protocolo MSRPC
                serviceInfo = identifyFTPService(host, port);
                break;
            case 137:
            case 138:
            case 139:
                // Protocolo NetBIOS
                serviceInfo = identifyFTPService(host, port);
                break;
            case 143:
                // Protocolo IMAP
                serviceInfo = identifyFTPService(host, port);
                break;
            case 161:
            case 162:
                // Protocolo SNMP
                serviceInfo = identifyFTPService(host, port);
                break;
            case 179:
                // Protocolo BGP
                serviceInfo = identifyHTTPService(host, port);
                break;
            case 194:
                // Protocolo IRC
                serviceInfo = identifyHTTPService(host, port);
                break;
            case 220:
                // Protocolo IMAP3
                serviceInfo = identifyFTPService(host, port);
                break;
            case 389:
                // Protocolo LDAP
                serviceInfo = identifyFTPService(host, port);
                break;
            case 443:
                // Protocolo HTTPS
                serviceInfo = identifyHTTPSService(host, port);
                break;
            case 465:
                // Protocolo SMTPS
                serviceInfo = identifyFTPService(host, port);
                break;
            case 512:
            case 513:
            case 514:
                // Protocolos Rexec, Rlogin, Rsh
                serviceInfo = identifyFTPService(host, port);
                break;
            case 587:
                // Protocolo SMTP Alt
                serviceInfo = identifyFTPService(host, port);
                break;
            case 591:
                // Protocolo SMTP Alt
                serviceInfo = identifyFTPService(host, port);
                break;
            case 636:
                // Protocolo LDAPS
                serviceInfo = identifyFTPService(host, port);
                break;
            case 853:
                // Protocolo DNS sobre TLS
                serviceInfo = identifyFTPService(host, port);
                break;
            case 990:
                // Protocolo FTPS
                serviceInfo = identifyFTPService(host, port);
                break;
            case 993:
                // Protocolo IMAPS
                serviceInfo = identifyFTPService(host, port);
                break;
            case 995:
                // Protocolo POP3S
                serviceInfo = identifyFTPService(host, port);
                break;
            case 1194:
                // Puerto estandar OpenVPN
                serviceInfo = identifyFTPService(host, port);
                break;
            case 1723:
                // Puerto estandar VPN PPTP
                serviceInfo = identifyFTPService(host, port);
                break;
            case 1812:
                // Puerto estandar de autentificación RADIUS
                serviceInfo = identifyFTPService(host, port);
                break;
            case 1813:
                // Puerto estandar para el accounting de RADIUS
                serviceInfo = identifyFTPService(host, port);
                break;
            case 2049:
                // Protocolo NFS
                serviceInfo = identifyFTPService(host, port);
                break;
            case 2082:
            case 2083:
                // Puerto estandar para CPanel
                serviceInfo = identifyFTPService(host, port);
                break;
            case 3074:
                // Puerto estandar para Microsoft Xbox Live
                serviceInfo = identifyFTPService(host, port);
                break;
            case 3306:
                // Puerto estandar MYSQL
                serviceInfo = identifyFTPService(host, port);
                break;
            case 3389:
                // Puerto estandar RDP de Windows
                serviceInfo = identifyFTPService(host, port);
                break;
            case 4662:
            case 4672:
                // Puerto estandar eMule
                serviceInfo = identifyFTPService(host, port);
                break;
            case 4899:
                // Puerto estandar de Radmin
                serviceInfo = identifyFTPService(host, port);
                break;
            case 5000:
                // Protocolo UPnP
                serviceInfo = identifyFTPService(host, port);
                break;
            case 5400:
            case 5500:
            case 5600:
            case 5700:
            case 5800:
            case 5900:
                // Puertos estandar del VNC
                serviceInfo = identifyFTPService(host, port);
                break;
            case 6881:
            case 6969:
                // Puerto estandar de BitTorrent
                serviceInfo = identifyFTPService(host, port);
                break;
            case 8080:
                // Protocolo HTTP Alt
                serviceInfo = identifyFTPService(host, port);
                break;
            case 25565:
                // Puerto estandar de Minecraft
                serviceInfo = identifyFTPService(host, port);
                break;
            case 51400:
                // Puerto predeterminado de BitTorrent
                serviceInfo = identifyFTPService(host, port);
                break;
            
            default:
                serviceInfo = new HashMap<>();
                serviceInfo.put("Service", "Unknown");
                break;
        }
        result.addServiceDetails(host, port, serviceInfo);
    }

    // Función para identificar servicios FTP
    private static Map<String, String> identifyFTPService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
    
            // Leer la respuesta del servidor
            String response = in.readLine();
    
            // Los servidores FTP suelen empezar sus mensajes de bienvenida con "220"
            if (response.startsWith("220")) {
                serviceInfo.put("Service", "FTP");
                serviceInfo.put("Response", response);
            } else {
                serviceInfo.put("Service", "Unknown");
            }
        } catch (Exception e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", e.getMessage());
        }
        return serviceInfo;
    }

    // Función para identificar servicios SSH
    private static Map<String, String> identifySSHService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            // Establecer un tiempo de espera razonable para la respuesta
            socket.setSoTimeout(3000);
    
            // Leer la respuesta del servidor
            String response = in.readLine();
    
            // Los servidores SSH suelen responder con una cadena que incluye "SSH"
            if (response != null && response.contains("SSH")) {
                serviceInfo.put("Service", "SSH");
                serviceInfo.put("Response", response);
            } else {
                serviceInfo.put("Service", "Unknown");
            }
        } catch (Exception e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", e.getMessage());
        }
        return serviceInfo;
    }

    // Función para identificar servicios Telnet
    private static Map<String, String> identifyTelnetService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        try (Socket socket = new Socket()) {
            // Establecer un tiempo de espera razonable para la conexión
            socket.connect(new InetSocketAddress(host, port), 3000);
    
            // Si la conexión es exitosa, es probable que sea un servicio Telnet
            serviceInfo.put("Service", "Telnet");
            serviceInfo.put("Response", "Connected to Telnet service");
        } catch (IOException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", e.getMessage());
        }

        return serviceInfo;
    }

    public static Map<String, String> identifyDNSService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        try {
            // Crear un paquete de consulta DNS (esto es bastante simplificado)
            byte[] query = new byte[28]; // Un paquete DNS simple
            DatagramPacket packet = new DatagramPacket(query, query.length, InetAddress.getByName(host), port);
    
            // Enviar la consulta DNS
            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(3000); // Establecer un tiempo de espera
            socket.send(packet);
    
            // Esperar la respuesta
            DatagramPacket response = new DatagramPacket(new byte[512], 512);
            socket.receive(response);
    
            // Si recibimos una respuesta, asumimos que es un servicio DNS
            serviceInfo.put("Service", "DNS");
            serviceInfo.put("Response", "Received DNS response");
        } catch (IOException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", e.getMessage());
        }
        return serviceInfo;
    }

    // Función para identificar servicios SMTP
    private static Map<String, String> identifySMTPService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 3000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    
            // Leer la respuesta del servidor SMTP
            String response = in.readLine();
    
            // Los servidores SMTP típicamente comienzan su saludo con "220"
            if (response != null && response.startsWith("220")) {
                serviceInfo.put("Service", "SMTP");
                serviceInfo.put("Response", response);
            } else {
                serviceInfo.put("Service", "Unknown");
            }
        } catch (IOException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", e.getMessage());
        }
        return serviceInfo;
    }

    // Función para identificar servicios DHCP
    public static Map<String, String> identifyDHCPService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(3000); // Establecer un tiempo de espera
    
            // Crear un paquete DHCP DISCOVER (esto es altamente simplificado y no funcional)
            byte[] dhcpDiscoverMsg = new byte[240]; // Un paquete DHCP DISCOVER muy simplificado
            DatagramPacket packet = new DatagramPacket(dhcpDiscoverMsg, dhcpDiscoverMsg.length, InetAddress.getByName(host), port);
    
            // Enviar el paquete DHCP DISCOVER
            socket.send(packet);
    
            // Esperar la respuesta (DHCP OFFER)
            DatagramPacket response = new DatagramPacket(new byte[512], 512);
            socket.receive(response);
    
            // Si recibimos una respuesta, asumimos que es un servicio DHCP
            serviceInfo.put("Service", "DHCP");
            serviceInfo.put("Response", "Received DHCP response");
        } catch (IOException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
        return serviceInfo;
    }

    // Función para identificar servicios TFTP
    public static Map<String, String> identifyTFTPService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(3000); // Establecer un tiempo de espera
    
            // Crear un paquete TFTP RRQ (Read Request) muy simplificado
            byte[] rrqPacket = new byte[]{0, 1, 't', 'e', 's', 't', 0, 'o', 'c', 't', 'e', 't', 0};
            DatagramPacket packet = new DatagramPacket(rrqPacket, rrqPacket.length, InetAddress.getByName(host), port);
    
            // Enviar el paquete RRQ
            socket.send(packet);
    
            // Esperar la respuesta (puede ser un paquete de error o acuse de recibo)
            DatagramPacket response = new DatagramPacket(new byte[512], 512);
            socket.receive(response);
    
            // Si recibimos una respuesta, asumimos que es un servicio TFTP
            serviceInfo.put("Service", "TFTP");
            serviceInfo.put("Response", "Received TFTP response");
        } catch (SocketTimeoutException e) {
            serviceInfo.put("Service", "TFTP");
            serviceInfo.put("Response", "No response (TFTP might still be present)");
        } catch (IOException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
        return serviceInfo;
    }

    // Función para identificar servicios HTTP
    private static Map<String, String> identifyHTTPService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            // Enviar solicitud HTTP básica
            out.println("GET / HTTP/1.1");
            out.println("Host: " + host);
            out.println("Connection: Close");
            out.println();
    
            // Leer la respuesta
            String line;
            StringBuilder header = new StringBuilder();
            while ((line = in.readLine()) != null) {
                header.append(line).append("\n");
                if (line.isEmpty()) {
                    break; // Fin de los encabezados de la respuesta
                }
            }
    
            // Comprobar si la respuesta es válida para HTTP
            if (header.toString().startsWith("HTTP/")) {
                serviceInfo.put("Service", "HTTP");
                serviceInfo.put("Response", header.toString());
            } else {
                serviceInfo.put("Service", "Unknown");
            }
        } catch (Exception e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", e.getMessage());
        }
        return serviceInfo;
    }

    // Función para identificar servicios POP3
    public static Map<String, String> identifyPOP3Service(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
    
            socket.setSoTimeout(3000); // Establecer un tiempo de espera
    
            // Leer la respuesta del servidor POP3
            String response = in.readLine();
    
            // Los servidores POP3 suelen comenzar su saludo con "+OK"
            if (response != null && response.startsWith("+OK")) {
                serviceInfo.put("Service", "POP3");
                serviceInfo.put("Response", response);
            } else {
                serviceInfo.put("Service", "Unknown");
            }
        } catch (IOException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", e.getMessage());
        }
        return serviceInfo;
    }

    // Función para identificar servicios HTTPS
    private static Map<String, String> identifyHTTPSService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        try {
            // Crear un contexto SSL y un socket factory
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, null, null);
            SSLSocketFactory factory = context.getSocketFactory();

            // Crear un socket SSL y conectar
            try (SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Enviar solicitud HTTP básica
                out.println("GET / HTTP/1.1");
                out.println("Host: " + host);
                out.println("Connection: Close");
                out.println();

                // Leer la respuesta
                String line;
                StringBuilder header = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    header.append(line).append("\n");
                    if (line.isEmpty()) {
                        break; // Fin de los encabezados de la respuesta
                    }
                }

                // Comprobar si la respuesta es válida para HTTP (HTTPS en este caso)
                if (header.toString().startsWith("HTTP/")) {
                    serviceInfo.put("Service", "HTTPS");
                    serviceInfo.put("Response", header.toString());
                } else {
                    serviceInfo.put("Service", "Unknown");
                }
            }
        } catch (Exception e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", e.getMessage());
        }
        return serviceInfo;
    }
}