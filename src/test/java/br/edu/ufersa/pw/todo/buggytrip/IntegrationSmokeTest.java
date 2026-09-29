package br.edu.ufersa.pw.todo.buggytrip;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationSmokeTest extends IntegrationTestBase {
    @Autowired
    TestRestTemplate http;

    @Test
    void health() {
        var r = http.getForEntity("/actuator/health", String.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertTrue(r.getBody().contains("UP"));
    }
}
