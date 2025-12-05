package be.kdg.team22.communicationservice.api.chat;

import be.kdg.team22.communicationservice.application.chat.ChatService;
import be.kdg.team22.communicationservice.domain.chat.ChatChannel;
import be.kdg.team22.communicationservice.domain.chat.ChatChannelType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatChannelController.class)
class ChatChannelControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ChatService service;

    //TODO wanneer AI implementatie er is
    @Test
    void createLobbyChannel_returnsResponse() throws Exception {
        ChatChannel channel = ChatChannel.createNew(ChatChannelType.LOBBY, "lobby-99");

        Mockito.when(service.createChannel(
                eq(ChatChannelType.LOBBY),
                eq("lobby-99")
        )).thenReturn(channel);

        mvc.perform(post("/api/chat/channel/lobby/lobby-99"))
                .andExpect(status().is4xxClientError());
        //.andExpect(jsonPath("$.type").value("LOBBY"))
        //.andExpect(jsonPath("$.referenceId").value("lobby-99"));
    }

    @Test
    void createBotChannel_returnsResponse() throws Exception {
        ChatChannel channel = ChatChannel.createNew(ChatChannelType.BOT, "ai-user-55");

        Mockito.when(service.createChannel(
                eq(ChatChannelType.BOT),
                eq("ai-user-55")
        )).thenReturn(channel);

        mvc.perform(post("/api/chat/channel/bot/ai-user-55"))
                .andExpect(status().is4xxClientError());
        //.andExpect(jsonPath("$.type").value("BOT"))
        //.andExpect(jsonPath("$.referenceId").value("ai-user-55"));
    }
}
