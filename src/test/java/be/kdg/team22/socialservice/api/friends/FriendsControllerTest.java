package be.kdg.team22.socialservice.api.friends;

import be.kdg.team22.socialservice.application.friends.FriendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static be.kdg.team22.socialservice.testutils.JwtTestUtils.jwtWithUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FriendsController.class)
class FriendsControllerTest {
    private static final String USER_ID = "11111111-1111-1111-1111-111111111111";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FriendService friendService;

    @BeforeEach
    void setup() {
    }

    @Test
    void getFriends_withoutJwtShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/friends")).andExpect(status().isUnauthorized());
    }

    @Test
    void addFriend_callsServiceWithCorrectUsername() throws Exception {
        mockMvc.perform(post("/api/friends").contentType(MediaType.APPLICATION_JSON).content("""
                {"username": "piet"}
                """).with(jwtWithUser(USER_ID, "jan", "jan@kdg.be"))).andExpect(status().isOk());
    }

    @Test
    void addFriend_withoutJwtShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/friends").with(jwt().jwt(jwt -> jwt.claim("sub", USER_ID).claim("preferred_username", "jan")))).andExpect(status().isOk());

        mockMvc.perform(get("/api/friends")).andExpect(status().isUnauthorized());
    }

    @Test
    void acceptFriend_callsServiceWithCorrectId() throws Exception {
        UUID requestId = UUID.randomUUID();

        mockMvc.perform(post("/api/friends/accept/{id}", requestId).with(jwtWithUser(USER_ID, "jan", "jan@kdg.be"))).andExpect(status().isOk());
    }

    @Test
    void acceptFriend_withoutJwtShouldReturn401() throws Exception {
        mockMvc.perform(post("/api/friends/accept/{id}", UUID.randomUUID()).with(csrf())).andExpect(status().isUnauthorized());
    }

    @Test
    void rejectFriend_callsServiceWithCorrectId() throws Exception {
        UUID requestId = UUID.randomUUID();

        mockMvc.perform(post("/api/friends/reject/{id}", requestId).with(jwtWithUser(USER_ID, "jan", "jan@kdg.be"))).andExpect(status().isOk());
    }

    @Test
    void rejectFriend_withoutJwtShouldReturn401() throws Exception {
        mockMvc.perform(post("/api/friends/reject/{id}", UUID.randomUUID()).with(csrf())).andExpect(status().isUnauthorized());
    }

    @Test
    void cancelFriendRequest_callsServiceWithCorrectId() throws Exception {
        UUID requestId = UUID.randomUUID();

        mockMvc.perform(post("/api/friends/cancel/{id}", requestId).with(jwtWithUser(USER_ID, "jan", "jan@kdg.be"))).andExpect(status().isOk());
    }

    @Test
    void cancelFriendRequest_withoutJwtShouldReturn401() throws Exception {
        mockMvc.perform(post("/api/friends/cancel/{id}", UUID.randomUUID()).with(csrf())).andExpect(status().isUnauthorized());
    }

    @Test
    void removeFriend_callsServiceWithCorrectId() throws Exception {
        UUID friendId = UUID.randomUUID();

        mockMvc.perform(delete("/api/friends/remove/{id}", friendId).with(jwtWithUser(USER_ID, "jan", "jan@kdg.be"))).andExpect(status().isOk());
    }

    @Test
    void removeFriend_withoutJwtShouldReturn401() throws Exception {
        mockMvc.perform(delete("/api/friends/remove/{id}", UUID.randomUUID()).with(csrf())).andExpect(status().isUnauthorized());
    }

    @Test
    void invalidUuidInPathReturns400() throws Exception {
        mockMvc.perform(post("/api/friends/accept/not-a-uuid").with(jwtWithUser(USER_ID, "jan", "jan@kdg.be"))).andExpect(status().isInternalServerError());
    }
}