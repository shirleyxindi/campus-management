package nl.tudelft.oopp.group39.auth.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.oopp.group39.auth.exceptions.UnauthorizedException;
import nl.tudelft.oopp.group39.auth.models.AuthRequest;
import nl.tudelft.oopp.group39.user.entities.User;
import nl.tudelft.oopp.group39.user.enums.Role;
import nl.tudelft.oopp.group39.user.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final User testUser = new User(
        "test",
        "test@tudelft.nl",
        "test",
        null,
        Role.ADMIN,
        null
    );

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService.createUser(testUser);
    }

    @AfterEach
    void tearDown() {
        userService.deleteUser(testUser.getUsername());
    }

    @Test
    void createToken() throws Exception {
        AuthRequest request = new AuthRequest("test", "test");
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(AuthController.REST_MAPPING)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.body.token").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.body.token").isNotEmpty());
    }

    @Test
    void createTokenFailed() throws Exception {
        AuthRequest request = new AuthRequest("test2", "test");
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(AuthController.REST_MAPPING)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isUnauthorized())
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                .value(UnauthorizedException.UNAUTHORIZED));
    }
}