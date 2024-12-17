package college;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/SignupServlet")
public class SignupServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        System.out.println("Received Name: " + name);
        System.out.println("Received Email: " + email);

        if (!password.equals(confirmPassword)) {
        	response.getWriter().println("<script type=\"text/javascript\">");
        	response.getWriter().println("alert('This email already exists, please sign in.');");
        	response.getWriter().println("window.location.href='project/singup.html';");
        	response.getWriter().println("</script>");

            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {

            String checkEmailQuery = "SELECT * FROM signup WHERE email = ?";
            try (PreparedStatement checkEmailStmt = conn.prepareStatement(checkEmailQuery)) {
                checkEmailStmt.setString(1, email);

                ResultSet rs = checkEmailStmt.executeQuery();
                if (rs.next()) {
                	response.getWriter().println("<script type=\"text/javascript\">");
                	response.getWriter().println("alert('This email already exists, please sign in.');");
                	response.getWriter().println("window.location.href='project/singup.html';"); 
                	response.getWriter().println("</script>");

                    return;
                }
            }

            String query = "INSERT INTO signup (name, email, password) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setString(3, password);

                int result = pstmt.executeUpdate();
                if (result > 0) {
                    System.out.println("Data Inserted");
                    response.sendRedirect("project/mainpage.html"); 
                } else {
                    response.getWriter().write("Error in saving data!");
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            response.getWriter().write("Database connection error!");
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
    }
}
