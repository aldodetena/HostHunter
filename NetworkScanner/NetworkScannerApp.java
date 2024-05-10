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
import java.util.Map;
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
    private JPanel mainpanel;
    private JPanel networkPanelContainer;
    private JPanel infoPanel;
    private JScrollPane scrollPane;
    private JScrollPane networkPanelScrollPane;
    private volatile boolean isScanning = false;
    private String selectedNetwork = null;
    private Queue<NetworkScanTask> networkQueue = new LinkedList<>();
    private List<NetworkScanResult> scanResults = new ArrayList<>();

    public NetworkScannerApp() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        mainpanel = new JPanel();
        networkPanelContainer = new JPanel();
        infoPanel = new JPanel();
        progressBar = new JProgressBar(0, 254); // Asumiendo que escaneas 254 hosts (1-254)
        btnScanNetwork = new JButton("Escanear Red");
        btnCancelScan = new JButton("Cancelar Escaner");
        textArea = new JTextArea();
        scrollPane = new JScrollPane(textArea);
        networkPanelScrollPane = new JScrollPane(networkPanelContainer);
        
        frame.setBounds(100, 100, 1000, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));
        frame.setResizable(true); // Permitir redimensionamiento
        progressBar.setValue(0);
        progressBar.setStringPainted(true); // Para mostrar el porcentaje de progreso
        btnCancelScan.setEnabled(false); // Inicialmente deshabilitado
        networkPanelContainer.setLayout(new BoxLayout(networkPanelContainer, BoxLayout.Y_AXIS));
        infoPanel.setLayout(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Información del Host"));

        mainpanel.add(btnScanNetwork);
        mainpanel.add(btnCancelScan);

        frame.getContentPane().add(mainpanel, BorderLayout.SOUTH);
        frame.getContentPane().add(progressBar, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(networkPanelScrollPane, BorderLayout.EAST);
        frame.getContentPane().add(infoPanel, BorderLayout.WEST);

        btnScanNetwork.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isScanning = true;
                btnScanNetwork.setEnabled(false);
                btnCancelScan.setEnabled(true);

                // Limpiar los resultados del escaneo anterior
                scanResults.clear();
        
                // Limpiar networkPanelContainer antes de iniciar un nuevo escaneo
                networkPanelContainer.removeAll();
                networkPanelContainer.revalidate();
                networkPanelContainer.repaint();
                infoPanel.removeAll();
                infoPanel.revalidate();
                infoPanel.repaint();
        
                listNetworkInterfaces();
            }
        });

        btnCancelScan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isScanning = false;
                textArea.append("Escaneo Cancelado.\n");
            }
        });
    }

    private void scanNetwork(JTextArea textArea, String network, int startPort, int endPort) {
        new Thread(() -> {
            NetworkScanResult result = new NetworkScanResult(network);
    
            for (int host = 1; host <= 30; host++) {
                if (!isScanning) {
                    break;
                }
                final int hostIndex = host;
                EventQueue.invokeLater(() -> progressBar.setValue(hostIndex - 1));
                String hostAddress = network.substring(0, network.lastIndexOf(".") + 1) + host;
    
                try {
                    InetAddress address = InetAddress.getByName(hostAddress);
                    if (address.isReachable(1000)) {
                        textArea.append("Host: " + hostAddress + " Es alcanzable.\n");
                        result.addReachableHost(hostAddress);

                         // Escanear puertos para el host alcanzable
                        scanHostPorts(hostAddress, startPort, endPort, textArea, result);
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
                    if (!subnetMask.equals("No disponible")) {
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
            panel.add(new JLabel("Selecciona una red:"));
            panel.add(networkComboBox);
            panel.add(Box.createHorizontalStrut(15)); // Espaciado
            panel.add(new JLabel("Puerto Inicial:"));
            panel.add(startPortField);
            panel.add(new JLabel("Puerto Final:"));
            panel.add(endPortField);
    
            int result = JOptionPane.showConfirmDialog(frame, panel, "Rango de selección de puertos y red", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
    
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
            textArea.append("No se encontraron interfaces de red.\n");
        }
    }

    private String calculateNetworkAddress(String ipAddress, String subnetMask) {
        String[] ipParts = ipAddress.split("\\.");
        String[] maskParts = subnetMask.split("\\.");
        StringBuilder networkAddress = new StringBuilder();
    
        for (int i = 0; i < 4; i++) {
            int ipPart = Integer.parseInt(ipParts[i]);
            int maskPart = Integer.parseInt(maskParts[i]);
            if (i < 3) { // Para los primeros tres octetos, aplica la máscara de subred
                networkAddress.append((ipPart & maskPart)).append(".");
            } else { // Para el último octeto, establecerlo a 0
                networkAddress.append("0");
            }
        }
    
        return networkAddress.toString();
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
            return "No disponible";
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
                 // Identificar el servicio en el puerto abierto
                ServiceIdentifier.getServiceInfo(hostAddress, finalPort, result);
                EventQueue.invokeLater(() -> textArea.append("Host: " + hostAddress + " en el puerto " + finalPort + " está abierto.\n"));
                result.addOpenPort(hostAddress, finalPort);
            } catch (IOException e) {
                textArea.append("Host: " + hostAddress + " en el puerto " + finalPort + " está cerrado.\n");
            }
        }
    }

    public List<String> getOpenPortsInfoForHost(String network, String hostAddress) {
        List<String> openPortsInfo = new ArrayList<>();
    
        for (NetworkScanResult result : scanResults) {
            if (result.network.equals(network)) {
                List<Integer> ports = result.openPorts.get(hostAddress);
                if (ports != null) {
                    for (Integer port : ports) {
                        openPortsInfo.add("Puerto Abierto: " + port);
                    }
                }
                break; // Salir del bucle una vez que encuentres la red y hayas procesado el host
            }
        }
    
        return openPortsInfo;
    }

    private void processNextNetwork() {
        if (!networkQueue.isEmpty()) {
            NetworkScanTask task = networkQueue.poll();
            scanNetwork(textArea, task.network, task.startPort, task.endPort);
        } else {
            EventQueue.invokeLater(() -> {
                // Limpiar el contenido del JTextArea
                textArea.setText("");
    
                displayScanResults(); // Mostrar los resultados del escaneo
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
        SwingUtilities.invokeLater(() -> {
            for (String hostAddress : result.reachableHosts) {
                JPanel networkPanel = new JPanel();
                networkPanel.setLayout(new BoxLayout(networkPanel, BoxLayout.Y_AXIS));
                networkPanel.setBorder(BorderFactory.createTitledBorder("Red: " + result.network));
                networkPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    
                // Etiqueta con la dirección IP del host alcanzable
                JLabel infoLabel = new JLabel("Host activo: " + hostAddress);
                infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Alineación a la izquierda
                networkPanel.add(infoLabel);
    
                // Panel para botones
                JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JButton showPortsbButton = new JButton("Mostrar Puertos");
                JButton showInfButton = new JButton("Mostrar Información");
                JButton actionButton3 = new JButton("Sacar Informe");
    
                buttonsPanel.add(showPortsbButton);
                buttonsPanel.add(showInfButton);
                buttonsPanel.add(actionButton3);
                networkPanel.add(buttonsPanel); // Añadir el panel de botones
    
                // Añadir eventos a los botones si es necesario
                showPortsbButton.addActionListener(e -> {
                    updatePortsInfoPanel(hostAddress, result);
                });
                showInfButton.addActionListener(e -> {
                    updateHostInfoPanel(hostAddress, result);
                });
                actionButton3.addActionListener(e -> {
                    result.exportHostToHTML(hostAddress.toString());
                });
    
                networkPanelContainer.add(networkPanel);
            }
            mainpanel.revalidate();
        });
    }

    public void updatePortsInfoPanel(String hostAddress, NetworkScanResult result) {
        infoPanel.removeAll();
        JPanel infoPortsPanel = new JPanel();
        infoPortsPanel.add(new JLabel("Host: " + hostAddress), BorderLayout.NORTH);
        infoPortsPanel.setPreferredSize(new Dimension(250, frame.getHeight()));
        infoPortsPanel.setMinimumSize(new Dimension(250, 100));
    
        Map<Integer, Map<String, String>> services = result.getServiceDetails(hostAddress);
        if (services.isEmpty()) {
            JLabel noPortsLabel = new JLabel("No se encontraron puertos abiertos.");
            infoPortsPanel.add(noPortsLabel, BorderLayout.CENTER);
        } else {
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (Map.Entry<Integer, Map<String, String>> entry : services.entrySet()) {
                Integer port = entry.getKey();
                Map<String, String> serviceInfo = entry.getValue();
                String serviceName = serviceInfo.getOrDefault("Service", "Desconocido");
                listModel.addElement("Puerto " + port + ": " + serviceName);
            }
            JList<String> portsList = new JList<>(listModel);
            infoPortsPanel.add(new JScrollPane(portsList), BorderLayout.CENTER);
        }
    
        infoPanel.add(infoPortsPanel);
        mainpanel.revalidate();
    }

    public void updateHostInfoPanel(String hostAddress, NetworkScanResult result) {
        infoPanel.removeAll();
        JPanel infoHostPanel = new JPanel();
    
        // Obtener la información del host
        List<String> hostInfo = result.getHostInfo(hostAddress);
    
        // Crear y configurar la JList
        JList<String> infoList = new JList<>(hostInfo.toArray(new String[0]));
        infoList.setLayoutOrientation(JList.VERTICAL);
    
        // Añadir la JList al infoPanel
        infoHostPanel.add(new JScrollPane(infoList), BorderLayout.CENTER);
    
        // Actualizar el panel
        infoPanel.add(infoHostPanel);
        mainpanel.revalidate();
    }

    public void show() {
        frame.setVisible(true);
    }
}