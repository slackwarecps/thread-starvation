# O Mistério da CPU Ociosa

Este projeto é o código-fonte de demonstração para o artigo **"O mistério da CPU ociosa: Por que sua API Java trava com 5% de processamento"**, publicado no Medium.

🔗 **Leia o artigo completo aqui:** [Medium - Fabio Alvaro](https://medium.com/@fabio.alvaro/o-mist%C3%A9rio-da-cpu-ociosa-por-que-sua-api-java-trava-com-5-de-processamento-0f1b890ad862)

## 🎯 Objetivo

O objetivo deste projeto é demonstrar, na prática, o comportamento de uma aplicação Java (Spring Boot) baseada em Threads de Plataforma (modelo tradicional one-thread-per-request) quando submetida a uma carga de requisições bloqueantes (I/O lento).

O projeto ilustra como o **Pool de Threads** do servidor (Tomcat) se esgota rapidamente, fazendo com que novas requisições sejam enfileiradas (backlog) e, eventualmente, rejeitadas, mesmo que a CPU esteja com uso baixo (ociosa), pois as threads estão apenas "dormindo" (waiting).

## 🚀 Estrutura do Projeto

*   **`TestenormalApplication.java`**: A aplicação servidora Spring Boot. Expõe um endpoint `/api-lenta` que simula um processamento demorado (sleep de 5 segundos).
*   **`StressTest.java`**: Um cliente de teste de carga simples, escrito em Java puro (usando Virtual Threads no cliente para eficiência), que dispara 300 requisições simultâneas contra o servidor para saturar o pool de threads.

## 🛠️ Pré-requisitos

*   **Java 21**
*   **Maven**

## 🏃‍♂️ Como Executar

### 1. Iniciar o Servidor

Primeiro, inicie a aplicação Spring Boot. Ela rodará na porta `8080`.

```bash
./mvnw spring-boot:run
```

Ou, se preferir rodar a classe principal diretamente na sua IDE: `br.com.fabioalvaro.thread1.TestenormalApplication`.

### 2. Executar o Teste de Stress

Com o servidor rodando, abra um novo terminal e execute a classe `StressTest`.

Você precisa compilar e rodar a classe `StressTest`. Uma forma simples via Maven (exec-maven-plugin) ou compilando manualmente:

**Compilando e rodando manualmente (na pasta raiz do projeto):**

```bash
# Compilar
javac -cp target/classes src/main/java/br/com/fabioalvaro/thread1/StressTest.java -d target/classes

# Rodar (Windows)
java -cp target/classes br.com.fabioalvaro.thread1.StressTest

# Rodar (Linux/Mac)
java -cp target/classes br.com.fabioalvaro.thread1.StressTest
```

*Nota: Se estiver usando uma IDE (IntelliJ, Eclipse, VS Code), basta clicar com o botão direito em `StressTest.java` e selecionar "Run".*

## 📊 O que esperar

Ao rodar o `StressTest`, você verá no console:

1.  O cliente dispara **300 requisições**.
2.  O servidor (Tomcat) por padrão (nesta configuração) tem cerca de **200 threads** para processamento.
3.  As primeiras 200 requisições serão aceitas e ficarão processando (dormindo 5s).
4.  O backlog (fila de espera do SO/Tomcat) aceitará mais algumas (ex: 10 ou 100 dependendo do SO).
5.  As requisições excedentes falharão imediatamente com erro de conexão ou timeout, pois não há threads nem espaço na fila para elas.
6.  No log do servidor, você verá o número de threads ativas subindo até o limite e travando lá até que os timeouts de 5s acabem.

Isso demonstra o **Gargalo de Threads**.

---
Feito com ☕ e Java por [Fabio Alvaro](https://medium.com/@fabio.alvaro).
