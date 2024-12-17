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

@WebServlet("/BusServiceServlet")
@MultipartConfig(
	    fileSizeThreshold = 1024 * 1024 * 1,  // 1 MB - Temporary buffer size before storing on disk
	    maxFileSize = 1024 * 1024 * 5,        // 5 MB - Maximum file size allowed per file
	    maxRequestSize = 1024 * 1024 * 10     // 10 MB - Total request size allowed including all parts
	)
public class BusServiceServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String studentName = request.getParameter("student-name");
        String department = request.getParameter("department");
        String rollNo = request.getParameter("roll");
        String phoneNo = request.getParameter("phone");
        String problems = request.getParameter("problems");

        Part imagePart = request.getPart("images");

        try (Connection connection = DatabaseConnection.getConnection()) {
        	
            String sql = "INSERT INTO busform (student_name, department, roll_no, phone_no, problems, images) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, studentName);
                statement.setString(2, department);
                statement.setString(3, rollNo);
                statement.setString(4, phoneNo);
                statement.setString(5, problems);

                if (imagePart != null && imagePart.getSize() > 0) {
                    System.out.println("Image file size: " + imagePart.getSize());
                    try (InputStream inputStream = imagePart.getInputStream()) {
                        statement.setBlob(6, inputStream);
                    }
                } else {
                    System.out.println("No image uploaded or file is empty.");
                    statement.setNull(6, java.sql.Types.BLOB);
                }


                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    response.sendRedirect("project/submission-confirmation.html");
                } else {
                    response.getWriter().println("Error submitting the form. Please try again.");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().println("Database error: " + e.getMessage());
        }
    }
}
