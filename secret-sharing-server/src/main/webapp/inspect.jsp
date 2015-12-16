<%@page import="org.mitre.secretsharing.server.PartInspector.Field"%>
<%@page import="org.mitre.secretsharing.server.PartInspector"%>
<%@page import="org.mitre.secretsharing.codec.PartFormats"%>
<%@page import="org.mitre.secretsharing.Part"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="bootstrap/css/bootstrap.css">
<link rel="stylesheet" type="text/css" href="splitjoin.css">
<title>Secret Part Inspector</title>
<style type="text/css">
input, .details {
	width: 100%;
}

table.details {
	border-collapse: collapse;
}

table.details tr:first-child {
	border-top-style: dotted;
}

table.details tr {
	border-bottom-style: dotted;
	border-width: 1px;
}

table.details td:first-child {
	white-space: nowrap;
}

td {
	padding-top: 5px;
	padding-bottom: 5px;
}

span.format {
	outline-style: solid;
	outline-width: 1px;
	padding: 5px;
	font-family: monospace;
	font-weight: bold;
}

.public {
	background-color: #afa;
}

.private {
	background-color: #faa;
}

.delim {
	background-color: #aaa;
}

.version {
	background-color: #aaf;
}

table.format td:first-child {
	text-align: right;
}

</style>
</head>
<body>

<h1>Secret Part Inspector</h1>

<form action="inspect.html" method="post">
Enter your secret part below<br/>
<% 
Part part = null;
Throwable parseThrown = null;
if(request.getParameter("part") != null) { 
	try {
		part = PartFormats.parse(request.getParameter("part").trim()); 
	} catch(Exception e) {
		parseThrown = e;
	}
%>
<textarea rows="2" cols="120" name="part" style="width:100%;"><%= ((part == null ? "Invalid secret part!\n" : "") + StringEscapeUtils.escapeHtml(request.getParameter("part"))) %></textarea>
<% } else { %>
<textarea rows="2" cols="120" name="part" style="width:100%;">Enter Secret Part</textarea>
<% } %>
<br /><br/>
<button type="submit" name="submit">Inspect Secret Part</button>
</form>

<br/>
<hr/>
<h2>Secret Part Details:</h2>
<table class="details">
<tr><td>Format version</td><td class="details"><input class="version" type="text" value="<%= PartInspector.get(part, Field.version) %>"></td></tr>
<tr><td>Secret length (bytes)</td><td class="details"><input class="public" type="text" value="<%= PartInspector.get(part, Field.length) %>"></td></tr>
<tr><td>Required secret parts</td><td class="details"><input class="public" type="text" value="<%= PartInspector.get(part, Field.required) %>"></td></tr>
<tr><td>Polynomial prime modulus</td><td class="details"><input class="public" type="text" value="<%= PartInspector.get(part, Field.modulus) %>"></td></tr>
<tr><td>Polynomial point X coordinate</td><td class="details"><input class="private" type="text" value="<%= PartInspector.get(part, Field.x) %>"></td></tr>
<tr><td>Polynomial point Y coordinate</td><td class="details"><input class="private" type="text" value="<%= PartInspector.get(part, Field.y) %>"></td></tr>
</table>
<br/>
<hr/>
<h2>Secret Part Format: Field Composition</h2>
<p>
	<span class="format version">version</span>
	<span class="format delim">:</span>
	<span class="format public">length, required parts, &amp; modulus</span>
	<span class="format delim">//</span>
	<span class="format private">point &amp; checksum</span>
</p>
<h2>Secret Part Format: Field Meanings</h2>
<table class="format">
<tr><td><span class="format version">version</span></td><td>Identifies how to parse this secret part</td></tr>
<tr><td><span class="format delim">:</span></td><td>Delimeter; no information content</td></tr>
<tr><td><span class="format public">length, required parts, &amp; modulus</span></td><td><b>PUBLIC</b> information, shared among all secret parts</td></tr>
<tr><td><span class="format delim">//</span></td><td>Delimeter; no information content</td></tr>
<tr><td><span class="format private">point &amp; checksum</span></td><td><b>PRIVATE</b> information, specific to this secret part</td></tr>
</table>
</body>
</html>