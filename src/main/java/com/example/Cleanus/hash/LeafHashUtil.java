package com.example.Cleanus.hash;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bouncycastle.jcajce.provider.digest.Keccak;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class LeafHashUtil {

    // JSON의 키를 알파벳 순서로 일정하게 만들어, 같은 데이터 -> 같은 해시가 나오도록 한다.
    private static final ObjectMapper M = new ObjectMapper()
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);


    // 거래내역 데이터를 표준 JSON 문자열(canonical JSON)형태로 변환
    // 변환된 JSON 문자열은 이후 keccak-256 해시를 계산하는데에 사용.
    public static String canonicalLeafJson(String fintechUseNum, Instant txAt,
                                           String shopName, BigDecimal amount, String providerTxId) {
        ObjectNode n = M.createObjectNode();
        n.put("amount", amount.setScale(2).toString());
        n.put("fintechUseNum", fintechUseNum);
        n.put("providerTxId", providerTxId);
        n.put("shopName", shopName);
        n.put("txAt", DateTimeFormatter.ISO_INSTANT.format(txAt));
        return n.toString();
    }

    // keccak256 해시 계산기
    public static String keccak256Hex(String s) {
        var d = new Keccak.Digest256();
        d.update(s.getBytes(StandardCharsets.UTF_8));
        byte[] out = d.digest();
        StringBuilder sb = new StringBuilder("0x");
        for (byte b : out) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
