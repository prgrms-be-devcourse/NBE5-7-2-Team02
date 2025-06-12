package io.twogether.nbe_5_7_2_02team.global.listener;

import org.flywaydb.core.Flyway;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class FlywayResetTestExecutionListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        ApplicationContext ac = testContext.getApplicationContext();
        Flyway flyway = ac.getBean(Flyway.class);
        flyway.clean();
        flyway.migrate();
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        ApplicationContext ac = testContext.getApplicationContext();
        Flyway flyway = ac.getBean(Flyway.class);
        flyway.clean();
    }
}
