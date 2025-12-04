package kr.hyfata.rest.api.service.agora;

import kr.hyfata.rest.api.dto.agora.team.NoticeResponse;
import kr.hyfata.rest.api.dto.agora.team.CreateNoticeRequest;
import kr.hyfata.rest.api.dto.agora.team.UpdateNoticeRequest;

import java.util.List;

public interface AgoraTeamNoticeService {

    List<NoticeResponse> getNoticeList(String userEmail, Long teamId);

    NoticeResponse getNoticeDetail(String userEmail, Long teamId, Long noticeId);

    NoticeResponse createNotice(String userEmail, Long teamId, CreateNoticeRequest request);

    NoticeResponse updateNotice(String userEmail, Long teamId, Long noticeId, UpdateNoticeRequest request);

    String deleteNotice(String userEmail, Long teamId, Long noticeId);
}
