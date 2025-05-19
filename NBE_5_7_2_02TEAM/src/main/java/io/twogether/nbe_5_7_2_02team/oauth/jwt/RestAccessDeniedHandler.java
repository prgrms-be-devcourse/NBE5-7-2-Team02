// 관리자 기능 추가시 필요

//package io.twogether.nbe_5_7_2_02team.oauth.jwt;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.web.access.AccessDeniedHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//public class RestAccessDeniedHandler implements AccessDeniedHandler {
//
//    @Override
//    public void handle(HttpServletRequest request,
//        HttpServletResponse response,
//        AccessDeniedException accessDeniedException) throws IOException {
//        response.setContentType("application/json;charset=UTF-8");
//        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
//        response.getWriter().write("{\"message\": \"접근 권한이 없습니다.\"}");
//    }
//}
