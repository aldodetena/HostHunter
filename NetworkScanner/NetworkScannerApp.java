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

/**
 * Clase principal de la aplicación NetworkScannerApp que proporciona una interfaz gráfica para
 * escanear redes y puertos, así como identificar servicios en hosts específicos.
 */
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

    /**
     * Constructor que inicializa la interfaz de usuario y configura los componentes de la ventana.
     */
    public NetworkScannerApp() {
        initialize();
    }

    /**
     * Inicializa los componentes de la interfaz gráfica de usuario y configura los manejadores de eventos.
     */
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
        textArea.setEditable(false);  // Hacer que el JTextArea no sea editable
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

    /**
     * Inicia un nuevo escaneo de red en un hilo separado.
     *
     * @param textArea El área de texto donde se mostrarán los resultados.
     * @param network La dirección de red a escanear.
     * @param startPort El puerto inicial del rango a escanear.
     * @param endPort El puerto final del rango a escanear.
     */
    private void scanNetwork(JTextArea textArea, String network, int startPort, int endPort) {
        new Thread(() -> {
            NetworkScanResult result = new NetworkScanResult(network);
    
            for (int host = 1; host <= 254; host++) {
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

    /**
     * Lista todas las interfaces de red disponibles y permite al usuario seleccionar una para el escaneo.
     */
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

    /**
     * Calcula la dirección de red basada en la dirección IP y la máscara de subred.
     *
     * @param ipAddress La dirección IP.
     * @param subnetMask La máscara de subred.
     * @return La dirección de red calculada.
     */
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

    /**
     * Calcula la máscara de subred para una interfaz de red dada, basada en su longitud de prefijo de red.
     * Esta función convierte la longitud de prefijo en una representación de máscara de subred decimal
     * (por ejemplo, 24 -> 255.255.255.0).
     *
     * @param networkInterface La interfaz de red cuya máscara de subred se va a calcular.
     * @return La máscara de subred en formato decimal con puntos (p.ej., "255.255.255.0") o "No disponible"
     *         si la interfaz no tiene direcciones configuradas.
     * @throws SocketException Si se produce un error al acceder a las propiedades de la interfaz de red.
     */
    private static String getSubnetMask(NetworkInterface networkInterface) throws SocketException {
        if (!networkInterface.getInterfaceAddresses().isEmpty()) {
            int prefixLength = networkInterface.getInterfaceAddresses().get(0).getNetworkPrefixLength();
            int mask = -1 << (32 - prefixLength);  // Cálculo de la máscara de subred utilizando el desplazamiento de bits
            int part1 = ((mask >>> 24) & 255);  // Extraer el primer octeto
            int part2 = ((mask >>> 16) & 255);  // Extraer el segundo octeto
            int part3 = ((mask >>> 8) & 255);   // Extraer el tercer octeto
            int part4 = (mask & 255);           // Extraer el cuarto octeto
            return part1 + "." + part2 + "." + part3 + "." + part4;  // Formato de notación decimal con puntos
        } else {
            return "No disponible";  // Devuelve esto si la interfaz no tiene direcciones
        }
    }

    /**
     * Escanea los puertos de un host específico y registra los puertos abiertos.
     *
     * @param hostAddress La dirección IP del host.
     * @param startPort El puerto inicial del rango a escanear.
     * @param endPort El puerto final del rango a escanear.
     * @param textArea El área de texto donde se mostrarán los resultados.
     * @param result El objeto donde se almacenan los resultados del escaneo.
     */
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

    /**
     * Recupera una lista de puertos abiertos para un host específico dentro de una red dada.
     * Esta función busca en los resultados de escaneos previos para encontrar la red y el host especificados
     * y compila una lista de descripciones de puertos abiertos.
     *
     * @param network La dirección de red o el identificador de la red en la cual se encuentra el host.
     * @param hostAddress La dirección IP del host cuyos puertos abiertos se desean listar.
     * @return Una lista de cadenas, cada una representando un puerto abierto con la etiqueta "Puerto Abierto:" seguida del número de puerto.
     *         Si no se encuentran puertos abiertos, la lista será vacía.
     */
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

    /**
     * Procesa la siguiente tarea de escaneo de red en la cola. Si la cola no está vacía, extrae la tarea siguiente
     * y comienza el escaneo de la red especificada. Si la cola está vacía, limpia el área de texto y muestra
     * los resultados del escaneo anterior.
     *
     * Este método gestiona la transición entre las tareas de escaneo activas y también actualiza la interfaz
     * de usuario una vez que todas las tareas han sido procesadas, reactivando el botón de escaneo y desactivando
     * el botón de cancelación.
     */
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

    /**
     * Muestra los resultados del escaneo de red.
     */
    private void displayScanResults() {
        for (NetworkScanResult result : scanResults) {
            displayNetworkResult(result);
        }
    }
    
    /**
     * Muestra los resultados del escaneo para una red específica.
     *
     * @param result El resultado del escaneo que se mostrará.
     */
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
                JPanel buttonsPanel = new JPanel();
                buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
                JButton showPortsbButton = new JButton("Mostrar Puertos");
                JButton showInfButton = new JButton("Mostrar Información");
                JButton reportButton = new JButton("Sacar Informe");

                // Configurar la alineación central y agregar a panel
                showPortsbButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                showInfButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                reportButton.setAlignmentX(Component.CENTER_ALIGNMENT);

                // Establecer el tamaño máximo de los botones basado en el botón más grande
                Dimension maxButtonSize = new Dimension(Integer.MAX_VALUE, showPortsbButton.getPreferredSize().height);
                showPortsbButton.setMaximumSize(maxButtonSize);
                showInfButton.setMaximumSize(maxButtonSize);
                reportButton.setMaximumSize(maxButtonSize);
    
                buttonsPanel.add(showPortsbButton);
                buttonsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                buttonsPanel.add(showInfButton);
                buttonsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                buttonsPanel.add(reportButton);
                networkPanel.add(buttonsPanel); // Añadir el panel de botones
    
                // Añadir eventos a los botones si es necesario
                showPortsbButton.addActionListener(e -> {
                    updatePortsInfoPanel(hostAddress, result);
                });
                showInfButton.addActionListener(e -> {
                    updateHostInfoPanel(hostAddress, result);
                });
                reportButton.addActionListener(e -> {
                    result.exportHostToHTML(hostAddress.toString());
                });
    
                networkPanelContainer.add(networkPanel);
            }
            mainpanel.revalidate();
        });
    }

    /**
     * Actualiza el panel de información de puertos mostrando los servicios disponibles en los puertos abiertos
     * de un host específico. Este método construye una lista visual de puertos y sus servicios asociados para
     * su exhibición en el panel de información.
     *
     * Si no se encuentran puertos abiertos, el panel mostrará un mensaje indicando que no se encontraron puertos.
     * Si se encuentran puertos abiertos, mostrará una lista de los puertos y los nombres de los servicios corriendo en ellos.
     *
     * @param hostAddress La dirección IP del host cuyos detalles de puertos se van a mostrar.
     * @param result El objeto NetworkScanResult que contiene la información de los puertos y servicios del host especificado.
     */
    public void updatePortsInfoPanel(String hostAddress, NetworkScanResult result) {
        infoPanel.removeAll();
        JPanel infoPortsPanel = new JPanel();
        infoPortsPanel.setMinimumSize(new Dimension(300, 100));
    
        Map<Integer, Map<String, String>> services = result.getServiceDetails(hostAddress);
        if (services.isEmpty()) {
            JLabel noPortsLabel = new JLabel("No se encontraron puertos abiertos.");
            infoPortsPanel.setPreferredSize(new Dimension(300, frame.getHeight()));
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
            infoPortsPanel.setPreferredSize(new Dimension(170, frame.getHeight()));
            infoPortsPanel.add(new JScrollPane(portsList), BorderLayout.CENTER);
        }
    
        infoPanel.add(infoPortsPanel);
        mainpanel.revalidate();
    }

    /**
     * Actualiza el panel de información del host mostrando detalles específicos sobre un host individual. 
     * Esta función construye una lista visual con información detallada sobre el host, como su dirección IP, 
     * nombre de host, y otros detalles relevantes, y la muestra en el panel de información.
     *
     * @param hostAddress La dirección IP del host cuyos detalles se van a mostrar.
     * @param result El objeto NetworkScanResult que contiene la información detallada del host especificado.
     */
    public void updateHostInfoPanel(String hostAddress, NetworkScanResult result) {
        infoPanel.removeAll();
        JPanel infoHostPanel = new JPanel();
    
        // Obtener la información del host
        List<String> hostInfo = result.getHostInfo(hostAddress);

        // Llamar a la función identifyOS y agregar el resultado a la lista de información
        String osInfo = NetworkScanResult.identifyOS(hostAddress);
        hostInfo.add("Sistema Operativo: " + osInfo);
    
        // Crear y configurar la JList
        JList<String> infoList = new JList<>(hostInfo.toArray(new String[0]));
        infoList.setLayoutOrientation(JList.VERTICAL);
    
        // Añadir la JList al infoPanel
        infoHostPanel.add(new JScrollPane(infoList), BorderLayout.CENTER);
    
        // Actualizar el panel
        infoPanel.add(infoHostPanel);
        mainpanel.revalidate();
    }

    /**
     * Hace visible la ventana principal de la aplicación.
     */
    public void show() {
        frame.setVisible(true);
    }
}