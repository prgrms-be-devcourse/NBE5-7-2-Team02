package io.twogether.nbe_5_7_2_02team.browser.template;

import com.github.database.rider.spring.api.DBRider;

import io.twogether.nbe_5_7_2_02team.browser.config.MockTestConfig;
import io.twogether.nbe_5_7_2_02team.global.annotation.FlywayReset;
import io.twogether.nbe_5_7_2_02team.oauth.jwt.JwtTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@DBRider
@FlywayReset
@SpringBootTest
@AutoConfigureMockMvc
@Import(MockTestConfig.class)
public abstract class BrowserTestTemplate {

    @Autowired public MockMvc mockMvc;
    @Autowired public JwtTokenProvider jwtTokenProvider;
}
