package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList; //import
import java.sql.Connection; //import
import java.sql.PreparedStatement; //import

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        // get all categories
        ArrayList<Category> categoryArrayList = new ArrayList<>(); //create the arraylist to hold the results
        String sqlQuery = "select * from categories"; //create a string to hold the sql query
        try(Connection connection = getConnection(); //create the try-with-resources and connect to the database
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery); //prepare the sql query for execution
            ResultSet resultSet = preparedStatement.executeQuery();){ //execute the query and get the results

            while(resultSet.next()){ //parse the results
                Category category = mapRow(resultSet); //use the mapRow function that already existed to create the catagory
                categoryArrayList.add(category); //add the result to the arraylist
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return categoryArrayList; //return the arraylist
    }

    @Override
    public Category getById(int categoryId)
    {
        // get category by id
        String sqlQuery = "select * from categories where category_id = ?"; //create a string to hold the sql query
        try(Connection connection = getConnection(); //create the try-with-resources and connect to the database
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);){ //prepare the sql query for execution
            preparedStatement.setInt(1, categoryId); //set parameter of sql query
            try (ResultSet resultSet = preparedStatement.executeQuery()) { //create result set inside another try
                if (resultSet.next()) { //parse the results
                    Category category = mapRow(resultSet); //use the mapRow function that already existed to create the catagory
                    return category;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Category create(Category category)
    {
        // create a new category
        String sqlQuery = "insert into categories (name, description) values (?, ?)"; //create a string to hold the sql query
        try(Connection connection = getConnection(); //create the try-with-resources and connect to the database
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, PreparedStatement.RETURN_GENERATED_KEYS);){ //prepare the sql query for execution
            preparedStatement.setString(1, category.getName()); //set parameter of sql query
            preparedStatement.setString(2, category.getDescription()); //set parameter of sql query
            int rows = preparedStatement.executeUpdate();
            if (rows > 0){ //get key from updated rows
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {//get keys from result set
                    int orderId = generatedKeys.getInt(1); //assign key to var
                    return getById(orderId); //call getbyID with key
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void update(int categoryId, Category category)
    {
        // update category
        String sqlQuery = "update categories set name = ?, description = ? where category_id = ?"; //create a string to hold the sql query
        try(Connection connection = getConnection(); //create the try-with-resources and connect to the database
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)){ //prepare the sql query for execution
            preparedStatement.setString(1, category.getName()); //set parameter of sql query
            preparedStatement.setString(2, category.getDescription()); //set parameter of sql query
            preparedStatement.setInt(3, categoryId); //set parameter of sql query
            int rows = preparedStatement.executeUpdate(); //execute
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int categoryId)
    {
        // delete category
        String sqlQuery = "delete from categories where category_id = ?"; //create a string to hold the sql query
        try(Connection connection = getConnection(); //create the try-with-resources and connect to the database
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)){ //prepare the sql query for execution
            preparedStatement.setInt(1,categoryId); //set parameter of sql query
            int rows = preparedStatement.executeUpdate(); //execute
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Category mapRow(ResultSet row) throws SQLException     //this already existed in the project files
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
