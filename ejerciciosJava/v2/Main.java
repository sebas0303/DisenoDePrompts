package ejerciciosJava.v2;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Clase que representa el buffer compartido para sincronizar a los Productor y Consumidor.
 */
class BufferCompartido {
    private int[] elementos;
    private final ReentrantLock lock = new ReentrantLock();
    private int frente, tras, cantidadElementos;

    public BufferCompartido(int capacidad) {
        this.elementos = new int[capacidad];
        this.frente = 0;
        this.tras = 0;
        this.cantidadElementos = 0;
    }

    /**
     * Método para agregar un elemento al buffer.
     */
    synchronized void add(int elemento) throws InterruptedException {
        lock.lock();
        try {
            while (cantidadElementos == elementos.length) {
                System.out.println("Buffer lleno, esperando espacio...");
                wait(); // Espera hasta que haya espacio disponible
            }
            elementos[tras] = elemento;
            tras = (tras + 1) % elementos.length;
            cantidadElementos++;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Método para extraer un elemento del buffer.
     */
    synchronized int remove() throws InterruptedException {
        lock.lock();
        try {
            while (cantidadElementos == 0) {
                System.out.println("Buffer vacío, esperando elementos...");
                wait(); // Espera hasta que haya elementos disponibles
            }
            int elemento = elementos[frente];
            frente = (frente + 1) % elementos.length;
            cantidadElementos--;
            return elemento;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Método para obtener el número de elementos en el buffer.
     */
    synchronized int size() {
        return cantidadElementos;
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
        for (int i = 0; i < 10; i++) {
            try {
                System.out.println("Productor " + Thread.currentThread().getName() + ": Agregando elemento " + i);
                buffer.add(i);
                Thread.sleep(50); // Simula el tiempo de producción
            } catch (InterruptedException e) {
                if(Thread.currentThread().isInterrupted()) {
                    System.out.println("Productor " + Thread.currentThread().getName() + ": Se interrumpió. Terminando...");
                    return;
                }
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
        for (int i = 0; i < 10; i++) {
            try {
                System.out.println("Consumidor " + Thread.currentThread().getName() + ": Extraiendo elemento " + i);
                buffer.remove();
                Thread.sleep(50); // Simula el tiempo de consumo
            } catch (InterruptedException e) {
                if(Thread.currentThread().isInterrupted()) {
                    System.out.println("Consumidor " + Thread.currentThread().getName() + ": Se interrumpió. Terminando...");
                    return;
                }
            }
        }
    }
}

/**
 * Clase Main que lanza 2 Productores y 3 Consumidores, duerme el hilo principal 10 segundos (Thread.sleep(10000)), invoca interrupt() sobre los 5 hilos y espera su cierre real con join().
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        BufferCompartido buffer = new BufferCompartido(5);
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
        buffer.add(-1); // Simula un interrupt para Productores y Consumidores
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
