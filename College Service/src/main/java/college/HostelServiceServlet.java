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

@WebServlet("/HostelServiceServlet")
@MultipartConfig(
	    fileSizeThreshold = 1024 * 1024 * 1,  // 1 MB - Temporary buffer size before storing on disk
	    maxFileSize = 1024 * 1024 * 5,        // 5 MB - Maximum file size allowed per file
	    maxRequestSize = 1024 * 1024 * 10     // 10 MB - Total request size allowed including all parts
	)
public class HostelServiceServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Retrieve form data
        String studentName = request.getParameter("student-name");
        String department = request.getParameter("department");
        String rollNo = request.getParameter("roll");
        String phoneNo = request.getParameter("phone");
        String problems = request.getParameter("problems");

        // Retrieve uploaded image file
        Part filePart = request.getPart("images");
        InputStream imageStream = null;
        if (filePart != null && filePart.getSize() > 0) {
            imageStream = filePart.getInputStream();
        }

        // SQL query to insert data into MySQL
        String sql = "INSERT INTO hostelform (student_name, department, roll_no, phone_no, problems, images) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            // Set parameters for the SQL query
            statement.setString(1, studentName);
            statement.setString(2, department);
            statement.setString(3, rollNo);
            statement.setString(4, phoneNo);
            statement.setString(5, problems);
            if (imageStream != null) {
                statement.setBlob(6, imageStream);  // Store image as BLOB
            } else {
                statement.setNull(6, java.sql.Types.BLOB);
            }

            int row = statement.executeUpdate();
            if (row > 0) {
                response.sendRedirect("project/submission-confirmation.html");
            } else {
                response.getWriter().println("Failed to submit form. Please try again.");
            }
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            response.getWriter().println("An error occurred while submitting the form: " + ex.getMessage());
        }
    }
}
