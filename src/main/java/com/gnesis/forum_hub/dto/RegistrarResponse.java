package com.gnesis.forum_hub.dto;

public record RegistrarResponse(
        Long id,
        String login,
        String nome,
        String email,
        String message
) {}