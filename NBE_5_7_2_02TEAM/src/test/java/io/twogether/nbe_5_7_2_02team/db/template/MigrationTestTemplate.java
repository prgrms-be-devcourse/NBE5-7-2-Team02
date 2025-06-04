package io.twogether.nbe_5_7_2_02team.db.template;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

@SpringBootTest
public abstract class MigrationTestTemplate {

    @Autowired protected DataSource dataSource;

    public void migrate(String targetVersion) {
        Flyway flyway = getFlyway(targetVersion);
        flyway.migrate();
    }

    public void cleanAndMigrate(String targetVersion) {
        Flyway flyway = getFlyway(targetVersion);
        flyway.clean();
        flyway.migrate();
    }

    private Flyway getFlyway(String targetVersion) {
        return Flyway.configure()
                .cleanDisabled(false)
                .dataSource(dataSource)
                .locations("classpath:db/migration/test")
                .target(targetVersion)
                .load();
    }
}
