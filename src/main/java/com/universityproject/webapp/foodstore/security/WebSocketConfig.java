package com.universityproject.webapp.foodstore.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Autowired
    private JwtUtil jwtUtil;


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");  // ⬅️ حتى تدعم /user/queue/
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user"); // ⬅️ مهم جداً
    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // نقطة الاتصال بين العميل والسيرفر عبر WebSocket
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // مسموح لأي دومين يتصل
                .withSockJS(); // يدعم fallbacks للمتصفحات التي لا تدعم WebSocket
    }
    @Bean
    public DefaultHandshakeHandler handshakeHandler() {
        return new DefaultHandshakeHandler() {
            @Override
            protected Principal determineUser(ServerHttpRequest request,
                                              WebSocketHandler wsHandler,
                                              Map<String, Object> attributes) {

                String token = request.getHeaders().getFirst("Authorization");
                if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7); // إزالة "Bearer "
                    try {
                        String email = jwtUtil.extractUsername(token);
                        return () -> email; // يعتبر الـ email هو اسم المستخدم
                    } catch (Exception e) {
                        System.out.println("❌ Failed to extract email from token: " + e.getMessage());
                        return null;
                    }
                }
                return null;
            }
        };
    }
}

