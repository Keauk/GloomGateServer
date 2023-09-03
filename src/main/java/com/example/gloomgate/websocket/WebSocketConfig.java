package com.example.gloomgate.websocket;

import com.example.gloomgate.security.JwtUtil;
import com.example.gloomgate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Autowired
    public WebSocketConfig(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");  // Prefix for server messages
        config.setApplicationDestinationPrefixes("/app");  // Prefix for client messages
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket-endpoint")
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    @CrossOrigin
                    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                        // Retrieve the JWT token from the request parameters and authenticate the user
                        MultiValueMap<String, String> params = UriComponentsBuilder.fromUriString(request.getURI().toString()).build().getQueryParams();
                        String token = params.getFirst("token");
                        String username = jwtUtil.extractUsername(token);

                        if (username != null) {
                            UserDetails userDetails = userService.loadUserByUsername(username);
                            if (jwtUtil.validateToken(token, userDetails)) {
                                return new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());
                            }
                        }
                        return null; // Deny the WebSocket connection if the token is invalid
                    }
                })
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}