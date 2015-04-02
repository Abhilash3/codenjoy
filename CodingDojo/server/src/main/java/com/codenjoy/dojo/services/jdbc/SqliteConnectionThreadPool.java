package com.codenjoy.dojo.services.jdbc;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Created by indigo on 12.03.2015.
 */
public class SqliteConnectionThreadPool extends ConnectionThreadPool {

    private static final int ONLY_ONE_CONNECTION = 1; // this is sqlite restriction
    private String databaseFile;

    public SqliteConnectionThreadPool(final String databaseFile, String... createTableSqls) {
        super(ONLY_ONE_CONNECTION, new Get() {
            @Override
            public Connection connection() throws Exception {
                Class.forName("org.sqlite.JDBC");
                Connection result = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
                return result;
            }
        });
        this.databaseFile = databaseFile;

        for (String sql : createTableSqls) {
            createDB(sql);
        }
    }

    private void createDB(final String sql) {
        run(new For<Void>() {
            @Override
            public Void run(Connection connection) {
                try (Statement statement = connection.createStatement()) {
                    statement.execute(sql);
                } catch (Exception e) {
                    throw new RuntimeException("Error creating db: " + databaseFile, e);
                }
                return null;
            }
        });
    }

    public void removeDatabase() {
        close();

        File file = new File(databaseFile);
        file.delete();
        if (file.exists()) {
            throw new RuntimeException("Cant remove DB " + file.getAbsolutePath());
        }
    }
}
