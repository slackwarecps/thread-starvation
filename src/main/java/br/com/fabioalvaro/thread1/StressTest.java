package br.com.fabioalvaro.thread1;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class StressTest {

    // Vamos disparar 300 requisições. 
    // O servidor só aguenta 200 threads + 10 backlog = 210.
    // 90 requisições DEVEM falhar.
    private static final int TOTAL_REQUESTS = 300;

    public static void main(String[] args) {
        // Usando Virtual Threads no cliente para conseguir gerar carga fácil
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            
            HttpClient client = HttpClient.newHttpClient();
            List<Future<?>> futures = new ArrayList<>();

            System.out.println(">>> INICIANDO ATAQUE DE " + TOTAL_REQUESTS + " REQUISIÇÕES <<<");

            for (int i = 0; i < TOTAL_REQUESTS; i++) {
                int id = i;
                futures.add(executor.submit(() -> {
                    long start = System.currentTimeMillis();
                    try {
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:8080/api-lenta"))
                                .GET()
                                .timeout(Duration.ofSeconds(10)) // Timeout do cliente
                                .build();

                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        
                        // Se deu 200 OK
                        if (response.statusCode() == 200) {
                            System.out.printf("Req #%d: SUCESSO (%dms)\n", id, (System.currentTimeMillis() - start));
                        } else {
                            System.out.printf("Req #%d: ERRO HTTP %d\n", id, response.statusCode());
                        }

                    } catch (Exception e) {
                        // AQUI ESTÁ A PROVA DO CRIME: Connection Refused ou Timeout
                        System.err.printf("Req #%d: FALHOU - %s (%dms)\n", id, e.getMessage(), (System.currentTimeMillis() - start));
                    }
                }));
            }
            
            // Aguarda tudo terminar só pra não fechar o main
            futures.forEach(f -> {
                try { f.get(); } catch (Exception e) {}
            });
        }
    }
}