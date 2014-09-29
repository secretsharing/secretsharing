<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="splitjoin.css">
<title>Recover a Secret</title>
</head>
<body>

<form action="join.jsp" method="post">
Enter your secret parts below, 1 per line<br/>
<% if(request.getParameter("parts") != null) { %>
<textarea rows="8" cols="120" name="parts" style="width:100%;"><%= request.getParameter("parts") %></textarea>
<% } else { %>
<textarea rows="8" cols="120" name="parts" style="width:100%;">Enter Secret Parts</textarea>
<% } %>
<br/><br/>
Check this box if your secret was base64 encoded:<br />
<input name="base64" type="checkbox" value="true" id="base64"  <%= request.getParameter("base64") != null && Boolean.parseBoolean(request.getParameter("base64")) ? "checked" : "" %> />
<label for="base64">Base 64</label>
<br /><br/>
<button type="submit" name="submit">Recover My Secret</button>
</form>

<br/>
<hr/>
<br/>
Your secret:<br/>
<%
if(request.getParameter("submit") != null) {
%>
<textarea rows="5" cols="120" readonly="readonly" style="width:100%;"><jsp:include page="/form-join"></jsp:include></textarea>
<% } else { %>
<textarea rows="5" cols="120" readonly="readonly" style="width:100%;">Joined secret returned here</textarea>
<% } %>
</body>
</html>