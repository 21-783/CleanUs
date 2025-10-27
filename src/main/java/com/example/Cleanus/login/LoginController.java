package com.example.Cleanus.login;

import com.example.Cleanus.user.User;
import com.example.Cleanus.user.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email,
                                   @RequestParam String password,
                                   HttpSession session) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("존재하지 않는 이메일입니다.");
        }

        User user = userOpt.get();
        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
        }

        session.setAttribute("loginUser", user.getId());
        return ResponseEntity.ok("로그인 성공");
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        Long userId = (Long) session.getAttribute("loginUser");
        if (userId == null) {
            return ResponseEntity.status(401).body("로그인 필요");
        }
        return ResponseEntity.ok("현재 로그인된 사용자 ID = " + userId);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("로그아웃 완료");
    }
}
