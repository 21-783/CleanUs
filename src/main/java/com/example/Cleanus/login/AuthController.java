package com.example.Cleanus.login;

import com.example.Cleanus.user.User;
import com.example.Cleanus.user.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCrypt;
import jakarta.validation.constraints.Email;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController {

    private final MailService mailService;
    private final UserRepository userRepository;

    private final Random rnd = new Random();

    @Value("${app.auth.code-exp-min:10}") private int codeExpMin;
    @Value("${app.auth.verified_window-min:10}") private int verifiedWindowMin;

    @GetMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestParam @Email String email, HttpSession session) {
        String code = String.format("%06d", rnd.nextInt(1_000_000));
        Instant expiresAt = Instant.now().plus(codeExpMin, ChronoUnit.MINUTES);

        session.setAttribute("signup_email", email);
        session.setAttribute("signup_code", code);
        session.setAttribute("signup_expires", expiresAt.toString());

        try {
            mailService.sendCodeMail(email, code, codeExpMin);
        }catch(Exception e) {
            return ResponseEntity.status(500).body("메일 발송 실패: " + e.getMessage());
        }
        return ResponseEntity.ok("인증코드를 전송완료했습니다.");
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestParam @Email String email,
                                        @RequestParam String code,
                                        HttpSession session) {
        String sEmail = (String) session.getAttribute("signup_email");
        String sCode = (String) session.getAttribute("signup_code");
        String sExpires = (String) session.getAttribute("signup_expires");

        if (sEmail == null || sCode == null || sExpires == null) {
            return ResponseEntity.badRequest().body("인증코드가 발급되지 않았습니다.");
        }
        if (!sEmail.equalsIgnoreCase(email)) {
            return ResponseEntity.badRequest().body("이메일 불일치");
        }
        if (!sCode.equals(code)) {
            return ResponseEntity.badRequest().body("코드 불일치");
        }
        if (Instant.now().isAfter(Instant.parse(sExpires))) {
            return ResponseEntity.badRequest().body("코드 만료");
        }

        session.setAttribute("verified_email", email);
        session.setAttribute("verified_until",
                Instant.now().plus(verifiedWindowMin, ChronoUnit.MINUTES).toString());

        session.removeAttribute("signup_code");
        session.removeAttribute("signup_expires");

        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam @Email String email,
                                      @RequestParam String password,
                                      HttpSession session) {
        String vEmail = (String) session.getAttribute("verified_email");
        String vUntil = (String) session.getAttribute("verified_until");

        if (vEmail == null || vUntil == null || !vEmail.equalsIgnoreCase(email)) {
            return ResponseEntity.badRequest().body("이메일 인증이 필요합니다.");
        }
        if (Instant.now().isAfter(Instant.parse(vUntil))) {
            return ResponseEntity.badRequest().body("이메일 인증이 만료되었습니다.");
        }
        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body("이미 가입된 이메일입니다.");
        }

        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = User.builder()
                .email(email)
                .passwordHash(hash)
                .createdAt(Instant.now())
                .build();
        userRepository.save(user);

        session.removeAttribute("verified_email");
        session.removeAttribute("verified_until");

        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }
}
