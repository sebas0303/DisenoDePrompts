package ejerciciosJava.blockingQueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Clase que representa el buffer compartido utilizando BlockingQueue.
 */
class BufferCompartido {
    private final BlockingQueue<Integer> cola = new ArrayBlockingQueue<>(5);

    /**
     * Método para agregar un elemento al buffer.
     *
     * @param valor El valor a agregar al buffer.
     * @throws InterruptedException Si se interrumpe el hilo durante la operación.
     */
    public void producir(int valor) throws InterruptedException {
        cola.put(valor);
        System.out.println("Productor " + Thread.currentThread().getName() + ": Agregando elemento " + valor + " (Buffer ocupado: " + cola.size() + "/5)");
    }

    /**
     * Método para extraer un elemento del buffer.
     *
     * @return El elemento extraído del buffer.
     * @throws InterruptedException Si se interrumpe el hilo durante la operación.
     */
    public int consumir() throws InterruptedException {
        return cola.take();
    }
}

/**
 * Clase que representa al Productor y implementa Runnable para agregar elementos al BufferCompartido.
 */
class Productor implements Runnable {
    private final BufferCompartido buffer;

    public Productor(BufferCompartido buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                int valor = (int) (Math.random() * 10); // Genera un número aleatorio entre 0 y 9
                System.out.println("Productor " + Thread.currentThread().getName() + ": Agregando elemento " + valor);
                buffer.producir(valor);
                Thread.sleep(300); // Dormir por 300 ms
            } catch (InterruptedException e) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Productor " + Thread.currentThread().getName() + ": Se interrumpió. Terminando...");
                    break;
                }
                Thread.currentThread().interrupt();
            }
        }
    }
}

/**
 * Clase que representa al Consumidor y implementa Runnable para extraer elementos del BufferCompartido.
 */
class Consumidor implements Runnable {
    private final BufferCompartido buffer;

    public Consumidor(BufferCompartido buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                int valor = buffer.consumir();
                System.out.println("Consumidor " + Thread.currentThread().getName() + ": Extraiendo elemento " + valor);
                Thread.sleep(500); // Dormir por 500 ms
            } catch (InterruptedException e) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Consumidor " + Thread.currentThread().getName() + ": Se interrumpió. Terminando...");
                    break;
                }
                Thread.currentThread().interrupt();
            }
        }
    }
}

/**
 * Clase Main que lanza 2 Productores y 3 Consumidores, duerme el hilo principal 10 segundos (Thread.sleep(10000)), invoca interrupt() sobre los 5 hilos y espera su cierre real con join().
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        BufferCompartido buffer = new BufferCompartido();
        Thread productor1 = new Thread(new Productor(buffer), "Productor 1");
        Thread productor2 = new Thread(new Productor(buffer), "Productor 2");
        Thread consumidor1 = new Thread(new Consumidor(buffer), "Consumidor 1");
        Thread consumidor2 = new Thread(new Consumidor(buffer), "Consumidor 2");
        Thread consumidor3 = new Thread(new Consumidor(buffer), "Consumidor 3");

        productor1.start();
        productor2.start();
        consumidor1.start();
        consumidor2.start();
        consumidor3.start();

        // Espera hasta que todos los hilos terminen
        Thread.sleep(10000);
        buffer.producir(-1); // Simula un interrupt para Productores y Consumidores
        productor1.interrupt();
        productor2.interrupt();
        consumidor1.interrupt();
        consumidor2.interrupt();
        consumidor3.interrupt();

        productor1.join();
        productor2.join();
        consumidor1.join();
        consumidor2.join();
        consumidor3.join();
    }
}
