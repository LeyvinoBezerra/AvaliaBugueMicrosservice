package br.edu.ufersa.pw.todo.buggytrip;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

class SecurityIntegrationTest extends IntegrationTestBase {
    @Autowired
    TestRestTemplate http;

    @Test
    void endpointProtegido() {
        var r = http.getForEntity("/api/v1/avaliacoes", String.class);
        assertEquals(HttpStatus.FORBIDDEN, r.getStatusCode());
    }

    @Test
    void openApiPublico() {
        var r = http.getForEntity("/v3/api-docs", String.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertTrue(r.getBody().contains("BuggyTrip API"));
    }
}
