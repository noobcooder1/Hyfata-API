package kr.hyfata.rest.api.service.agora;

import kr.hyfata.rest.api.dto.agora.chat.ChatResponse;
import kr.hyfata.rest.api.dto.agora.chat.ChatListResponse;
import kr.hyfata.rest.api.dto.agora.chat.CreateChatRequest;
import kr.hyfata.rest.api.dto.agora.chat.MessageDto;
import kr.hyfata.rest.api.dto.agora.chat.SendMessageRequest;

import java.util.List;

public interface AgoraChatService {

    List<ChatListResponse> getChatList(String userEmail);

    ChatResponse createChat(String userEmail, CreateChatRequest request);

    ChatResponse getChatDetail(String userEmail, Long chatId);

    List<MessageDto> getMessages(String userEmail, Long chatId, Long cursor, int limit);

    MessageDto sendMessage(String userEmail, Long chatId, SendMessageRequest request);

    String deleteMessage(String userEmail, Long chatId, Long messageId);

    String markChatAsRead(String userEmail, Long chatId);
}
