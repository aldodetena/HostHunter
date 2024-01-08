package NetworkScanner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServiceIdentifier {

    public static Map<String, String> getServiceInfo(String host, int port) {
        Map<String, String> serviceInfo = new HashMap<>();

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
                serviceInfo = identifyFTPService(host, port);
                break;
            case 25:
                // Protocolo SMTP
                serviceInfo = identifyFTPService(host, port);
                break;
            case 53:
                // Protocolo FTP
                serviceInfo = identifyFTPService(host, port);
                break;
            case 67:
            case 68:
                // Protocolo DHCP
                serviceInfo = identifyFTPService(host, port);
                break;
            case 69:
                // Protocolo TFTP
                serviceInfo = identifyFTPService(host, port);
                break;
            case 80:
            case 443:
                // Protocolo HTTP o HTTPS
                serviceInfo = identifyHTTPService(host, port);
                break;
            case 110:
                // Protocolo POP3
                serviceInfo = identifyFTPService(host, port);
                break;
            case 111:
                // Protocolo RPC
                serviceInfo = identifyFTPService(host, port);
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
                serviceInfo.put("Service", "Unknown");
                break;
        }
        return serviceInfo;
    }

    // private static Map<String, String> identifyHTTPService(String host, int port) {

    // }

    // private static Map<String, String> identifyFTPService(String host, int port) {

    // }
}