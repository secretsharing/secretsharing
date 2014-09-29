<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Split a Secret</title>
</head>
<body>


<form action="split.jsp" method="post">
Enter your secret:<br />
<% if(request.getParameter("secret") != null) { %>
<textarea rows="5" cols="120" name="secret" style="width:100%;"><%= request.getParameter("secret") %></textarea>
<% } else { %>
<textarea rows="5" cols="120" name="secret" style="width:100%;">Your Secret Here</textarea>
<% } %>
<br /><br/>
Enter the number of parts to create:<br />
<input name="total_parts" value="<%= request.getParameter("total_parts") != null ? request.getParameter("total_parts") : "" %>" />
<i>(Integer at least 1.)</i><br /><br/>
Enter the number of parts required to reconstruct the secret:<br />
<input name="required_parts" value="<%= request.getParameter("required_parts") != null ? request.getParameter("required_parts") : "" %>" />
<i>(Integer at least 1, no more than number of total parts.)</i><br /><br/>
Check this box if your secret is already base64 encoded:<br />
<input name="base64" type="checkbox" value="true" id="base64" <%= request.getParameter("base64") != null && Boolean.parseBoolean(request.getParameter("base64")) ? "checked" : "" %> />
<label for="base64">Base 64</label>
<br /><br/>
<button type="submit" name="submit">Split My Secret</button>
</form>

<br>
<hr/>
<br>
Your secret parts:<br/>
<% if(request.getParameter("submit") != null) { %>
<textarea rows="15" cols="120" readonly="readonly" style="width:100%;"><jsp:include page="/form-split"></jsp:include></textarea>
<% } else { %>
<textarea rows="15" cols="120" readonly="readonly" style="width:100%;">Split parts returned here</textarea>
<% } %>

</body>
</html>