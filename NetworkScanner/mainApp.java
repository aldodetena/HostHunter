package NetworkScanner;

/**
 * Punto de entrada principal para la aplicación de escaneo de red.
 * Este método inicializa la ventana principal de la aplicación NetworkScannerApp y muestra la interfaz gráfica al usuario.
 * Sirve como el punto de inicio para ejecutar la aplicación.
 *
 * @param args Los argumentos de la línea de comandos pasados al programa. En este caso, no se utilizan.
 */
public class mainApp {
    public static void main(String[] args) {
        NetworkScannerApp window = new NetworkScannerApp();
        window.show();
    }
}
