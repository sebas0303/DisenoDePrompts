package ej6;

public class Main {
 public static void main(String[] args) throws InterruptedException {
 EstadisticasWeb stats = new EstadisticasWeb();
 String[] paginas = {"/inicio", "/inicio", "/inicio",
 "/productos", "/productos", "/contacto"};
 Thread[] hilos = new Thread[4];
 for (int i = 0; i < hilos.length; i++) {
 hilos[i] = new Thread(() -> {
 for (int j = 0; j < 6000; j++) {
 stats.registrar(paginas[j % paginas.length]);
 }
 });
 hilos[i].start();
 }
 for (Thread h : hilos) h.join();
 System.out.println("Total: " + stats.getTotal() + " (esperado 24000)");
 System.out.println("Top 2: " + stats.masVisitadas(2)
 + " (esperado [/inicio, /productos])");
 }
}
