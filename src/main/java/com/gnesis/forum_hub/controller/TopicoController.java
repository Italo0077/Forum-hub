package com.gnesis.forum_hub.controller;

import com.gnesis.forum_hub.dto.TopicoAtualizacaoRequisicao;
import com.gnesis.forum_hub.dto.TopicoCriarRequisicao;
import com.gnesis.forum_hub.dto.TopicoResponse;
import com.gnesis.forum_hub.model.Topico;
import com.gnesis.forum_hub.service.TopicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/topicos")
public class TopicoController {

    @Autowired
    private TopicoService topicoService;


    @PostMapping
    public ResponseEntity<TopicoResponse> createTopic(
            @RequestBody @Valid TopicoCriarRequisicao requisicao,
            Authentication authentication) {

        String username = authentication.getName();
        Topico topico = topicoService.criarTopico(requisicao, username);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new TopicoResponse(topico));
    }
    @GetMapping
    public ResponseEntity<List<TopicoResponse>> getAllTopics() {
        List<Topico> topicos = topicoService.getAllTopics();

        List<TopicoResponse> response = topicos.stream()
                .map(TopicoResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<TopicoResponse> getTopicById(@PathVariable Long id) {
        Topico topico = topicoService.getTopicById(id);
        return ResponseEntity.ok(new TopicoResponse(topico));
    }


    @PutMapping("/{id}")
    public ResponseEntity<TopicoResponse> updateTopic(
            @PathVariable Long id,
            @RequestBody TopicoAtualizacaoRequisicao requisicao,
            Authentication autenticacao) {

        String username = autenticacao.getName();
        Topico topico = topicoService.updateTopic(id, requisicao, username);

        return ResponseEntity.ok(new TopicoResponse(topico));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopic(
            @PathVariable Long id,
            Authentication authentication) {

        String username = authentication.getName();
        topicoService.deleteTopic(id, username);

        return ResponseEntity.ok().build();
    }
}
