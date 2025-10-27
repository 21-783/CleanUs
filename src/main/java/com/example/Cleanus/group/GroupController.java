package com.example.Cleanus.group;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateGroupRequest req, HttpSession session){
        Long userId = (Long) session.getAttribute("loginUser");
        if (userId == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        Group g = groupService.createGroup(userId, req.getName(), req.getPassword());
        return ResponseEntity.ok(new GroupResponse(g.getId(), g.getName(), g.getOwnerUserId()));
    }

    @PatchMapping("/{groupId}/password")
    public ResponseEntity<?> changePassword(@PathVariable Long groupId,
                                            @Valid @RequestBody ChangePasswordRequest req,
                                            HttpSession session) {
        Long userId = (Long) session.getAttribute("loginUser");
        if (userId ==null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        groupService.changeGroupPassword(userId, groupId, req.getNewPassword());
        return ResponseEntity.ok("그룹 비밀번호가 정상적으로 변경되었습니다.");
    }

    @GetMapping("/{groupid}")
    public ResponseEntity<?> get(@PathVariable Long groupId, HttpSession session) {
        Long userId = (Long) session.getAttribute("loginUser");
        if (userId ==null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        Group g = groupService.getGroup(groupId);
        return ResponseEntity.ok(new GroupResponse(g.getId(), g.getName(), g.getOwnerUserId()));
    }

    // DTO
    @Data
    public static class CreateGroupRequest {
        @NotBlank private String name;
        @NotBlank private String password;
    }

    @Data
    public static class ChangePasswordRequest {
        @NotBlank private String newPassword;
    }

    @Data
    public static class GroupResponse {
        private final Long id;
        private final String name;
        private final Long ownerUserId;
    }
}
