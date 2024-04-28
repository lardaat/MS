package com.itm.space.backendresources.service;

import com.itm.space.backendresources.BaseIntegrationTest;
import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;


@SpringBootTest
public class UserServiceImplTest extends BaseIntegrationTest {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private Keycloak keycloak;
    @Value("${keycloak.realm}")
    private String realm;
    private static UserRequest testUser;

    @BeforeAll
    public static void setupOnce() {
        testUser = new UserRequest("dao", "nik@gmail.com", "12345", "Nik", "Kod");
    }

    @Test
    void createUserTest() {
        UserRepresentation userRepresentation = null;
        try {
            // Проверяем
            userService.createUser(testUser);

            userRepresentation = keycloak.realm(realm).users().search(testUser.getUsername()).get(0);
            Assertions.assertEquals(userRepresentation.getUsername(), testUser.getUsername());
            Assertions.assertEquals(userRepresentation.getEmail(), testUser.getEmail());
            Assertions.assertEquals(userRepresentation.getFirstName(), testUser.getFirstName());
            Assertions.assertEquals(userRepresentation.getLastName(), testUser.getLastName());
        } finally {
            // Удаляем
            if (userRepresentation != null) {
                keycloak.realm(realm).users().get(userRepresentation.getId()).remove();
            }
        }
    }

    @Test
    void getUserByIdTest() {
        UserRepresentation userRepresentation = null;

        try {
            // Подготавливаем
            userService.createUser(testUser);
            userRepresentation = keycloak.realm(realm).users().search(testUser.getUsername()).get(0);
            String id = userRepresentation.getId();

            try {
                // Проверяем
                UserResponse userResponse = userService.getUserById(UUID.fromString(id));
                Assertions.assertEquals(userResponse.getFirstName(), testUser.getFirstName());
                Assertions.assertEquals(userResponse.getLastName(), testUser.getLastName());
                Assertions.assertEquals(userResponse.getEmail(), testUser.getEmail());
            } catch (Exception e) {
                // Вывод ошибки
                System.err.println("Ошибка при проверке: " + e.getMessage());
                throw e;
            } finally {
                // Удаляем
                if (userRepresentation != null) {
                    keycloak.realm(realm).users().get(userRepresentation.getId()).remove();
                }
            }

        } catch (Exception e) {
            // Вывод ошибки
            System.err.println("Ошибка при подготовке: " + e.getMessage());
            throw e;
        }
    }
}
