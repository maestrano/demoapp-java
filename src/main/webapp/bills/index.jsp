<%@ page import="com.maestrano.Maestrano"%>

<% HttpSession sess = request.getSession(); %>
<% Boolean loggedIn = (sess.getAttribute("loggedIn") != null && (Boolean) sess.getAttribute("loggedIn")); %>

<!doctype html>

<html>
<head>
<meta charset="utf-8">
<title>Maestrano Java Demo App</title>

<meta content="IE=edge,chrome=1" http-equiv="X-UA-Compatible">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link
	href="//netdna.bootstrapcdn.com/bootstrap/2.3.2/css/bootstrap.min.css"
	rel="stylesheet">
<body>
	<div class="navbar navbar-fixed-top">
		<div class="navbar-inner">
			<a class="brand" href="/">DemoApp</a>
			<ul class="nav">
				<li><a href="/">Home</a></li>
				<% if (loggedIn) { %>
					<li><a href="/bills">Bills</a></li>
					<li><a href="/logout">Logout</a></li>
				<% } %>
			</ul>
		</div>
	</div>

	<div class="container" style="margin-top: 60px;">
		<div class="row">
			<div class="span8 offset2" style="text-align: center;">
				<% if (!loggedIn) { %>
				<p class="text-error">
					You need to be logged in to see your Maestrano bills
				</p>
				<% } else { %>
				<table class="table table-striped">
					<thead>
						<tr>
							<th>UID</th>
							<th>Description</th>
							<th>Price (cents)</th>
							<th>Currency</th>
							<th>Created At</th>
						</tr>
					</thead>
					<tbody>
						<% for (MnoBill bill : request.getAttribute("billList")) %>
						<tr>
							<td><%= bill.getId() %></td>
							<td><%= bill.getDescription() %></td>
							<td><%= bill.getPriceCents() %></td>
							<td><%= bill.getCurrency() %></td>
							<td><%= bill.getCreatedAt() %></td>
						</tr>
						<% } %>
					</tbody>
				</table>
				<% } %>
			</div>
		</div>
	</div>
	</div>

	<script src="//code.jquery.com/jquery-1.9.1.min.js"></script>
	<script
		src="//netdna.bootstrapcdn.com/bootstrap/2.3.2/js/bootstrap.min.js"></script>
</body>
</html>