import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

public class NetworkScannerApp {

    private JFrame frame;
    private JTextArea textArea;
    private JButton btnScanNetwork;
    private JButton btnCancelScan;
    private volatile boolean isScanning = false;

    public NetworkScannerApp() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));
        frame.setResizable(true); // Permitir redimensionamiento

        btnScanNetwork = new JButton("Scan Network");
        btnCancelScan = new JButton("Cancel Scan");
        btnCancelScan.setEnabled(false); // Inicialmente deshabilitado

        JPanel panel = new JPanel();
        panel.add(btnScanNetwork);
        panel.add(btnCancelScan);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);

        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        btnScanNetwork.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isScanning = true;
                btnScanNetwork.setEnabled(false);
                btnCancelScan.setEnabled(true);
                listNetworkInterfaces();
            }
        });

        btnCancelScan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isScanning = false;
                textArea.append("Scan cancelled.\n");
            }
        });
    }

    private void scanNetwork(JTextArea textArea, List<String> ipAddressesToScan) {
        new Thread(new Runnable() {
            public void run() {
                textArea.append("Scanning network...\n");
                for (String host : ipAddressesToScan) {
                    if (!isScanning) break;
                    try {
                        InetAddress address = InetAddress.getByName(host);
                        if (address.isReachable(1000)) {
                            textArea.append("Host: " + host + " is reachable.\n");
                        } else {
                            textArea.append("Host: " + host + " is not reachable.\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                textArea.append("Network scan completed.\n");
                isScanning = false;
                EventQueue.invokeLater(() -> {
                    btnScanNetwork.setEnabled(true);
                    btnCancelScan.setEnabled(false);
                });
            }
        }).start();
    }

    public void listNetworkInterfaces() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface intf : Collections.list(interfaces)) {
                if (!intf.isLoopback() && intf.isUp()) {
                    String subnetMask = getSubnetMask(intf);
                    if (!subnetMask.equals("Unavailable")) {
                        textArea.append("Interface: " + intf.getName() + " - Subnet Mask: " + subnetMask + "\n");
    
                        Enumeration<InetAddress> addresses = intf.getInetAddresses();
                        for (InetAddress addr : Collections.list(addresses)) {
                            if (addr.isSiteLocalAddress()) {
                                String networkAddress = calculateNetworkAddress(addr.getHostAddress(), subnetMask);
                                textArea.append("   Address: " + addr.getHostAddress() + " - Network: " + networkAddress + "\n");
                            }
                        }
                    } else {
                        textArea.append("Interface: " + intf.getName() + " - Subnet Mask: Unavailable\n");
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private String calculateNetworkAddress(String ipAddress, String subnetMask) {
        String[] ipParts = ipAddress.split("\\.");
        String[] maskParts = subnetMask.split("\\.");
        String networkAddress = "";
    
        for (int i = 0; i < 4; i++) {
            int ipPart = Integer.parseInt(ipParts[i]);
            int maskPart = Integer.parseInt(maskParts[i]);
            networkAddress += (ipPart & maskPart) + (i < 3 ? "." : "");
        }
    
        return networkAddress;
    }

    private static String getSubnetMask(NetworkInterface networkInterface) throws SocketException {
        // Verifica si la lista de InterfaceAddress no está vacía
        if (!networkInterface.getInterfaceAddresses().isEmpty()) {
            int prefixLength = networkInterface.getInterfaceAddresses().get(0).getNetworkPrefixLength();
            int mask = -1 << (32 - prefixLength);
            int part1 = ((mask >>> 24) & 255);
            int part2 = ((mask >>> 16) & 255);
            int part3 = ((mask >>> 8) & 255);
            int part4 = (mask & 255);
            return part1 + "." + part2 + "." + part3 + "." + part4;
        } else {
            return "Unavailable";
        }
    }

    public void show() {
        frame.setVisible(true);
    }
}