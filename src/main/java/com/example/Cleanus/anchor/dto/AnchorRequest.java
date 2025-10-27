package com.example.Cleanus.anchor.dto;

import java.util.List;

// ([POST] /api/anchor) 이 배치(Batch)에 포함된 LeafHash 들의 리스트를 포함한 request를 보내 앵커링 요청.
public record AnchorRequest(String batchId, List<String> leaves) {}
