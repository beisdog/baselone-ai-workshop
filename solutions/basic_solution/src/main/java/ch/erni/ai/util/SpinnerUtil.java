package ch.erni.ai.util;
public class SpinnerUtil {

    private static final String[] SPINNER = {".  ", ".. ", "...", "   "};
    private Thread spinnerThread;
    private volatile boolean running = false;

    public void startSpinner(String message) {
        if (running) {
            return; // Spinner läuft bereits
        }

        running = true;
        spinnerThread = new Thread(() -> {
            int i = 0;
            while (running) {
                System.out.print("\r" + SPINNER[i % SPINNER.length] + " " + message);
                i++;
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        spinnerThread.start();
    }

    public void stopSpinner(String finalMessage) {
        if (!running) {
            return;
        }

        running = false;
        try {
            spinnerThread.join(); // Warte bis Thread beendet ist
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Lösche die Spinner-Zeile und zeige finale Nachricht
        System.out.print("\r" + finalMessage + "                    \n");
    }

    public void stopSpinner() {
        stopSpinner("              ");
    }
}
