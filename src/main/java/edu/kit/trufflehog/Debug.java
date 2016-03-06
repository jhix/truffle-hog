package edu.kit.trufflehog;

import edu.kit.trufflehog.model.FileSystem;
import edu.kit.trufflehog.model.configdata.FilterDataModel;
import edu.kit.trufflehog.model.filter.FilterInput;
import edu.kit.trufflehog.presenter.LoggedScheduledExecutor;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * </p>
 *
 * @author Julian Brendl
 * @version 1.0
 */
public class Debug {
    public static void main(String[] args) {
        FilterDataModel filterDataModel = new FilterDataModel(new FileSystem(), new LoggedScheduledExecutor(1));

        List<String> filterARules = new ArrayList<>();
        filterARules.add("Rule A: 1");
        filterARules.add("Rule A: 2");
        filterARules.add("Rule A: 3");
        FilterInput filterInput = new FilterInput("Filter A", filterARules, Color.black);

        filterDataModel.addFilterToDatabase(filterInput);

        filterDataModel.loadFilters();

        filterDataModel.removeFilterFromDatabase(filterInput);

        filterDataModel.loadFilters();

        //dbShit();
    }

    private static void dbShit() {
        Connection connection;
        Statement statement;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:./src/main/resources/db.sql");
            System.out.println("Opened database successfully");

            String sql;
            statement = connection.createStatement();
            sql = "CREATE TABLE FILTER" +
                    "(ID  INTEGER PRIMARY KEY     AUTOINCREMENT ," +
                    " ACTIVE          BOOLEAN BOOLEAN DEFAULT TRUE, " +
                    " COLOR_R         INTEGER     NOT NULL," +
                    " COLOR_G         INTEGER     NOT NULL, " +
                    " COLOR_B         INTEGER     NOT NULL, " +
                    " COLOR_A         INTEGER     NOT NULL)";
            statement.executeUpdate(sql);

            statement = connection.createStatement();
             sql = "CREATE TABLE RULES" +
                    "(ID  INTEGER PRIMARY KEY     NOT NULL," +
                    " FILTER_ID       INTEGER     NOT NULL," +
                    " RULE            TEXT    NOT NULL)";
            statement.executeUpdate(sql);

            sql = "INSERT INTO FILTER(COLOR_R,COLOR_G,COLOR_B,COLOR_A)" +
                         "VALUES (0, 0, 0, 255);";
            statement.executeUpdate(sql);

            sql = "INSERT INTO FILTER(COLOR_R,COLOR_G,COLOR_B,COLOR_A)" +
                    "VALUES (10, 10, 10, 255);";
            statement.executeUpdate(sql);

            sql = "INSERT INTO FILTER(COLOR_R,COLOR_G,COLOR_B,COLOR_A)" +
                    "VALUES (20, 20, 20, 255);";
            statement.executeUpdate(sql);

            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM FILTER;");
            while ( rs.next() ) {
                int id = rs.getInt("id");
                boolean active = rs.getBoolean("active");
                int color_r  = rs.getInt("color_r");
                int color_g  = rs.getInt("color_g");
                int color_b  = rs.getInt("color_b");
                int color_a  = rs.getInt("color_a");
                System.out.println("ID = " + id);
                System.out.println("ACTIVE = " + active);
                System.out.println("COLOR_R = " + color_r);
                System.out.println("COLOR_G = " + color_g);
                System.out.println("COLOR_B = " + color_b);
                System.out.println("COLOR_A = " + color_a);
                System.out.println();
            }

            statement.close();
            connection.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }
}
