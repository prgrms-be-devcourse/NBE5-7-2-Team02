package io.twogether.nbe_5_7_2_02team.global.config;

import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;
import io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode;
import io.twogether.nbe_5_7_2_02team.oauth.dto.common.TokenBody;
import io.twogether.nbe_5_7_2_02team.oauth.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Collections;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub");
        config.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chatroom").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(
                new ChannelInterceptor() {
                    @Override
                    public Message<?> preSend(Message<?> message, MessageChannel channel) {
                        StompHeaderAccessor accessor =
                                MessageHeaderAccessor.getAccessor(
                                        message, StompHeaderAccessor.class);

                        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                            String token = accessor.getFirstNativeHeader("Authorization");
                            if (token != null && token.startsWith("Bearer ")) {
                                token = token.substring(7);
                                try {
                                    if (jwtTokenProvider.validate(token)) {
                                        TokenBody tokenBody = jwtTokenProvider.parseJwt(token);

                                        Authentication authentication =
                                                new UsernamePasswordAuthenticationToken(
                                                        tokenBody,
                                                        null,
                                                        Collections.singletonList(
                                                                new SimpleGrantedAuthority(
                                                                        "ROLE_"
                                                                                + tokenBody
                                                                                        .getRole()
                                                                                        .name())));

                                        accessor.setUser(authentication);
                                    }
                                } catch (ErrorException e) {
                                    throw new ErrorException(ErrorCode.INVALID_ACCESS_TOKEN);
                                }
                            } else {
                                throw new ErrorException(ErrorCode.INVALID_ACCESS_TOKEN);
                            }
                        }
                        return message;
                    }
                });
    }
}
