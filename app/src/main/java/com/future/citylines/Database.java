package com.future.citylines;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Created by future on 31/08/15.
 */
public class Database {

    public Database (){
    }
    public void insertData(String query) throws SQLException, ClassNotFoundException {
        Connection connection = null;

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://173.194.81.252/futuredb", "futureusrdb", "futureusrdb@%");
            Statement st = connection.createStatement();

            st.executeUpdate(query);
            connection.close();
    }

    public void insertMultipleRows(List<String> querys) throws SQLException, ClassNotFoundException {

        Statement stmt = null;
        Connection connection = null;

        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://173.194.81.252/futuredb", "futureusrdb", "futureusrdb@%");
            connection.setAutoCommit(false);
            stmt = connection.createStatement();

        for( String query: querys){
            stmt.addBatch(query);
        }
            int [] updateCounts = stmt.executeBatch();
            connection.commit();



    }

    public ResultSet executeQuery(String query){
        Connection connection = null;
        try {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://173.194.81.252/futuredb", "futureusrdb", "futureusrdb@%");
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    } catch (SQLException e) {
        e.printStackTrace();
    }
        ResultSet resultSet = null;
           try {
            Statement statement = connection.createStatement();
            Cursor cursor;
            resultSet = statement.executeQuery(query);

            //   connection.close();
           /* while(resultSet.next()){
                System.out.println("Id negocio: "+resultSet.getInt("id") + "Nombre Negocios: "+resultSet.getString("nombre"));

            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultSet;
    }
}
