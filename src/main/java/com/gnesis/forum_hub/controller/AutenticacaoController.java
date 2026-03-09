package com.gnesis.forum_hub.controller;

import com.gnesis.forum_hub.dto.LoginRequisicao;
import com.gnesis.forum_hub.dto.RegistrarRequisicao;
import com.gnesis.forum_hub.dto.RegistrarResponse;
import com.gnesis.forum_hub.dto.TokenResponse;
import com.gnesis.forum_hub.model.Usuario;
import com.gnesis.forum_hub.repository.UsuarioRepository;
import com.gnesis.forum_hub.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequisicao request) {
        // Cria token de autenticação com login e senha
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(request.login(), request.senha());

        Authentication autenticacao = authenticationManager.authenticate(authToken);

        Usuario usuario = (Usuario) autenticacao.getPrincipal();
        String token = tokenService.generateToken(usuario);

        return ResponseEntity.ok(new TokenResponse(token));
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> register(@RequestBody @Valid RegistrarRequisicao request) {

        if (usuarioRepository.existsByLogin(request.login())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Login já está em uso");
        }


        Usuario novoUsuario = new Usuario();
        novoUsuario.setLogin(request.login());
        novoUsuario.setNome(request.nome());
        novoUsuario.setEmail(request.email());


        String hashedPassword = passwordEncoder.encode(request.senha());
        novoUsuario.setSenha(hashedPassword);


        Usuario savedUser = usuarioRepository.save(novoUsuario);


        RegistrarResponse response = new RegistrarResponse(
                savedUser.getId(),
                savedUser.getLogin(),
                savedUser.getNome(),
                savedUser.getEmail(),
                "Usuário cadastrado com sucesso!"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
