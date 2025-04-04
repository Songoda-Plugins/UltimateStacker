package com.songoda.ultimatestacker.database.migrations;

import com.songoda.core.database.DataMigration;
import com.songoda.core.database.DatabaseConnector;
import com.songoda.core.database.MySQLConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class _2_EntityStacks extends DataMigration {

    public _2_EntityStacks() {
        super(2);
    }

    @Override
    public void migrate(Connection connection, String tablePrefix) throws SQLException {
        // Create host entities table
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + "host_entities (" +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    "uuid VARCHAR(36) NOT NULL," +
                    "create_duplicates INTEGER NOT NULL DEFAULT 0" +
                    ")");
        }
    }
}
