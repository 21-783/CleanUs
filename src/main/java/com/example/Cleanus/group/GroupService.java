package com.example.Cleanus.group;

import com.example.Cleanus.ledger.Ledger;
import com.example.Cleanus.ledger.LedgerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final LedgerRepository ledgerRepository;

    @Transactional
    public Group createGroup(Long ownerUserId, String name, String rawPassword) {
        if (groupRepository.existsByName(name)) throw new IllegalStateException("이미 존재하는 그룹명입니다.");

        String pwHash = BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));

        Group group = Group.builder()
                .name(name)
                .passwordHash(pwHash)
                .ownerUserId(ownerUserId)
                .build();

        Group saved = groupRepository.save(group);

        Ledger ledger = Ledger.builder()
                .group(saved)
                .title(name + "의 장부")
                .build();
        ledgerRepository.save(ledger);

        saved.setLedger(ledger);
        return saved;
    }

    @Transactional
    public void changeGroupPassword(Long requesterUserId, Long groupId, String newRawPassword) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException(("해당 그룹을 찾을 수 없습니다.")));

        if(!group.getOwnerUserId().equals(requesterUserId)) {
            throw new SecurityException(("비밀번호 변경 권한이 없습니다."));
        }

        String newHash =  BCrypt.hashpw(newRawPassword, BCrypt.gensalt(12));
        group.setPasswordHash(newHash);
    }

    public boolean verifyGroupPassword(Long groupId, String rawPassword) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹을 찾을 수 없습니다."));
        return BCrypt.checkpw(rawPassword, group.getPasswordHash());
    }

    public Group getGroup(Long groupId){
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹을 찾을 수 없습니다."));
    }
}
