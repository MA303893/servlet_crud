<%@ page language="java" contentType="text/html; charset=EUC-KR" pageEncoding="EUC-KR" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min.css"/>
    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min.js"></script>
    <title>Add new user</title>
</head>
<body>
<script>
    $(function () {
        $('input[name=dob]').datepicker();
    });
    var changeSpan = function (val) {
        $('#fileName').innerText = val
    }
</script>

<form method="POST" action='UserController' name="frmAddUser" enctype="multipart/form-data">
    User ID : <input type="text" readonly="readonly" name="userid"
                     value="<c:out value="${user.userid}" />"/> <br/>
    First Name : <input
        type="text" name="firstName"
        value="<c:out value="${user.firstName}" />"/> <br/>
    Last Name : <input
        type="text" name="lastName"
        value="<c:out value="${user.lastName}" />"/> <br/>
    DOB : <input
        type="text" name="dob"
        value="<fmt:formatDate pattern="MM/dd/yyyy" value="${user.dob}" />"/> <br/>
    Email : <input type="text" name="email"
                   value="<c:out value="${user.email}" />"/> <br/>
    Image : <c:if test="${!empty user.fileName}">
    <span id="fileName">${user.fileName}</span>
</c:if>
    <input type="file" name="file" size="50" placeholder="Upload Your File" onchange="changeSpan(this.value)"/><br><br>
    <input type="submit" value="Submit"/>

</form>
</body>
</html>