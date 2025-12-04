package kr.hyfata.rest.api.controller.agora;

import kr.hyfata.rest.api.dto.agora.FileUploadResponse;
import kr.hyfata.rest.api.service.agora.AgoraFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/agora/files")
@RequiredArgsConstructor
@Slf4j
public class AgoraFileController {

    private final AgoraFileService agoraFileService;

    /**
     * 파일 업로드
     * POST /api/agora/files/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(
            Authentication authentication,
            @RequestParam("file") MultipartFile file
    ) {
        String userEmail = authentication.getName();
        FileUploadResponse response = agoraFileService.uploadFile(userEmail, file);
        return ResponseEntity.ok(response);
    }

    /**
     * 이미지 업로드 (썸네일 생성)
     * POST /api/agora/files/upload-image
     */
    @PostMapping("/upload-image")
    public ResponseEntity<FileUploadResponse> uploadImage(
            Authentication authentication,
            @RequestParam("file") MultipartFile file
    ) {
        String userEmail = authentication.getName();
        FileUploadResponse response = agoraFileService.uploadImage(userEmail, file);
        return ResponseEntity.ok(response);
    }

    /**
     * 파일 메타데이터 조회
     * GET /api/agora/files/{fileId}
     */
    @GetMapping("/{fileId}")
    public ResponseEntity<FileUploadResponse> getFileMetadata(
            @PathVariable Long fileId
    ) {
        FileUploadResponse response = agoraFileService.getFileMetadata(fileId);
        return ResponseEntity.ok(response);
    }

    /**
     * 파일 다운로드
     * GET /api/agora/files/{fileId}/download
     */
    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long fileId
    ) throws MalformedURLException {
        FileUploadResponse fileInfo = agoraFileService.getFileMetadata(fileId);
        Path filePath = Paths.get(fileInfo.getFileName());
        Resource resource = new UrlResource(filePath.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileInfo.getOriginalName() + "\"")
                .body(resource);
    }

    /**
     * 파일 삭제
     * DELETE /api/agora/files/{fileId}
     */
    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(
            Authentication authentication,
            @PathVariable Long fileId
    ) {
        String userEmail = authentication.getName();
        String message = agoraFileService.deleteFile(userEmail, fileId);

        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }
}
