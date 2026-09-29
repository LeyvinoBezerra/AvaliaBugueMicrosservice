package br.edu.ufersa.pw.todo.buggytrip.api.controllers;

import br.edu.ufersa.pw.todo.buggytrip.api.dtos.Auth.LoginResponse;
import br.edu.ufersa.pw.todo.buggytrip.domain.enuns.EnumUsuario;
import br.edu.ufersa.pw.todo.buggytrip.domain.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    MockMvc mvc;
    @MockitoBean
    AuthService service;

    @Test
    void login() throws Exception {
        when(service.login(any())).thenReturn(new LoginResponse("jwt", "Bearer", 3600, 1L, "Cliente", EnumUsuario.CLIENTE));
        mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"cliente@buggytrip.local\",\"senha\":\"Cliente@123\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.token").value("jwt"));
    }
}
