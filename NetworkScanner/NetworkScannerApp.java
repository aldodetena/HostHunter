package NetworkScanner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Queue;

public class NetworkScannerApp {

    private JFrame frame;
    private JTextArea textArea;
    private JButton btnScanNetwork;
    private JButton btnCancelScan;
    private JProgressBar progressBar;
    private JPanel networkPanelContainer;
    private volatile boolean isScanning = false;
    private String selectedNetwork = null;
    private Queue<NetworkScanTask> networkQueue = new LinkedList<>();
    private List<NetworkScanResult> scanResults = new ArrayList<>();

    public NetworkScannerApp() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));
        frame.setResizable(true); // Permitir redimensionamiento

        progressBar = new JProgressBar(0, 254); // Asumiendo que escaneas 254 hosts (1-254)
        progressBar.setValue(0);
        progressBar.setStringPainted(true); // Para mostrar el porcentaje de progreso

        btnScanNetwork = new JButton("Scan Network");
        btnCancelScan = new JButton("Cancel Scan");
        btnCancelScan.setEnabled(false); // Inicialmente deshabilitado

        networkPanelContainer = new JPanel();
        networkPanelContainer.setLayout(new BoxLayout(networkPanelContainer, BoxLayout.Y_AXIS));
        JScrollPane networkPanelScrollPane = new JScrollPane(networkPanelContainer);
        frame.getContentPane().add(networkPanelScrollPane, BorderLayout.EAST); // Ajusta la posición según necesidad

        JPanel panel = new JPanel();
        panel.add(btnScanNetwork);
        panel.add(btnCancelScan);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);
        frame.getContentPane().add(progressBar, BorderLayout.NORTH);

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

    private void scanNetwork(JTextArea textArea, String network, int startPort, int endPort) {
        new Thread(() -> {
            NetworkScanResult result = new NetworkScanResult(network);
    
            for (int host = 1; host <= 5; host++) {
                if (!isScanning) {
                    break;
                }
                final int hostIndex = host;
                EventQueue.invokeLater(() -> progressBar.setValue(hostIndex - 1));
                String hostAddress = network.substring(0, network.lastIndexOf(".") + 1) + host;
    
                try {
                    InetAddress address = InetAddress.getByName(hostAddress);
                    if (address.isReachable(1000)) {
                        textArea.append("Host: " + hostAddress + " is reachable.\n");
                        result.addReachableHost(hostAddress);
    
                        // Lanzar un nuevo hilo para escanear puertos
                        final String finalHostAddress = hostAddress;
                        new Thread(() -> scanHostPorts(finalHostAddress, startPort, endPort, textArea, result)).start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    
            scanResults.add(result);
            EventQueue.invokeLater(() -> progressBar.setValue(0));
            processNextNetwork();
        }).start();
    }

    public void listNetworkInterfaces() {
        List<String> networkAddresses = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface intf : Collections.list(interfaces)) {
                if (!intf.isLoopback() && intf.isUp()) {
                    String subnetMask = getSubnetMask(intf);
                    if (!subnetMask.equals("Unavailable")) {
                        Enumeration<InetAddress> addresses = intf.getInetAddresses();
                        for (InetAddress addr : Collections.list(addresses)) {
                            if (addr.isSiteLocalAddress()) {
                                String networkAddress = calculateNetworkAddress(addr.getHostAddress(), subnetMask);
                                if (!networkAddresses.contains(networkAddress)) {
                                    networkAddresses.add(networkAddress);
                                }
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    
        if (!networkAddresses.isEmpty()) {
            JComboBox<String> networkComboBox = new JComboBox<>(networkAddresses.toArray(new String[0]));
            JTextField startPortField = new JTextField(5);
            JTextField endPortField = new JTextField(5);
    
            JPanel panel = new JPanel();
            panel.add(new JLabel("Select a network:"));
            panel.add(networkComboBox);
            panel.add(Box.createHorizontalStrut(15)); // Espaciado
            panel.add(new JLabel("Start Port:"));
            panel.add(startPortField);
            panel.add(new JLabel("End Port:"));
            panel.add(endPortField);
    
            int result = JOptionPane.showConfirmDialog(frame, panel, "Network and Port Range Selection", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
    
            if (result == JOptionPane.OK_OPTION) {
                selectedNetwork = (String) networkComboBox.getSelectedItem();
                int startPort = Integer.parseInt(startPortField.getText());
                int endPort = Integer.parseInt(endPortField.getText());
                NetworkScanTask task = new NetworkScanTask(selectedNetwork, startPort, endPort);
                networkQueue.add(task); // Añade la tarea a la cola
                if (networkQueue.size() == 1) {
                    processNextNetwork();
                }
            }
        } else {
            textArea.append("No network interfaces found.\n");
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

    private void scanHostPorts(String hostAddress, int startPort, int endPort, JTextArea textArea, NetworkScanResult result) {
        for (int port = startPort; port <= endPort; port++) {
            if (!isScanning) {
                break;
            }
            final int finalPort = port;
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(hostAddress, finalPort), 1000); // Tiempo de espera corto
                socket.close();
                EventQueue.invokeLater(() -> textArea.append("Host: " + hostAddress + " on port " + finalPort + " is open.\n"));
                result.addOpenPort(hostAddress, finalPort);
            } catch (IOException e) {
                textArea.append("Host: " + hostAddress + " on port " + finalPort + " is closed.\n");
            }
        }
    }

    private void processNextNetwork() {
        if (!networkQueue.isEmpty()) {
            NetworkScanTask task = networkQueue.poll();
            scanNetwork(textArea, task.network, task.startPort, task.endPort);
        } else {
            textArea.append("All scans completed.\n");
            EventQueue.invokeLater(() -> {
                displayScanResults();
                btnScanNetwork.setEnabled(true);
                btnCancelScan.setEnabled(false);
            });
        }
    }

    private void displayScanResults() {
        for (NetworkScanResult result : scanResults) {
            displayNetworkResult(result);
        }
    }
    
    private void displayNetworkResult(NetworkScanResult result) {
        JPanel networkPanel = new JPanel();
        networkPanel.setBorder(BorderFactory.createTitledBorder("Network: " + result.network));
        networkPanel.setLayout(new BorderLayout());
    
        // Aquí, construye la información para mostrar basada en result
        JLabel infoLabel = new JLabel("Información de " + result.network);
        networkPanel.add(infoLabel, BorderLayout.CENTER);
        JButton actionButton = new JButton("Acción");
    
        actionButton.addActionListener(e -> {
            // Acción para realizar
        });

        networkPanel.add(infoLabel, BorderLayout.CENTER);
        networkPanel.add(actionButton, BorderLayout.SOUTH);
    
        // Añade networkPanel al networkPanelContainer
        networkPanelContainer.add(networkPanel);
        networkPanelContainer.revalidate();
        networkPanelContainer.repaint();
    }

    public void show() {
        frame.setVisible(true);
    }
}