package org.maxur.jj.orm;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static java.sql.ResultSet.CONCUR_READ_ONLY;
import static java.sql.ResultSet.TYPE_FORWARD_ONLY;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ROMapperTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private java.sql.Connection connection;

    @Mock
    private java.sql.Statement statement;

    @Mock
    private java.sql.ResultSet resultSet;

    @Mock
    private java.sql.ResultSetMetaData metaData;

    @Before
    public void setUp() throws Exception {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement(TYPE_FORWARD_ONLY, CONCUR_READ_ONLY)).thenReturn(statement);
        when(statement.getResultSet()).thenReturn(resultSet);
        when(resultSet.getMetaData()).thenReturn(metaData);
    }

    @Entity
    static class FakeEntity1 {

        @Column(name = "user_name")
        private final String name = null;

        @Column(name = "user_psw")
        private final String password = null;

        public String getName() {
            return name;
        }

        public String getPassword() {
            return password;
        }
    }

    @Test
    public void shouldBeReturnListOfObjectWithMapByColumnName() throws Exception {
        when(resultSet.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        when(metaData.getColumnCount()).thenReturn(2);
        when(metaData.getColumnName(1)).thenReturn("user_name");
        when(metaData.getColumnName(2)).thenReturn("user_psw");
        when(resultSet.getObject(1)).thenReturn("name0").thenReturn("name1");
        when(resultSet.getObject(2)).thenReturn("p0").thenReturn("p1");

        try (
                Connection connection = getConnection();
                Statement statement = connection.createStatement(TYPE_FORWARD_ONLY, CONCUR_READ_ONLY)
        ) {
            statement.executeQuery("SELECT user_name, user_psw  FROM FAKE");  // DEMO Query
            try (ResultSet rs = statement.getResultSet()) {
                List<FakeEntity1> list = ROMapper.map(rs, FakeEntity1.class);
                assertEquals(2, list.size());
                for (int i = 0; i < list.size(); i++) {
                    FakeEntity1 object = list.get(i);
                    assertEquals("name" + i, object.getName());
                    assertEquals("p" + i, object.getPassword());
                }
            }
        }
    }

    @Entity
    static class FakeEntity2 {

        @Column(index = 0)
        private final String name = null;

        @Column(index = 1)
        private final String password = null;

        public String getName() {
            return name;
        }

        public String getPassword() {
            return password;
        }
    }



    @Test
    public void shouldBeReturnListOfObjectWithMapByColumnNumber() throws Exception {
        when(resultSet.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        when(metaData.getColumnCount()).thenReturn(2);
        when(metaData.getColumnName(1)).thenReturn("user_name");
        when(metaData.getColumnName(2)).thenReturn("user_psw");
        when(resultSet.getObject(1)).thenReturn("name0").thenReturn("name1");
        when(resultSet.getObject(2)).thenReturn("p0").thenReturn("p1");

        try (
                Connection connection = getConnection();
                Statement statement = connection.createStatement(TYPE_FORWARD_ONLY, CONCUR_READ_ONLY)
        ) {
            statement.executeQuery("SELECT user_name, user_psw  FROM FAKE");  // DEMO Query
            try (ResultSet rs = statement.getResultSet()) {
                List<FakeEntity2> list = ROMapper.map(rs, FakeEntity2.class);
                assertEquals(2, list.size());
                for (int i = 0; i < list.size(); i++) {
                    FakeEntity2 object = list.get(i);
                    assertEquals("name" + i, object.getName());
                    assertEquals("p" + i, object.getPassword());
                }
            }
        }
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}