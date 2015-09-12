import java.sql.*;
import java.util.Random;

/**
 * Created by lionliliya on 09.09.15.
 */


public class Main {
    private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/flatsdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "your password";

    private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS Flats (" +
            "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
            "numb INT NOT NULL, " +
            "house INT NOT NULL, " +
            "street VARCHAR(60) NOT NULL, " +
            "roomNumb INT NOT NULL, " +
            "square DOUBLE(6,2) NOT NULL, " +
            "price DOUBLE(8,2))";

    private static Connection getDBConnection() {
        Connection dbConnection = null;

        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        try {
            dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
        return dbConnection;
    }

    public static void main(String[] args) {

        Connection connection = getDBConnection();
        if (connection == null) {
            System.out.println("Error!Connection hasn't been created");
        }

        try {
            try {
                //Создали таблицу
                Statement st = connection.createStatement();
                try {
                    st.execute(CREATE_TABLE_SQL);
                } finally {
                    if (st != null) st.close();
                }

                // Предвыполнение...заполняем тадлицу квартирами
                connection.setAutoCommit(false);
                PreparedStatement preparedStatement = null;
                try {
                    preparedStatement = connection.prepareStatement
                            ("INSERT INTO Flats (numb, house, street, roomNumb, square, price) VALUES (?, ?, ?, ?, ?, ?)");
                    Random rn = new Random();
                    try {
                        for (int i = 0; i < 10; i++) {
                            preparedStatement.setInt(1, 10 + i);
                            preparedStatement.setInt(2, 12);
                            preparedStatement.setString(3, "Rusanivska");
                            preparedStatement.setInt(4, (1 + rn.nextInt(4)));
                            preparedStatement.setDouble(5, (250 - 17) * rn.nextDouble() + 17);
                            preparedStatement.setDouble(6, (120000 - 40000) * rn.nextDouble() + 40000);

                            preparedStatement.executeUpdate();

                        }
                    } finally {
                        connection.commit();
                        if (preparedStatement != null) preparedStatement.close();
                        System.out.println("Flats table created and  filled up");
                    }

                } catch (Exception ex) {
                    connection.rollback();
                }
                connection.setAutoCommit(true);

                // Запрос № 1 выводим информацию о всех квартирах
                preparedStatement = connection.prepareStatement("SELECT * FROM Flats");
                try {
                    ResultSet resultSet = preparedStatement.executeQuery();
                    try {
                        ResultSetMetaData MetaData = resultSet.getMetaData();
                        for (int i = 1; i <= MetaData.getColumnCount(); i++)
                            System.out.print(MetaData.getColumnName(i) + "\t\t");
                        System.out.println();

                        while (resultSet.next()) {
                            for (int i = 1; i <= MetaData.getColumnCount(); i++) {
                                System.out.print(resultSet.getString(i) + "\t\t");
                            }
                            System.out.println();
                        }
                    } finally {
                        resultSet.close();
                    }

                } finally {
                    if (preparedStatement != null) preparedStatement.close();
                }

                //Запрос №2
                System.out.println("------------------------------------");
                st = connection.createStatement();
                try { // finally if st!=null st.close()
                    ResultSet resultSet = st.executeQuery("SELECT id, numb, house FROM Flats WHERE price <= '80000'");
                    try {
                        ResultSetMetaData metaData = resultSet.getMetaData();
                        for (int i = 1; i <= metaData.getColumnCount(); i++) {
                            System.out.print(metaData.getColumnName(i) + "\t\t");
                        }
                        System.out.println();

                        while (resultSet.next()) {
                            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                                System.out.print(resultSet.getString(i) + "\t\t");
                            }
                            System.out.println();


                        }
                    } finally {
                        resultSet.close();
                    }

                } finally {
                    if (st != null) st.close();
                }

            } finally {
                connection.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
