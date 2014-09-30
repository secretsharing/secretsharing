<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
<% if(request.getParameter("part") != null) { %>
<textarea rows="2" cols="120" name="part" style="width:100%;"><%= request.getParameter("part") %></textarea>
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
<tr><td>Format version</td><td class="details"><input class="version" type="text" value="<jsp:include page="/form-inspect"><jsp:param name="field" value="version"/></jsp:include>"></td></tr>
<tr><td>Secret length (bytes)</td><td class="details"><input class="public" type="text" value="<jsp:include page="/form-inspect"><jsp:param name="field" value="length"/></jsp:include>"></td></tr>
<tr><td>Polynomial prime modulus</td><td class="details"><input class="public" type="text" value="<jsp:include page="/form-inspect"><jsp:param name="field" value="modulus"/></jsp:include>"></td></tr>
<tr><td>Polynomial point X coordinate</td><td class="details"><input class="private" type="text" value="<jsp:include page="/form-inspect"><jsp:param name="field" value="x"/></jsp:include>"></td></tr>
<tr><td>Polynomial point Y coordinate</td><td class="details"><input class="private" type="text" value="<jsp:include page="/form-inspect"><jsp:param name="field" value="y"/></jsp:include>"></td></tr>
<tr><td>Polynomial point checksum</td><td class="details"><input class="private" type="text" value="<jsp:include page="/form-inspect"><jsp:param name="field" value="checksum"/></jsp:include>"></td></tr>
</table>
<br/>
<hr/>
<h2>Secret Part Format: Field Composition</h2>
<p>
	<span class="format version">version</span>
	<span class="format delim">:</span>
	<span class="format public">length & modulus</span>
	<span class="format delim">//</span>
	<span class="format private">point & checksum</span>
</p>
<h2>Secret Part Format: Field Meanings</h2>
<table class="format">
<tr><td><span class="format version">version</span></td><td>Identifies how to parse this secret part</td></tr>
<tr><td><span class="format delim">:</span></td><td>Delimeter; no information content</td></tr>
<tr><td><span class="format public">length & modulus</span></td><td><b>PUBLIC</b> information, shared among all secret parts</td></tr>
<tr><td><span class="format delim">//</span></td><td>Delimeter; no information content</td></tr>
<tr><td><span class="format private">point & checksum</span></td><td><b>PRIVATE</b> information, specific to this secret part</td></tr>
</table>
</body>
</html>