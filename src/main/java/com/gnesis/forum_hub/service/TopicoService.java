package com.gnesis.forum_hub.service;

import com.gnesis.forum_hub.dto.TopicoAtualizacaoRequisicao;
import com.gnesis.forum_hub.dto.TopicoCriarRequisicao;
import com.gnesis.forum_hub.model.Curso;
import com.gnesis.forum_hub.model.Topico;
import com.gnesis.forum_hub.model.Usuario;
import com.gnesis.forum_hub.repository.CursoRepository;
import com.gnesis.forum_hub.repository.TopicoRepository;
import com.gnesis.forum_hub.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicoService {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Transactional
    public Topico criarTopico(TopicoCriarRequisicao requisicao, String username) {
        // Validar tópico duplicado
        if (topicoRepository.existsByTituloAndMensagem(requisicao.titulo(), requisicao.mensagem())) {
            throw new IllegalArgumentException("Já existe um tópico com este título e mensagem");
        }

        // Buscar usuário autor
        Usuario autor = usuarioRepository.findUserByLogin(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        // Buscar ou criar curso
        Curso curso = cursoRepository.findByNomeIgnoreCase(requisicao.nomeCurso())
                .orElseGet(() -> {
                    Curso novoCurso = new Curso();
                    novoCurso.setNome(requisicao.nomeCurso());
                    novoCurso.setCategoria("Programação");
                    return cursoRepository.save(novoCurso);
                });

        // Criar tópico
        Topico topic = new Topico();
        topic.setTitulo(requisicao.titulo());
        topic.setMensagem(requisicao.mensagem());
        topic.setAutor(autor);
        topic.setCurso(curso);

        return topicoRepository.save(topic);
    }

    public List<Topico> getAllTopics() {
        return topicoRepository.findAll();
    }

    public Topico getTopicById(Long id) {
        return topicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tópico não encontrado"));
    }

    @Transactional
    public Topico updateTopic(Long id, TopicoAtualizacaoRequisicao requisicao, String username) {
        Topico topic = topicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tópico não encontrado"));

        // Verificar se usuário é o autor
        if (!topic.getAutor().getLogin().equals(username)) {
            throw new IllegalArgumentException("Apenas o autor pode atualizar o tópico");
        }

        // Atualizar campos
        if (requisicao.titulo() != null && !requisicao.titulo().isBlank()) {
            topic.setTitulo(requisicao.titulo());
        }

        if (requisicao.mensagem() != null && !requisicao.mensagem().isBlank()) {
            topic.setMensagem(requisicao.mensagem());
        }

        if (requisicao.status() != null && !requisicao.status().isBlank()) {
            try {
                Topico.StatusTopico newStatus = Topico.StatusTopico.valueOf(requisicao.status().toUpperCase());
                topic.setStatus(newStatus);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Status inválido: " + requisicao.status());
            }
        }

        return topicoRepository.save(topic);
    }

    @Transactional
    public void deleteTopic(Long id, String username) {
        Topico topic = topicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tópico não encontrado"));

        // Verificar se usuário é o autor
        if (!topic.getAutor().getLogin().equals(username)) {
            throw new IllegalArgumentException("Apenas o autor pode deletar o tópico");
        }

        topicoRepository.delete(topic);
    }
}
