package com.example.auth_service.dto;

public record TokenResponse(String accessToken, String refreshToken) {
}
