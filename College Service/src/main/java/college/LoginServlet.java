package college;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            Connection connection = DatabaseConnection.getConnection();

            String query = "SELECT * FROM signup WHERE email = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
            	response.sendRedirect("project/mainpage.html");
            } else {
            	PrintWriter out = response.getWriter();
            	out.println("<script type=\"text/javascript\">");
            	out.println("alert('Login failed! Invalid email or password.');");
            	out.println("location='project/login.html';");
            	out.println("</script>");
            }

            resultSet.close();
            statement.close();
            connection.close();
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().println("Database connection error!");
        }
    }
}
