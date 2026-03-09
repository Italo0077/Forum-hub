package com.gnesis.forum_hub.dto;

import jakarta.validation.constraints.NotBlank;

public record TopicoCriarRequisicao(
        @NotBlank(message = "Título é obrigatório")
        String titulo,

        @NotBlank(message = "Mensagem é obrigatória")
        String mensagem,

        @NotBlank(message = "Nome do curso é obrigatório")
        String nomeCurso
) {}

