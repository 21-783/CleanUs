package com.example.cleanus_mockapi.common;

import java.time.Instant;

public record ApiError (
        String code,
        String message,
        String requestId,
        Instant timestamp
){}
