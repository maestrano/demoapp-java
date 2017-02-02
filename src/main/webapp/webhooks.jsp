<%@ page import="java.util.*"%>
<%@ page import="com.example.WebhookManager.WebhookInfo"%>
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
				<li><a href="/webhooks">Webhooks!</a></li>
				<li><a href="/logout">Logout</a></li>
				<%
					}
				%>
			</ul>
		</div>
	</div>
	<div class="container" style="margin-top: 60px;">
		<div class="row">
			<div class="span12" style="text-align: center;">
				<%
					if (!loggedIn) {
				%>
				<p class="text-error">You need to be logged in to see your webhooks</p>
				<%
					} else {
						Collection<WebhookInfo> webhooks = (Collection<WebhookInfo>) request.getAttribute("webhooks");
						if (webhooks == null) {
							%>
				<p class="text-error">Could not retrieve the webhooks.</p>
				<%
						} else {
				%>
				<p>
					Below are the webhooks received for the group: <%=sess.getAttribute("groupName")%> <%=sess.getAttribute("groupId")%></p>
				<table class="table table-striped">
					<thead>
						<tr>
							<th>Date</th>
							<th>Entity Name</th>
							<th>Entity Id</th>
							<th>Entity</th>we
						</tr>
					</thead>
					<tbody>
						<%
							for (WebhookInfo webhook : webhooks) {
						%>
						<tr>
							<td><%=webhook.getDate()%></td>
							<td><%=webhook.getEntityName()%></td>
							<td><%=webhook.getEntity().get("id")%></td>
							<td><%=webhook.getEntity().toString()%></td>
						</tr>
						<%
								}
							}
						%>
					</tbody>
				</table>
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