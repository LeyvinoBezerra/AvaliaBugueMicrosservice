package br.edu.ufersa.pw.todo.buggytrip;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection; import org.springframework.boot.test.context.SpringBootTest; import org.testcontainers.containers.PostgreSQLContainer; import org.testcontainers.junit.jupiter.Container; import org.testcontainers.junit.jupiter.Testcontainers;
@Testcontainers @SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT) abstract class IntegrationTestBase{@Container @ServiceConnection static PostgreSQLContainer<?> postgres=new PostgreSQLContainer<>("postgres:17-alpine");}
