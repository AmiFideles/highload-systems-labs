package ru.itmo.service.user;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class IntegrationEnvironmentTest extends IntegrationEnvironment {
    private static final Set<String> tablesSet = Set.of(
            "databasechangelog",
            "databasechangeloglock",
            "category",
            "category_listing",
            "deal",
            "deal_review",
            "listing",
            "saved_listing",
            "seller_review",
            "users"
    );

    @Test
    @SneakyThrows
    void connection__getMetaData_returnCorrectSchema() {
        Connection connection = getConnection();
        List<String> tableNames = getTablesNames(connection);
        for (String name : tableNames) {
            assertThat(tablesSet).contains(name);
        }
    }

    @SneakyThrows
    private List<String> getTablesNames(Connection connection) {
        List<String> tablesNames = new ArrayList<>();
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet rs = metaData.getTables(null, "public", "%", new String[]{"TABLE"})) {
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                tablesNames.add(tableName);
            }
        }
        return tablesNames;
    }
}
