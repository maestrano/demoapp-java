<%@ page import="com.maestrano.*"%>
<%@ page import="com.maestrano.account.*"%>
<%@ page import="com.example.dto.*"%>
<%@ page import="java.util.*"%>
<%
	HttpSession sess = request.getSession();
%>
<%
	Boolean loggedIn = (sess.getAttribute("loggedIn") != null && (Boolean) sess.getAttribute("loggedIn"));
%>
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title>Maestrano Java Demo App</title>
<meta content="IE=edge,chrome=1" http-equiv="X-UA-Compatible">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link href="//netdna.bootstrapcdn.com/bootstrap/2.3.2/css/bootstrap.min.css" rel="stylesheet">
<body>
	<div class="navbar navbar-fixed-top">
		<div class="navbar-inner">
			<a class="brand" href="/">DemoApp</a>
			<ul class="nav">
				<li><a href="/">Home</a></li>
				<%
					if (loggedIn) {
				%>
				<li><a href="/bills">Bills</a></li>
				<li><a href="/connec">Connec!</a></li>
				<li><a href="/logout">Logout</a></li>
				<%
					}
				%>
			</ul>
		</div>
	</div>
	<div class="container" style="margin-top: 60px;">
		<div class="row">
			<div class="span12"">
				<%
					if (!loggedIn) {
				%>
				<p class="text-error">You need to be logged in to see Connec! Call Example</p>
				<%
					} else {
						List<Organization> organizations = (List<Organization>) request.getAttribute("organizations");
				%>
				<h2>Typed</h2>
				<p>This is an example of a typed call to Connec! call to Get Organizations</p>
				<pre>ConnecClient connecClient = ConnecClient.withPreset(marketplace);
connecClient.all("organizations", groupId, Organizations.class);</pre>
				<table class="table table-striped">
					<thead>
						<tr>
							<th>ID</th>
							<th>Name</th>
							<th>Created At</th>
							<th>Updated At</th>
						</tr>
					</thead>
					<tbody>
						<%
							for (Organization organization : organizations) {
						%>
						<tr>
							<td><%=organization.getId()%></td>
							<td><%=organization.getName()%></td>
							<td><%=organization.getCreatedAt()%></td>
							<td><%=organization.getUpdatedAt()%></td>
						</tr>
						<%
							}
						%>
					</tbody>
				</table>
				<h2>Raw</h2>
				<p>This is an example of a raw call to Connec! call to Get Organizations</p>
				<pre>connecClient.all("organizations", groupId);</pre>
				<pre><%=request.getAttribute("organizationsJson")%></pre>
				<%
					}
				%>
			</div>
		</div>
	</div>
	<script src="//code.jquery.com/jquery-1.9.1.min.js"></script>
	<script src="//netdna.bootstrapcdn.com/bootstrap/2.3.2/js/bootstrap.min.js"></script>
</body>
</html>