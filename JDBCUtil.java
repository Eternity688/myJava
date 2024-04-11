
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCUtil {
    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/school?useUnicode=true&characterEncoding=utf8&rewriteBatchStatements=true";

        return DriverManager.getConnection(url,"root","000000");
    }

    public static void closeConnection(Connection conn) throws SQLException {
        if (conn.getAutoCommit())
            conn.close();
    }

    public static int executeUpdate(String sql, Object... params) throws ClassNotFoundException, SQLException {
        Connection conn = getConnection();
        PreparedStatement prep = conn.prepareStatement(sql);
        if (params.length != 0 && params != null) {
            for (int i = 0; i < params.length; i++) {
                prep.setObject(i + 1, params[i]);
            }
        }
        int rows = prep.executeUpdate();
        prep.close();
        return rows;
    }

    public static <T> List<T> executeQuery(Class<T> clazz, String sql, Object... params) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        Connection conn = getConnection();
        PreparedStatement prep = conn.prepareStatement(sql);
        if (params.length != 0 && params != null) {
            for (int i = 0; i < params.length; i++) {
                prep.setObject(i + 1, params[i]);
            }
        }
        ResultSet rs = prep.executeQuery();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<T> list = new ArrayList<>();
        while (rs.next()) {
            T t = clazz.newInstance();
            for (int i = 1; i <= columnCount; i++) {
                String name = metaData.getColumnLabel(i);
                Object o = rs.getObject(i);

                Field field = clazz.getDeclaredField(name);

                field.setAccessible(true);
                field.set(t, o);

            }
            list.add(t);
        }
        prep.close();
        rs.close();
        closeConnection(conn);
        return list;

    }
}
