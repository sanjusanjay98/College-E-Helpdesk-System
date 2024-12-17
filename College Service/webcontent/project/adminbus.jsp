<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.io.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="java.sql.*" %>
<%@ page import="admin.DatabaseConnection" %>
<html>
<head>
    <title>Bus Submissions</title>
    <link rel="stylesheet" type="text/css" href="../css/campusform.css">
</head>
<body>
    <div class="container">
        <h2>Bus Submissions</h2>
        <table class="submission-table">
            <thead>
                <tr>
                    <th>Student Name</th>
                    <th>Department</th>
                    <th>Roll No</th>
                    <th>Phone</th>
                    <th>Problems</th>
                    <th>Image</th>
                    <th>Date Submitted</th>
                </tr>
            </thead>
            <tbody>
            <%
            try (Connection connection = DatabaseConnection.getConnection()) {
            	
                String sql = "SELECT student_name, department, roll_no, phone_no, problems, images, date FROM busform";
                try (PreparedStatement statement = connection.prepareStatement(sql);
                     ResultSet resultSet = statement.executeQuery()) {

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    while (resultSet.next()) {
                        String studentName = resultSet.getString("student_name");
                        String department = resultSet.getString("department");
                        String rollNo = resultSet.getString("roll_no");
                        String phone = resultSet.getString("phone_no");
                        String problems = resultSet.getString("problems");
            %>
                <tr>
                    <td><%= studentName %></td>
                    <td><%= department %></td>
                    <td><%= rollNo %></td>
                    <td><%= phone %></td>
                    <td><%= problems %></td>
                    
                    <%
                        Blob imageBlob = resultSet.getBlob("images");
                        String image = null;
                        if (imageBlob != null) {
                            InputStream inputStream = imageBlob.getBinaryStream();
                            byte[] imageBytes = inputStream.readAllBytes();
                            image = Base64.getEncoder().encodeToString(imageBytes);
                    %>
                        <td><img src="data:image/jpeg;base64,<%= image %>" alt="Student Image" style="width:100px; height:auto;" /></td>
                    <%
                        } else {
                    %>
                        <td>No Image</td>
                    <%
                        }

                        Timestamp date = resultSet.getTimestamp("date");
                        String formattedDate = date != null ? dateFormat.format(date) : null;
                    %>
                    <td><%= formattedDate %></td>
                </tr>
            <%
                    }
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                response.getWriter().print("An error occurred while retrieving data: " + e.getMessage());
            }
            %>
            </tbody>
        </table>
    </div>
</body>
</html>
