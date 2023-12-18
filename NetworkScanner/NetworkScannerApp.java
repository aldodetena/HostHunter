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
    private volatile boolean isScanning = false;
    private String selectedNetwork = null;
    private Queue<NetworkScanTask> networkQueue = new LinkedList<>();

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
            List<String> reachableHosts = new ArrayList<>();
            for (int host = 1; host < 255; host++) {
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
                        reachableHosts.add(hostAddress);
                        EventQueue.invokeLater(() -> displayReachableNetwork(hostAddress));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Ahora escanear los puertos de los hosts alcanzables
            for (String hostAddress : reachableHosts) {
                scanHostPorts(hostAddress, startPort, endPort, textArea);
            }
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

    private void scanHostPorts(String hostAddress, int startPort, int endPort, JTextArea textArea) {
        for (int port = startPort; port <= endPort; port++) {
            if (!isScanning) {
                // Si se ha cancelado el escaneo, salir del bucle y detener el escaneo de puertos
                break;
            }
            final int finalPort = port; // Crear una copia final de port para usar en la lambda
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(hostAddress, finalPort), 1000); // Tiempo de espera corto
                socket.close();
                // Usar finalPort dentro de la lambda
                EventQueue.invokeLater(() -> textArea.append("Host: " + hostAddress + " on port " + finalPort + " is open.\n"));
            } catch (IOException e) {
                textArea.append("Host: " + hostAddress + " on port " + finalPort + " is closed.\n");
                // Puerto no abierto o no alcanzable, puedes optar por no imprimir nada para estos casos
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
                btnScanNetwork.setEnabled(true);
                btnCancelScan.setEnabled(false);
            });
        }
    }

    private void displayReachableNetwork(String network) {
        JPanel networkPanel = new JPanel();
        networkPanel.setBorder(BorderFactory.createTitledBorder("Network: " + network));
        networkPanel.setLayout(new BorderLayout());

        JLabel infoLabel = new JLabel("Información adicional aquí");
        JButton actionButton = new JButton("Acción");

        // Aquí puedes agregar funcionalidades adicionales al botón o a otros componentes
        actionButton.addActionListener(e -> {
            // Acción para realizar
        });

        networkPanel.add(infoLabel, BorderLayout.CENTER);
        networkPanel.add(actionButton, BorderLayout.SOUTH);

        // Agrega networkPanel al contenedor principal o a otro componente de la interfaz de usuario
        frame.getContentPane().add(networkPanel);
        frame.revalidate();
        frame.repaint();
    }

    public void show() {
        frame.setVisible(true);
    }
}