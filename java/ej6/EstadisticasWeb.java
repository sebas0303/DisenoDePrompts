package ej6;

import java.util.*;

public class EstadisticasWeb {
    // Usamos ConcurrentHashMap en lugar de HashMap para evitar corrupción concurrente
    private final Map<String, Integer> visitas = new HashMap<>();
    private int total = 0;

    /**
     * Registra una visita de forma sincronizada para evitar condiciones de carrera.
     */
    public synchronized void registrar(String pagina) {
        Integer actual = visitas.get(pagina);
        if (actual == null) {
            visitas.put(pagina, 1);
        } else {
            visitas.put(pagina, actual + 1);
        }
        total++;
    }

    /**
     * Devuelve el total acumulado de forma sincronizada.
     */
    public synchronized int getTotal() {
        return total;
    }

    /**
     * Devuelve las n páginas más visitadas ordenadas de mayor a menor.
     */
    public synchronized List<String> masVisitadas(int n) {
        List<String> paginas = new ArrayList<>(visitas.keySet());
        
        // Orden descendente (b.compareTo(a)) para obtener las MÁS visitadas, no las menos
        paginas.sort((a, b) -> visitas.get(b).compareTo(visitas.get(a)));
        
        // Validación de seguridad para evitar IndexOutOfBoundsException si n es mayor que el mapa
        int limite = Math.min(n, paginas.size());
        return paginas.subList(0, limite);
    }
}