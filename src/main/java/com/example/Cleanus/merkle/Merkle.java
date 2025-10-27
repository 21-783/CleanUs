package com.example.Cleanus.merkle;

import org.bouncycastle.jcajce.provider.digest.Keccak;

import java.util.ArrayList;
import java.util.List;

// LeafHash 목록을 받아 Merkle tree의 Merkle Root를 계산하는 Util.
public class Merkle {

    // "0x" 형태로 시작하는 LeafHash들을 Keccak이라는 해시 함수에 입력값으로 넣기 위해 바이트(bytes[])형태로 바꾸는 함수.
    private static byte[] hexToBytes(String hex) {
        String s = hex.startsWith("0x") ? hex.substring(2) : hex;
        byte[] r = new byte[s.length() / 2];
        for (int i = 0; i<s.length(); i+=2) r[i/2] = (byte) Integer.parseInt(s.substring(i,i+2), 16);
        return r;
    }

    // 자식 노드 (Left node, Right node)를 사용해서 상위 노드(부모 노드)를 만드는 함수.
    private static String keccakHex(String l, String r) {
        var d = new Keccak.Digest256();
        d.update(hexToBytes(l)); d.update(hexToBytes(r));
        byte[] out = d.digest();
        StringBuilder sb = new StringBuilder("0x");
        for (byte b : out) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    // keccakHex()를 사용해서, LeafHash 리스트를 압축하는 과정. (노드가 홀수 개일때의,제어 포함)
    public static List<String> nextLevel(List<String> level) {
        List<String> out = new ArrayList<>();
        for (int i=0; i<level.size(); i+=2) {
            String L = level.get(i);
            String R = (i+1<level.size()) ? level.get(i+1) : L; // 노드 개수가 홀수 개일때는 자기 복제 방식을 사용.
            out.add(keccakHex(L, R));
        }
        return out;
    }

    // keccakHex(), nextLevel() 함수를 이용해서 결과적으로 Merkle Root를 구하는 함수.
    public static String root(List<String> leaves) {
        if (leaves.isEmpty()) throw new IllegalArgumentException("LeafHash 배열이 비어있습니다.");
        List<String> cur = new ArrayList<>(leaves);
        while (cur.size() > 1) cur = nextLevel(cur);
        return cur.get(0);
    }
}
