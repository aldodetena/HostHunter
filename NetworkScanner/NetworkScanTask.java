package NetworkScanner;

/**
 * Representa una tarea de escaneo de red que identifica los puertos abiertos dentro de un rango especificado en una red.
 * Esta clase almacena la información necesaria para ejecutar un escaneo de puertos en una red. Incluye
 * la dirección de la red y el rango de puertos a escanear. Esta configuración es utilizada por el motor
 * de escaneo para verificar la disponibilidad de puertos en la red indicada.
 *
 * @author Inicia en
 */
public class NetworkScanTask {
    String network;
    int startPort;
    int endPort;

    /**
     * Construye una nueva tarea de escaneo de red con los parámetros especificados.
     *
     * @param network   La dirección IP o rango de direcciones de red que se va a escanear.
     * @param startPort El número de puerto inicial del rango a escanear.
     * @param endPort   El número de puerto final del rango a escanear.
     */
    public NetworkScanTask(String network, int startPort, int endPort) {
        this.network = network;
        this.startPort = startPort;
        this.endPort = endPort;
    }
}
