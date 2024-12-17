package college;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/CampusServiceServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1,  // 1 MB - Temporary buffer size before storing on disk
    maxFileSize = 1024 * 1024 * 5,        // 5 MB - Maximum file size allowed per file
    maxRequestSize = 1024 * 1024 * 10     // 10 MB - Total request size allowed including all parts
)
public class CampusServiceServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String studentName = request.getParameter("student-name");
        String department = request.getParameter("department");
        String rollNo = request.getParameter("roll");
        String phone = request.getParameter("phone");
        String problems = request.getParameter("problems");

        Part imagePart = request.getPart("images");

        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO campusform (student_name, department, roll_no, phone, problems, image_path) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, studentName);
                statement.setString(2, department);
                statement.setString(3, rollNo);
                statement.setString(4, phone);
                statement.setString(5, problems);

                if (imagePart != null && imagePart.getSize() > 0) {
                    try (InputStream inputStream = imagePart.getInputStream()) {
                        statement.setBlob(6, inputStream);
                    } catch (IOException e) {
                        response.getWriter().print("Error handling the uploaded file: " + e.getMessage());
                        return;
                    }
                } else {
                    statement.setNull(6, java.sql.Types.BLOB);
                }

                statement.executeUpdate();

                response.sendRedirect("project/submission-confirmation.html");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().print("An error occurred while submitting the form: " + e.getMessage());
        }
    }
}
