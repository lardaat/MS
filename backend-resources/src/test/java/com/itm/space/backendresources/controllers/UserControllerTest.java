package com.itm.space.backendresources.controllers;

import com.itm.space.backendresources.BaseIntegrationTest;
import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import com.itm.space.backendresources.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@WithMockUser(roles = "MODERATOR")
public class UserControllerTest extends BaseIntegrationTest {
    @MockBean
    private UserService userService;
    private static UserRequest testUserRequest;
    private static UserResponse testUserResponse;

    @BeforeAll
    public static void setupOnce() {
        testUserResponse = new UserResponse("Nik", "Kod", "nik@gmail.com", List.of("user"), List.of("users"));
        testUserRequest = new UserRequest("dao", "nik@gmail.com", "12345", "Nik", "Kod");
    }

    @Test
    void createTest() throws Exception {
        // Подготавливаем
        MockHttpServletRequestBuilder requestBuilder = requestWithContent(post("/api/users"), testUserRequest);
        Mockito.doNothing().when(userService).createUser(any(UserRequest.class));
        // Тестируем
        mvc.perform(requestBuilder).
                andExpect(status().isOk());
    }

    @Test
    void getUserById() throws Exception {
        // Подготавливаем
        UUID id = UUID.randomUUID();
        Mockito.when(userService.getUserById(id)).thenReturn(testUserResponse);
        // Тестируем
        mvc.perform(get("/api/users/{id}", id.toString()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(testUserRequest.getEmail()));
    }

    @Test
    void helloTest() throws Exception {
        mvc.perform(get("/api/users/hello")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
