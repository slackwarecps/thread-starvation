package br.com.fabioalvaro.thread1;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@SpringBootApplication
@RestController
public class TestenormalApplication {

	private static final Logger logger = LoggerFactory.getLogger(TestenormalApplication.class);
    // Contador para vermos quantas requisições estão penduradas
    private final AtomicInteger activeRequests = new AtomicInteger(0);

	public static void main(String[] args) {
		SpringApplication.run(TestenormalApplication.class, args);
	}

	@GetMapping("/api-lenta")
    public String processarPagamento() throws InterruptedException {
        int current = activeRequests.incrementAndGet();
        String threadName = Thread.currentThread().getName();
        
        // Log para provar que a thread foi alocada
        logger.info("[{}] Recebido! Ativas agora: {}", threadName, current);

        try {
            // SIMULAÇÃO DE I/O (Banco de dados ou API Externa demorando 5s)
            // Aqui a thread fica PRESA, mas a CPU não faz nada.
            Thread.sleep(5000); 
            
            return "Pagamento Processado com Sucesso";
        } finally {
            activeRequests.decrementAndGet();
            logger.info("[{}] Finalizado. Liberando thread.", threadName);
        }
    }

}
