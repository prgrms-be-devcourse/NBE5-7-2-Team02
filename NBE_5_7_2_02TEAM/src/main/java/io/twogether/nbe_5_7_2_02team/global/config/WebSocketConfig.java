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
        registry.addEndpoint("/ws/chatroom")
            .setAllowedOriginPatterns("*")
            .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = accessor.getFirstNativeHeader("Authorization");
                    if (token != null && token.startsWith("Bearer ")) {
                        token = token.substring(7);
                        try {
                            // 토큰 검증
                            if (jwtTokenProvider.validate(token)) {
                                // 토큰에서 사용자 정보 추출
                                TokenBody tokenBody = jwtTokenProvider.parseJwt(token);

                                // 인증 객체 생성 - Principal로 TokenBody 사용
                                Authentication authentication = new UsernamePasswordAuthenticationToken(
                                    tokenBody, // Principal: TokenBody 객체 자체를 사용
                                    null,      // Credentials
                                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + tokenBody.getRole().name())) // Authorities
                                );

                                // SecurityContextHolder에 인증 정보 저장 (선택적이며, Principal 주입을 우선시 할 경우 주석 처리)
                                // SecurityContextHolder.getContext().setAuthentication(authentication);
                                
                                // WebSocket 세션에 사용자 정보 설정 (이것이 @MessageMapping 메소드로 Principal을 주입하는 핵심)
                                accessor.setUser(authentication);
//                                System.out.println("====================
                            }
                        } catch (ErrorException e) {
                            // 토큰 검증 실패 시 연결 거부
                            // SecurityContextHolder.clearContext(); // 실패 시 컨텍스트 정리 (선택적)
                            throw new ErrorException(ErrorCode.INVALID_ACCESS_TOKEN);
                        }
                    } else {
                        // 토큰이 없는 경우 연결 거부
                        // SecurityContextHolder.clearContext(); // 실패 시 컨텍스트 정리 (선택적)
                        throw new ErrorException(ErrorCode.INVALID_ACCESS_TOKEN);
                    }
                } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                    // DISCONNECT 시 SecurityContext 정리 (선택적, Stateless 환경에서는 크게 중요하지 않을 수 있음)
                    // SecurityContextHolder.clearContext();
                }
                return message;
            }
        });
    }
}
