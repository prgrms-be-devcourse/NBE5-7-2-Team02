package io.twogether.nbe_5_7_2_02team.global.annotation;

import io.twogether.nbe_5_7_2_02team.global.listener.FlywayResetTestExecutionListener;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@TestExecutionListeners(
    value = {FlywayResetTestExecutionListener.class},
    mergeMode = MergeMode.MERGE_WITH_DEFAULTS
)
public @interface FlywayReset {
}
