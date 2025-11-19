package be.kdg.team22.socialservice.friends.api;

import be.kdg.team22.socialservice.friends.api.models.FriendModel;
import be.kdg.team22.socialservice.friends.api.models.FriendsOverviewModel;
import be.kdg.team22.socialservice.friends.application.FriendService;
import be.kdg.team22.socialservice.friends.domain.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static be.kdg.team22.socialservice.testutils.JwtTestUtils.jwtWithUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
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
    void getFriends_returnsOverview() throws Exception {
        UUID friendId = UUID.randomUUID();
        UUID incomingId = UUID.randomUUID();
        UUID outgoingId = UUID.randomUUID();

        FriendsOverviewModel overview = new FriendsOverviewModel(
                List.of(new FriendModel(friendId.toString(), "friend", "ACCEPTED")),
                List.of(new FriendModel(incomingId.toString(), "incoming", "PENDING")),
                List.of(new FriendModel(outgoingId.toString(), "outgoing", "PENDING"))
        );

        when(friendService.getOverview(any(UserId.class))).thenReturn(overview);

        mockMvc.perform(
                        get("/api/friends").with(jwtWithUser(USER_ID, "jan", "jan@kdg.be"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.friends[0].userId").value(friendId.toString()))
                .andExpect(jsonPath("$.friends[0].username").value("friend"))
                .andExpect(jsonPath("$.incoming[0].username").value("incoming"))
                .andExpect(jsonPath("$.outgoing[0].username").value("outgoing"));
    }

    @Test
    void getFriends_withoutJwtShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/friends"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void addFriend_callsServiceWithCorrectUsername() throws Exception {
        mockMvc.perform(
                        post("/api/friends/add")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {"username": "piet"}
                                        """)
                                .with(jwtWithUser(USER_ID, "jan", "jan@kdg.be"))
                )
                .andExpect(status().isOk());
    }

    @Test
    void addFriend_withoutJwtShouldReturn401() throws Exception {
        mockMvc.perform(
                post("/api/friends/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "piet"}
                                """)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    void acceptFriend_callsServiceWithCorrectId() throws Exception {
        UUID requestId = UUID.randomUUID();

        mockMvc.perform(
                        post("/api/friends/accept/{id}", requestId)
                                .with(jwtWithUser(USER_ID, "jan", "jan@kdg.be"))
                )
                .andExpect(status().isOk());
    }

    @Test
    void acceptFriend_withoutJwtShouldReturn401() throws Exception {
        mockMvc.perform(post("/api/friends/accept/{id}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectFriend_callsServiceWithCorrectId() throws Exception {
        UUID requestId = UUID.randomUUID();

        mockMvc.perform(
                        post("/api/friends/reject/{id}", requestId)
                                .with(jwtWithUser(USER_ID, "jan", "jan@kdg.be"))
                )
                .andExpect(status().isOk());
    }

    @Test
    void rejectFriend_withoutJwtShouldReturn401() throws Exception {
        mockMvc.perform(post("/api/friends/reject/{id}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cancelFriendRequest_callsServiceWithCorrectId() throws Exception {
        UUID requestId = UUID.randomUUID();

        mockMvc.perform(
                        post("/api/friends/cancel/{id}", requestId)
                                .with(jwtWithUser(USER_ID, "jan", "jan@kdg.be"))
                )
                .andExpect(status().isOk());
    }

    @Test
    void cancelFriendRequest_withoutJwtShouldReturn401() throws Exception {
        mockMvc.perform(post("/api/friends/cancel/{id}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void removeFriend_callsServiceWithCorrectId() throws Exception {
        UUID friendId = UUID.randomUUID();

        mockMvc.perform(
                        delete("/api/friends/remove/{id}", friendId)
                                .with(jwtWithUser(USER_ID, "jan", "jan@kdg.be"))
                )
                .andExpect(status().isOk());
    }

    @Test
    void removeFriend_withoutJwtShouldReturn401() throws Exception {
        mockMvc.perform(delete("/api/friends/remove/{id}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidUuidInPathReturns400() throws Exception {
        mockMvc.perform(
                        post("/api/friends/accept/not-a-uuid")
                                .with(jwtWithUser(USER_ID, "jan", "jan@kdg.be"))
                )
                .andExpect(status().isBadRequest());
    }
}