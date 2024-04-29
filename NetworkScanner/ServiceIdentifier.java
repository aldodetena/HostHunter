package NetworkScanner;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class ServiceIdentifier {

    public static void getServiceInfo(String host, int port, NetworkScanResult result) {
        Map<String, String> serviceInfo;
        boolean ssl;

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
                serviceInfo = identifyNNTPService(host, port);
                break;
            case 123:
                // Protocolo NTP
                serviceInfo = identifyNTPService(host, port);
                break;
            case 135:
                // Protocolo MSRPC
                serviceInfo = identifyMSRPCService(host, port);
                break;
            case 137:
            case 138:
            case 139:
                // Protocolo NetBIOS
                serviceInfo = identifyNetBIOSService(host, port);
                break;
            case 143:
                // Protocolo IMAP
                serviceInfo = identifyIMAPService(host, port);
                break;
            case 161:
            case 162:
                // Protocolo SNMP
                serviceInfo = identifySNMPService(host, port);
                break;
            case 179:
                // Protocolo BGP
                serviceInfo = identifyBGPService(host, port);
                break;
            case 194:
                // Protocolo IRC
                serviceInfo = identifyIRCService(host, port);
                break;
            case 220:
                // Protocolo IMAP3
                serviceInfo = identifyIMAP3Service(host, port);
                break;
            case 389:
                // Protocolo LDAP
                serviceInfo = identifyLDAPService(host, port);
                break;
            case 443:
                // Protocolo HTTPS
                serviceInfo = identifyHTTPSService(host, port);
                break;
            case 465:
                // Protocolo SMTPS
                serviceInfo = identifySMTPSService(host, port);
                break;
            case 512:
            case 513:
            case 514:
                // Protocolos Rexec, Rlogin, Rsh
                serviceInfo = identifyRemoteService(host, port);
                break;
            case 587:
                // Protocolo SMTP Alt
                serviceInfo = checkSMTPorSMTPSService(host, port);
                break;
            case 591:
                // Protocolo FileMaker
                serviceInfo = identifyFileMakerService(host, port);
                break;
            case 636:
                // Protocolo LDAPS
                serviceInfo = identifyLDAPSService(host, port);
                break;
            case 853:
                // Protocolo DoT
                serviceInfo = identifyDoTService(host, port);
                break;
            case 990:
                // Protocolo FTPS
                serviceInfo = identifyFTPSService(host, port);
                break;
            case 993:
                // Protocolo IMAPS
                serviceInfo = identifyIMAPSService(host, port);
                break;
            case 995:
                // Protocolo POP3S
                serviceInfo = identifyPOP3SService(host, port);
                break;
            case 1194:
                // Puerto estandar OpenVPN
                serviceInfo = identifyOpenVPNService(host, port);
                break;
            case 1723:
                // Puerto estandar VPN PPTP
                serviceInfo = identifyPPTPService(host, port);
                break;
            case 1812:
                // Puerto estandar de autentificación RADIUS
                serviceInfo = identifyRADIUSService(host, port);
                break;
            case 1813:
                // Puerto estandar para el accounting de RADIUS
                serviceInfo = identifyRADIUSAccountingService(host, port);
                break;
            case 1900:
                // Protocolo UPnP
                serviceInfo = identifyUPnPService(host, port);
                break;
            case 2049:
                // Protocolo NFS
                serviceInfo = identifyNFSService(host, port);
                break;
            case 2082:
                // Puerto estandar para CPanel
                ssl = false;
                serviceInfo = identifyCPanelService(host, port, ssl);
            case 2083:
                // Puerto estandar para CPanel sobre TLS
                ssl = true;
                serviceInfo = identifyCPanelService(host, port, ssl);
                break;
            case 3074:
                // Puerto estandar para Microsoft Xbox Live
                serviceInfo = identifyXboxLiveService(host, port);
                break;
            case 3306:
                // Puerto estandar MYSQL
                serviceInfo = identifyMySQLService(host, port);
                break;
            case 3389:
                // Puerto estandar RDP de Windows
                serviceInfo = identifyRDPService(host, port);
                break;
            case 4662:
                // Puerto estandar eMule TCP
                serviceInfo = identifyeMuleTCPService(host, port);
                break;
            case 4672:
                // Puerto estandar eMule UDP
                serviceInfo = identifyeMuleUDPService(host, port);
                break;
            case 4899:
                // Puerto estandar de Radmin
                serviceInfo = identifyRadminService(host, port);
                break;
            case 5400:
            case 5500:
            case 5600:
            case 5700:
            case 5800:
            case 5900:
                // Puertos estandar del VNC
                serviceInfo = identifyVNCService(host, port);
                break;
            case 6881:
            case 6889:
                // Puerto estandar de BitTorrent
                serviceInfo = identifyBitTorrentService(host, port);
                break;
            case 8080:
                // Protocolo HTTP Alt
                serviceInfo = identifyHTTPService(host, port);
                break;
            case 25565:
                // Puerto estandar de Minecraft
                serviceInfo = identifyMinecraftService(host, port);
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

    public static Map<String, String> identifyDNSService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        DatagramSocket socket = null;
        try {
            // Crear un paquete de consulta DNS (esto es bastante simplificado)
            byte[] query = new byte[28]; // Un paquete DNS simple
            DatagramPacket packet = new DatagramPacket(query, query.length, InetAddress.getByName(host), port);
    
            // Enviar la consulta DNS
            socket = new DatagramSocket();
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
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
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

    // Función para identificar servicios RPC
    public static Map<String, String> identifyRPCService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        DatagramSocket socket = null;
        try {
            // Crear el socket UDP
            socket = new DatagramSocket();
            socket.setSoTimeout(3000); // Establecer un tiempo de espera de 3 segundos

            // Enviamos un paquete básico
            byte[] sendData = "Hello RPC".getBytes();
            InetAddress address = InetAddress.getByName(host);

            // Enviar el paquete
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
            socket.send(sendPacket);

            // Preparar para recibir la respuesta
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            try {
                socket.receive(receivePacket);  // Esperar la respuesta
                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                serviceInfo.put("Service", "RPC");
                serviceInfo.put("Response", "Received response from RPC service: " + response.trim());
            } catch (SocketTimeoutException e) {
                serviceInfo.put("Service", "RPC");
                serviceInfo.put("Response", "No response from RPC service, port might be open but no RPC");
            }
        } catch (UnknownHostException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "Unknown host: " + e.getMessage());
        } catch (SocketException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "Socket error: " + e.getMessage());
        } catch (IOException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "IO error: " + e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
        return serviceInfo;
    }

    // Función para identificar servicios NNTP
    public static Map<String, String> identifyNNTPService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
    
            socket.setSoTimeout(3000); // Establecer un tiempo de espera
    
            // Leer la respuesta del servidor NNTP
            String response = in.readLine();
    
            // Los servidores NNTP suelen comenzar su mensaje de bienvenida con un código numérico
            if (response != null && response.matches("^[12]\\d\\d.*")) {
                serviceInfo.put("Service", "NNTP");
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

    // Función para identificar servicios NTP
    public static Map<String, String> identifyNTPService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(3000); // Establecer un tiempo de espera
    
            // Crear un mensaje NTP básico
            byte[] ntpRequest = new byte[48];
            ntpRequest[0] = 27; // NTP mode 3 (cliente) y versión 3
    
            // Enviar la solicitud NTP
            DatagramPacket requestPacket = new DatagramPacket(ntpRequest, ntpRequest.length, InetAddress.getByName(host), port);
            socket.send(requestPacket);
    
            // Intentar recibir la respuesta
            DatagramPacket responsePacket = new DatagramPacket(new byte[48], 48);
            socket.receive(responsePacket);
    
            // Si recibimos una respuesta, asumimos que es un servicio NTP
            serviceInfo.put("Service", "NTP");
            serviceInfo.put("Response", "Received NTP response");
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

    // Función para identificar servicios MSRPC
    public static Map<String, String> identifyMSRPCService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        DatagramSocket socket = null;
        try {
            // Crear el socket UDP
            socket = new DatagramSocket();
            socket.setSoTimeout(3000); // Establecer un tiempo de espera de 3 segundos

            // Enviamos un paquete básico
            byte[] sendData = "Hello MSRPC".getBytes();
            InetAddress address = InetAddress.getByName(host);

            // Enviar el paquete
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
            socket.send(sendPacket);

            // Preparar para recibir la respuesta
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            try {
                socket.receive(receivePacket);  // Esperar la respuesta
                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                serviceInfo.put("Service", "MSRPC");
                serviceInfo.put("Response", "Received response from MSRPC service: " + response.trim());
            } catch (SocketTimeoutException e) {
                serviceInfo.put("Service", "MSRPC");
                serviceInfo.put("Response", "No response from MSRPC service, port might be open but no MSRPC");
            }
        } catch (UnknownHostException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "Unknown host: " + e.getMessage());
        } catch (SocketException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "Socket error: " + e.getMessage());
        } catch (IOException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "IO error: " + e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
        return serviceInfo;
    }

    // Función para identificar servicios NetBIOS
    public static Map<String, String> identifyNetBIOSService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        DatagramSocket socket = null;
        try {
            // Crear el socket UDP
            socket = new DatagramSocket();
            socket.setSoTimeout(3000); // Establecer un tiempo de espera de 3 segundos

            // Mensaje de solicitud NetBIOS (esto es simplificado y debería ajustarse según la implementación real)
            byte[] sendData = new byte[50]; // Buffer para el paquete NetBIOS Name Service
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(host), port);
            socket.send(sendPacket);

            // Preparar para recibir la respuesta
            byte[] receiveData = new byte[512]; // Suficiente para una respuesta típica de NetBIOS
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            try {
                socket.receive(receivePacket);  // Esperar la respuesta
                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                serviceInfo.put("Service", "NetBIOS");
                serviceInfo.put("Response", "Received response from NetBIOS service: " + response.trim());
            } catch (SocketTimeoutException e) {
                serviceInfo.put("Service", "NetBIOS");
                serviceInfo.put("Response", "No response from NetBIOS service, port might be open but no NetBIOS");
            }
        } catch (UnknownHostException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "Unknown host: " + e.getMessage());
        } catch (SocketException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "Socket error: " + e.getMessage());
        } catch (IOException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "IO error: " + e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
        return serviceInfo;
    }

    // Función para identificar servicios IMAP
    public static Map<String, String> identifyIMAPService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
    
            socket.setSoTimeout(3000); // Establecer un tiempo de espera
    
            // Leer la respuesta del servidor IMAP
            String response = in.readLine();
    
            // Los servidores IMAP suelen comenzar su mensaje de bienvenida con "* OK"
            if (response != null && response.startsWith("* OK")) {
                serviceInfo.put("Service", "IMAP");
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

    // Función para identificar servicios SNMP
    public static Map<String, String> identifySNMPService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(3000); // Establecer un tiempo de espera
    
            // Crear un mensaje SNMP GET básico (esto es altamente simplificado)
            byte[] snmpRequest = new byte[] { /* ... datos del paquete SNMP ... */ };
            DatagramPacket requestPacket = new DatagramPacket(snmpRequest, snmpRequest.length, InetAddress.getByName(host), port);
    
            // Enviar la solicitud SNMP
            socket.send(requestPacket);
    
            // Intentar recibir la respuesta
            DatagramPacket responsePacket = new DatagramPacket(new byte[512], 512);
            socket.receive(responsePacket);
    
            // Si recibimos una respuesta, asumimos que es un servicio SNMP
            serviceInfo.put("Service", "SNMP");
            serviceInfo.put("Response", "Received SNMP response");
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

    // Función para identificar servicios BGP
    public static Map<String, String> identifyBGPService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        try (Socket socket = new Socket(host, port)) {
            // Establecer un tiempo de espera
            socket.setSoTimeout(3000);
    
            // Si la conexión es exitosa, asumimos que el puerto está abierto
            serviceInfo.put("Service", "BGP");
            serviceInfo.put("Response", "Port is open, might be BGP");
        } catch (Exception e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", e.getMessage());
        }
        return serviceInfo;
    }

    // Función para identificar servicios IRC
    public static Map<String, String> identifyIRCService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
    
            socket.setSoTimeout(3000); // Establecer un tiempo de espera
    
            // Leer la respuesta del servidor IRC
            String response = in.readLine();
    
            // Los servidores IRC suelen comenzar su mensaje con un código numérico o con "NOTICE"
            if (response != null && (response.matches("^:\\S+ \\d{3} .*") || response.startsWith("NOTICE"))) {
                serviceInfo.put("Service", "IRC");
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

    // Función para identificar servicios IMAP3
    public static Map<String, String> identifyIMAP3Service(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
    
            socket.setSoTimeout(3000); // Establecer un tiempo de espera
    
            // Leer la respuesta del servidor IMAP3
            String response = in.readLine();
    
            // Los servidores IMAP3 suelen comenzar su mensaje de bienvenida con "* OK"
            if (response != null && response.startsWith("* OK")) {
                serviceInfo.put("Service", "IMAP3");
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

    // Función para identificar servicios LDAP
    public static Map<String, String> identifyLDAPService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        try (Socket socket = new Socket(host, port)) {
            // Establecer un tiempo de espera
            socket.setSoTimeout(3000);
    
            // Si la conexión es exitosa, asumimos que el puerto está abierto
            serviceInfo.put("Service", "LDAP");
            serviceInfo.put("Response", "Port is open, might be LDAP");
        } catch (Exception e) {
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

    // Función para identificar servicios SMTPS
    public static Map<String, String> identifySMTPSService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        SSLSocket socket = null;
        try {
            // Crear un socket SSL
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) factory.createSocket(host, port);
    
            // Establecer un tiempo de espera
            socket.setSoTimeout(3000);
    
            // Iniciar la sesión SSL
            socket.startHandshake();
    
            // Leer la respuesta del servidor SMTPS
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = in.readLine();
    
            // Los servidores SMTPS suelen comenzar su saludo con "220"
            if (response != null && response.startsWith("220")) {
                serviceInfo.put("Service", "SMTPS");
                serviceInfo.put("Response", response);
            } else {
                serviceInfo.put("Service", "Unknown");
            }
        } catch (IOException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // Manejo de la excepción al cerrar el socket
                }
            }
        }
        return serviceInfo;
    }

    // Función para identificar servicios Rexec, Rlogin y Rsh
    public static Map<String, String> identifyRemoteService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(3000); // Establecer un tiempo de espera
    
            String serviceName = "";
            switch (port) {
                case 512:
                    serviceName = "Rexec";
                    break;
                case 513:
                    serviceName = "Rlogin";
                    break;
                case 514:
                    serviceName = "Rsh";
                    break;
            }
    
            // Si la conexión es exitosa, asumimos que el puerto está abierto y el servicio podría estar activo
            serviceInfo.put("Service", serviceName);
            serviceInfo.put("Response", "Port is open, might be " + serviceName);
        } catch (Exception e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", e.getMessage());
        }
        return serviceInfo;
    }

    // Función para identificar servicios STMP o SMTPS en el puerto 587
    public static Map<String, String> checkSMTPorSMTPSService(String host, int port) {
        Map<String, String> serviceInfo = identifySMTPSService(host, port);
    
        if (serviceInfo.get("Service").equals("Error")) {
            // Si hay un error al intentar SMTPS, intentamos SMTP
            serviceInfo = identifySMTPService(host, port);
        }
    
        // Actualizar el objeto NetworkScanResult con la información del servicio
        return serviceInfo;
    }

    // Función para identificar servicios FileMaker
    public static Map<String, String> identifyFileMakerService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        DatagramSocket socket = null;
        try {
            // Crear el socket UDP
            socket = new DatagramSocket();
            socket.setSoTimeout(3000); // Establecer un tiempo de espera de 3 segundos

            // Suponer que enviamos un paquete básico
            byte[] sendData = "Hello FileMaker".getBytes();
            InetAddress address = InetAddress.getByName(host);

            // Enviar el paquete
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
            socket.send(sendPacket);

            // Preparar para recibir la respuesta
            byte[] receiveData = new byte[512];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            try {
                socket.receive(receivePacket);  // Esperar la respuesta
                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                serviceInfo.put("Service", "FileMaker");
                serviceInfo.put("Response", "Received response from FileMaker service: " + response.trim());
            } catch (SocketTimeoutException e) {
                serviceInfo.put("Service", "FileMaker");
                serviceInfo.put("Response", "No response from FileMaker service, port might be open but no FileMaker");
            }
        } catch (UnknownHostException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "Unknown host: " + e.getMessage());
        } catch (SocketException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "Socket error: " + e.getMessage());
        } catch (IOException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "IO error: " + e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
        return serviceInfo;
    }

    // Función para identificar servicios LDAPS
    public static Map<String, String> identifyLDAPSService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        SSLSocket socket = null;
        try {
            // Crear un socket SSL
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) factory.createSocket(host, port);
    
            // Establecer un tiempo de espera
            socket.setSoTimeout(3000);
    
            // Iniciar la sesión SSL
            socket.startHandshake();
    
            // Leer la respuesta del servidor LDAPS
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = in.readLine();
    
            // Verificar si hay alguna respuesta
            if (response != null) {
                serviceInfo.put("Service", "LDAPS");
                serviceInfo.put("Response", response);
            } else {
                serviceInfo.put("Service", "Unknown");
            }
        } catch (IOException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // Manejo de la excepción al cerrar el socket
                }
            }
        }
        return serviceInfo;
    }

    // Función para identificar servicios DoT
    public static Map<String, String> identifyDoTService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        SSLSocket socket = null;
        try {
            // Crear un socket SSL
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) factory.createSocket(host, port);
    
            // Establecer un tiempo de espera
            socket.setSoTimeout(3000);
    
            // Iniciar la sesión TLS
            socket.startHandshake();
    
            // Enviar una solicitud DNS y recibir la respuesta sería el siguiente paso,
            // pero esto requiere una implementación más compleja y específica.
    
            // Si se establece la conexión sin errores, asumimos que DoT podría estar presente
            serviceInfo.put("Service", "DNS over TLS (DoT)");
            serviceInfo.put("Response", "TLS connection established");
        } catch (IOException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // Manejo de la excepción al cerrar el socket
                }
            }
        }
        return serviceInfo;
    }

    // Función para identificar servicios FTPS
    public static Map<String, String> identifyFTPSService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        SSLSocket socket = null;
        try {
            // Crear un socket SSL
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) factory.createSocket(host, port);
    
            // Establecer un tiempo de espera
            socket.setSoTimeout(3000);
    
            // Iniciar la sesión SSL
            socket.startHandshake();
    
            // Comunicación con el servidor FTPS
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
    
            // Leer la respuesta inicial del servidor
            String response = in.readLine();
            if (response != null && response.startsWith("220")) {
                // Enviar un comando básico de FTP (por ejemplo, STAT) y leer la respuesta
                out.println("STAT");
                String statResponse = in.readLine();
    
                serviceInfo.put("Service", "FTPS");
                serviceInfo.put("Response", response);
                serviceInfo.put("STAT Response", statResponse != null ? statResponse : "No STAT response");
            } else {
                serviceInfo.put("Service", "Unknown");
            }
        } catch (IOException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // Manejo de la excepción al cerrar el socket
                }
            }
        }
        return serviceInfo;
    }

    // Función para identificar servicios IMAPS
    public static Map<String, String> identifyIMAPSService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        SSLSocket socket = null;
        try {
            // Crear un socket SSL
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) factory.createSocket(host, port);
    
            // Establecer un tiempo de espera
            socket.setSoTimeout(3000);
    
            // Iniciar la sesión SSL
            socket.startHandshake();
    
            // Leer la respuesta del servidor IMAPS
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = in.readLine();
    
            // Los servidores IMAPS suelen comenzar su mensaje de bienvenida con "* OK"
            if (response != null && response.startsWith("* OK")) {
                serviceInfo.put("Service", "IMAPS");
                serviceInfo.put("Response", response);
            } else {
                serviceInfo.put("Service", "Unknown");
            }
        } catch (IOException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // Manejo de la excepción al cerrar el socket
                }
            }
        }
        return serviceInfo;
    }

    // Función para identificar servicios POP3S
    public static Map<String, String> identifyPOP3SService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        SSLSocket socket = null;
        try {
            // Crear un socket SSL
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) factory.createSocket(host, port);
    
            // Establecer un tiempo de espera
            socket.setSoTimeout(3000);
    
            // Iniciar la sesión SSL
            socket.startHandshake();
    
            // Leer la respuesta del servidor POP3S
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = in.readLine();
    
            // Los servidores POP3S suelen comenzar su mensaje de bienvenida con "+OK"
            if (response != null && response.startsWith("+OK")) {
                serviceInfo.put("Service", "POP3S");
                serviceInfo.put("Response", response);
            } else {
                serviceInfo.put("Service", "Unknown");
            }
        } catch (IOException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // Manejo de la excepción al cerrar el socket
                }
            }
        }
        return serviceInfo;
    }

    // Función para identificar servicios OpenVPN
    public static Map<String, String> identifyOpenVPNService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        DatagramSocket socket = null;
        try {
            // Crear el socket UDP
            socket = new DatagramSocket();
            socket.setSoTimeout(3000); // Establecer un tiempo de espera de 3 segundos

            // Enviamos un paquete que podría ser reconocido por un servidor OpenVPN
            // Este es un paquete de handshake inicial muy básico de OpenVPN
            byte[] sendData = {0x38, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
            InetAddress address = InetAddress.getByName(host);

            // Enviar el paquete
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
            socket.send(sendPacket);

            // Preparar para recibir la respuesta
            byte[] receiveData = new byte[512]; // Tamaño suficiente para una respuesta típica de OpenVPN
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            try {
                socket.receive(receivePacket);  // Esperar la respuesta
                // Si recibimos respuesta, hay alta probabilidad de que sea OpenVPN
                serviceInfo.put("Service", "OpenVPN");
                serviceInfo.put("Response", "Received response from OpenVPN service: possibly active");
            } catch (SocketTimeoutException e) {
                serviceInfo.put("Service", "OpenVPN");
                serviceInfo.put("Response", "No response from OpenVPN service, port might be open but not responding");
            }
        } catch (UnknownHostException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "Unknown host: " + e.getMessage());
        } catch (SocketException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "Socket error: " + e.getMessage());
        } catch (IOException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "IO error: " + e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
        return serviceInfo;
    }

    // Función para identificar servicios PPTP
    public static Map<String, String> identifyPPTPService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        try (Socket socket = new Socket(host, port)) {
            // Establecer un tiempo de espera
            socket.setSoTimeout(3000);
    
            // Si la conexión es exitosa, asumimos que el puerto está abierto
            serviceInfo.put("Service", "PPTP");
            serviceInfo.put("Response", "Port is open, might be PPTP");
        } catch (Exception e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", e.getMessage());
        }
        return serviceInfo;
    }

    // Función para identificar servicios RADIUS
    public static Map<String, String> identifyRADIUSService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        DatagramSocket socket = null;
        try {
            // Crear el socket UDP
            socket = new DatagramSocket();
            socket.setSoTimeout(3000); // Establecer un tiempo de espera de 3 segundos

            // Construir un paquete RADIUS básico de Access-Request
            byte[] sendData = new byte[20];
            sendData[0] = 0x01; // Código de Access-Request
            sendData[1] = 0x01; // Identificador
            sendData[2] = 0x00; // Longitud (alta)
            sendData[3] = (byte) sendData.length; // Longitud (baja)

            InetAddress address = InetAddress.getByName(host);

            // Enviar el paquete
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
            socket.send(sendPacket);

            // Preparar para recibir la respuesta
            byte[] receiveData = new byte[512];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            try {
                socket.receive(receivePacket);  // Esperar la respuesta
                serviceInfo.put("Service", "RADIUS");
                serviceInfo.put("Response", "Received response from RADIUS service: possibly active");
            } catch (SocketTimeoutException e) {
                serviceInfo.put("Service", "RADIUS");
                serviceInfo.put("Response", "No response from RADIUS service, port might be open but not responding");
            }
        } catch (UnknownHostException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "Unknown host: " + e.getMessage());
        } catch (SocketException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "Socket error: " + e.getMessage());
        } catch (IOException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "IO error: " + e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
        return serviceInfo;
    }

    // Función para identificar servicios RADIUS accounting
    public static Map<String, String> identifyRADIUSAccountingService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(3000); // Establecer un tiempo de espera
    
            // Crear un paquete RADIUS de prueba (solicitud de contabilidad)
            byte[] radiusRequest = new byte[20]; // Un paquete RADIUS simplificado
            radiusRequest[0] = 4; // Tipo de mensaje (Solicitud de Contabilidad)
            radiusRequest[1] = 1; // Identificador
            // Los siguientes bytes incluirían la longitud y los atributos, pero se omiten para simplificar
    
            DatagramPacket packet = new DatagramPacket(radiusRequest, radiusRequest.length, InetAddress.getByName(host), port);
            socket.send(packet);
    
            // Intentar recibir la respuesta
            byte[] responseBuf = new byte[512];
            DatagramPacket response = new DatagramPacket(responseBuf, responseBuf.length);
            socket.receive(response);
    
            // Si recibimos una respuesta, asumimos que es un servicio de contabilidad RADIUS
            serviceInfo.put("Service", "RADIUS Accounting");
            serviceInfo.put("Response", "Received RADIUS Accounting response");
        } catch (SocketTimeoutException e) {
            serviceInfo.put("Service", "RADIUS Accounting");
            serviceInfo.put("Response", "No response (RADIUS Accounting might still be present)");
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

    // Función para identificar servicios NFS
    public static Map<String, String> identifyNFSService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        DatagramSocket socket = null;
        try {
            // Crear el socket UDP
            socket = new DatagramSocket();
            socket.setSoTimeout(3000); // Establecer un tiempo de espera de 3 segundos

            // Crear un paquete NFS básico de solicitud
            // Por simplificación, se envía un paquete vacío como "ping"
            byte[] sendData = new byte[10]; // Esto no es un paquete NFS válido, solo para propósitos de prueba
            InetAddress address = InetAddress.getByName(host);

            // Enviar el paquete
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
            socket.send(sendPacket);

            // Preparar para recibir la respuesta
            byte[] receiveData = new byte[512]; // Tamaño suficiente para una respuesta típica
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            try {
                socket.receive(receivePacket);  // Esperar la respuesta
                serviceInfo.put("Service", "NFS");
                serviceInfo.put("Response", "Received response from NFS service: possibly active");
            } catch (SocketTimeoutException e) {
                serviceInfo.put("Service", "NFS");
                serviceInfo.put("Response", "No response from NFS service, port might be open but not responding");
            }
        } catch (UnknownHostException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "Unknown host: " + e.getMessage());
        } catch (SocketException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "Socket error: " + e.getMessage());
        } catch (IOException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "IO error: " + e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
        return serviceInfo;
    }

    // Función para identificar servicios CPanel
    public static Map<String, String> identifyCPanelService(String host, int port, boolean useSSL) {
        Map<String, String> serviceInfo = new HashMap<>();
        Socket socket = null;
        BufferedReader in = null;
    
        try {
            if (useSSL) {
                // Crear un socket SSL para conexiones seguras
                SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                socket = sslSocketFactory.createSocket(host, port);
            } else {
                // Crear un socket normal para conexiones no seguras
                socket = new Socket(host, port);
            }
    
            socket.setSoTimeout(3000); // Establecer un tiempo de espera
    
            // Establecer el flujo de entrada para leer la respuesta
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    
            // Leer la respuesta inicial
            String response = in.readLine();
    
            // Verificar si la respuesta contiene indicativos de cPanel
            if (response != null && response.contains("cPanel")) {
                serviceInfo.put("Service", "cPanel");
                serviceInfo.put("Response", response);
            } else {
                serviceInfo.put("Service", "Unknown or not cPanel");
            }
        } catch (IOException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", e.getMessage());
        } finally {
            // Cerrar el socket y el BufferedReader
            try {
                if (in != null) {
                    in.close();
                }
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                // Manejo de la excepción al cerrar recursos
            }
        }
        return serviceInfo;
    }

    // Función para identificar servicios XBox Live
    public static Map<String, String> identifyXboxLiveService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        DatagramSocket socket = null;
        try {
            // Crear el socket UDP
            socket = new DatagramSocket();
            socket.setSoTimeout(3000); // Establecer un tiempo de espera de 3 segundos

            // Enviar un paquete UDP básico al host y puerto especificado
            byte[] sendData = "Hello Xbox Live".getBytes();
            InetAddress address = InetAddress.getByName(host);

            // Enviar el paquete
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
            socket.send(sendPacket);

            // Preparar para recibir la respuesta
            byte[] receiveData = new byte[512]; // Tamaño suficiente para una respuesta típica
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            try {
                socket.receive(receivePacket);  // Esperar la respuesta
                serviceInfo.put("Service", "Xbox Live");
                serviceInfo.put("Response", "Received response from Xbox Live service: possibly active");
            } catch (SocketTimeoutException e) {
                serviceInfo.put("Service", "Xbox Live");
                serviceInfo.put("Response", "No response from Xbox Live service, port might be open but not responding");
            }
        } catch (UnknownHostException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "Unknown host: " + e.getMessage());
        } catch (SocketException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "Socket error: " + e.getMessage());
        } catch (IOException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "IO error: " + e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
        return serviceInfo;
    }

    // Función para identificar servicios MYSQL
    public static Map<String, String> identifyMySQLService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
    
            socket.setSoTimeout(3000); // Establecer un tiempo de espera
    
            // Leer la respuesta inicial del servidor MySQL
            String response = in.readLine();
    
            // Los servidores MySQL suelen enviar información de versión al principio
            if (response != null && response.contains("MySQL")) {
                serviceInfo.put("Service", "MySQL");
                serviceInfo.put("Response", response);
            } else {
                serviceInfo.put("Service", "Unknown or not MySQL");
            }
        } catch (IOException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", e.getMessage());
        }
        return serviceInfo;
    }

    // Función para identificar servicios RDP
    public static Map<String, String> identifyRDPService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        try (Socket socket = new Socket(host, port)) {
            // Establecer un tiempo de espera
            socket.setSoTimeout(3000);
    
            // Si la conexión es exitosa, asumimos que el puerto está abierto
            serviceInfo.put("Service", "RDP");
            serviceInfo.put("Response", "Port is open, might be RDP");
        } catch (Exception e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", e.getMessage());
        }
        return serviceInfo;
    }

    // Función para identificar servicios eMule TCP
    public static Map<String, String> identifyeMuleTCPService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        try (Socket socket = new Socket(host, port)) {
            // Establecer un tiempo de espera
            socket.setSoTimeout(3000);
    
            // Si la conexión es exitosa, asumimos que el puerto está abierto
            serviceInfo.put("Service", "eMule TCP");
            serviceInfo.put("Response", "TCP Port is open, might be eMule");
        } catch (Exception e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", e.getMessage());
        }
        return serviceInfo;
    }

    // Función para identificar servicios eMule UDP
    public static Map<String, String> identifyeMuleUDPService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        DatagramSocket socket = null;
        try {
            // Crear el socket UDP
            socket = new DatagramSocket();
            socket.setSoTimeout(3000); // Establecer un tiempo de espera de 3 segundos

            // Crear un paquete UDP básico. En un escenario real, debería contener un mensaje de protocolo eMule adecuado.
            byte[] sendData = "eMule ping".getBytes(); // Este es un mensaje simplificado y no un mensaje del protocolo eMule real.
            InetAddress address = InetAddress.getByName(host);

            // Enviar el paquete
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
            socket.send(sendPacket);

            // Preparar para recibir la respuesta
            byte[] receiveData = new byte[512];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            try {
                socket.receive(receivePacket);  // Esperar la respuesta
                serviceInfo.put("Service", "eMule");
                serviceInfo.put("Response", "Received response from eMule service: possibly active");
            } catch (SocketTimeoutException e) {
                serviceInfo.put("Service", "eMule");
                serviceInfo.put("Response", "No response from eMule service, port might be open but not responding");
            }
        } catch (UnknownHostException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "Unknown host: " + e.getMessage());
        } catch (SocketException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "Socket error: " + e.getMessage());
        } catch (IOException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "IO error: " + e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
        return serviceInfo;
    }

    // Función para identificar servicios Radmin
    public static Map<String, String> identifyRadminService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        try (Socket socket = new Socket(host, port)) {
            // Establecer un tiempo de espera
            socket.setSoTimeout(3000);
    
            // Si la conexión es exitosa, asumimos que el puerto está abierto
            serviceInfo.put("Service", "Radmin");
            serviceInfo.put("Response", "Port is open, might be Radmin");
        } catch (Exception e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", e.getMessage());
        }
        return serviceInfo;
    }

    // Función para identificar servicios UPnP
    public static Map<String, String> identifyUPnPService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(3000);
    
            // Mensaje de descubrimiento SSDP
            String message = "M-SEARCH * HTTP/1.1\r\n" +
                             "HOST: 239.255.255.250:1900\r\n" +
                             "MAN: \"ssdp:discover\"\r\n" +
                             "MX: 1\r\n" +
                             "ST: ssdp:all\r\n" + // Buscar todos los dispositivos y servicios
                             "\r\n";
            byte[] msgBytes = message.getBytes();
    
            // Enviar mensaje de descubrimiento SSDP
            InetAddress group = InetAddress.getByName("239.255.255.250");
            DatagramPacket packet = new DatagramPacket(msgBytes, msgBytes.length, group, 1900);
            socket.send(packet);
    
            // Esperar respuestas
            while (true) {
                DatagramPacket response = new DatagramPacket(new byte[1024], 1024);
                socket.receive(response);
    
                String responseData = new String(response.getData(), 0, response.getLength());
                if (responseData.contains("UPnP")) {
                    serviceInfo.put("Service", "UPnP");
                    serviceInfo.put("Response", responseData);
                    break;
                }
            }
        } catch (SocketTimeoutException e) {
            serviceInfo.put("Service", "UPnP");
            serviceInfo.put("Response", "No response (UPnP might still be present)");
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

    // Función para identificar servicios VNC
    public static Map<String, String> identifyVNCService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
    
            socket.setSoTimeout(3000); // Establecer un tiempo de espera
    
            // Leer la respuesta inicial del servidor VNC
            String response = in.readLine();
    
            // Los servidores VNC suelen enviar la versión del protocolo RFB al principio
            if (response != null && response.contains("RFB")) {
                serviceInfo.put("Service", "VNC");
                serviceInfo.put("Response", response);
            } else {
                serviceInfo.put("Service", "Unknown or not VNC");
            }
        } catch (IOException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", e.getMessage());
        }
        return serviceInfo;
    }

    // Función para identificar servicios BitTorrent
    public static Map<String, String> identifyBitTorrentService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(3000); // Establecer un tiempo de espera de 3 segundos

            // Preparar un paquete de solicitud de connect para un tracker UDP
            ByteBuffer sendData = ByteBuffer.allocate(16);
            sendData.putLong(0x41727101980L); // connection_id mágico para la conexión inicial
            sendData.putInt(0); // action (0 = connect)
            sendData.putInt(new Random().nextInt()); // transaction_id

            InetAddress address = InetAddress.getByName(host);

            // Enviar el paquete
            DatagramPacket sendPacket = new DatagramPacket(sendData.array(), sendData.position(), address, port);
            socket.send(sendPacket);

            // Preparar para recibir la respuesta
            byte[] receiveData = new byte[512];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            try {
                socket.receive(receivePacket);  // Esperar la respuesta
                ByteBuffer receiveBuffer = ByteBuffer.wrap(receivePacket.getData());
                int action = receiveBuffer.getInt();
                int transactionId = receiveBuffer.getInt();

                if (action == 0 && transactionId == sendData.getInt(12)) { // Verifica action y transaction_id
                    serviceInfo.put("Service", "BitTorrent Tracker");
                    serviceInfo.put("Response", "Received valid response from BitTorrent Tracker: service is active");
                } else {
                    serviceInfo.put("Service", "BitTorrent Tracker");
                    serviceInfo.put("Response", "Received invalid response from BitTorrent Tracker");
                }
            } catch (SocketTimeoutException e) {
                serviceInfo.put("Service", "BitTorrent Tracker");
                serviceInfo.put("Response", "No response from BitTorrent Tracker, port might be open but not responding");
            }
        } catch (UnknownHostException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "Unknown host: " + e.getMessage());
        } catch (SocketException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "Socket error: " + e.getMessage());
        } catch (IOException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", "IO error: " + e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
        return serviceInfo;
    }

    // Función para identificar servicios Minecraft
    public static Map<String, String> identifyMinecraftService(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();
        try (Socket socket = new Socket(host, port);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream())) {

            socket.setSoTimeout(3000);

            // Enviar handshake y status request
            sendHandshake(out, host, port);
            sendStatusRequest(out);

            // Leer la respuesta del servidor
            String response = readResponse(in);
            if (response != null && response.contains("minecraft")) {
                serviceInfo.put("Service", "Minecraft");
                serviceInfo.put("Response", response);
            } else {
                serviceInfo.put("Service", "Unknown or not Minecraft");
            }
        } catch (IOException e) {
            serviceInfo.put("Service", "Error");
            serviceInfo.put("ErrorMessage", e.getMessage());
        }
        return serviceInfo;
    }

    // Funciones relacionadas con la identificación de servicios de Minecraft
    private static void sendHandshake(DataOutputStream out, String host, int port) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream handshake = new DataOutputStream(buffer);

        handshake.writeByte(0x00); // ID del paquete de handshake
        writeVarInt(handshake, -1); // Versión del protocolo ( -1 para la versión más reciente)
        writeVarInt(handshake, host.length()); // Longitud del nombre del host
        handshake.writeBytes(host); // Nombre del host
        handshake.writeInt(port); // Puerto
        writeVarInt(handshake, 1); // Estado siguiente (1 para estado)

        writeVarInt(out, buffer.size()); // Preparar la longitud del paquete
        out.write(buffer.toByteArray()); // Enviar el paquete de handshake
    }

    private static void writeVarInt(DataOutputStream out, int value) throws IOException {
        while ((value & -128) != 0) {
            out.writeByte(value & 127 | 128);
            value >>>= 7;
        }
        out.writeByte(value);
    }

    private static void sendStatusRequest(DataOutputStream out) throws IOException {
        out.writeByte(0x01); // Tamaño del paquete
        out.writeByte(0x00); // ID del paquete de solicitud de estado
    }

    private static String readResponse(DataInputStream in) throws IOException {
        readVarInt(in); // Longitud total del paquete
        int id = readVarInt(in);
    
        if (id == 0x00) {
            int length = readVarInt(in);
            byte[] data = new byte[length];
            in.readFully(data);
            return new String(data);
        }
    
        return null;
    }
    
    private static int readVarInt(DataInputStream in) throws IOException {
        int numRead = 0;
        int result = 0;
        byte read;
        do {
            read = in.readByte();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));
    
            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0b10000000) != 0);
    
        return result;
    }
    // FIN Funciones Minecraft
}