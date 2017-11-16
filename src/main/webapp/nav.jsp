
<%
	HttpSession sess = request.getSession();
%>
<%
	Boolean loggedIn = (sess.getAttribute("loggedIn") != null && (Boolean) sess.getAttribute("loggedIn"));
%>
<nav class="navbar navbar-default">
	<div class="container-fluid">
		<div class="navbar-header">
			<a class="navbar-brand" href="#">Java Demo App</a>
		</div>
		<ul class="nav navbar-nav">
			<li><a href="/">Home</a></li>
			<%
					if (loggedIn) {
				%>
			<li><a href="/bills">Bills</a></li>
			<li><a href="/connec">Connec!</a></li>
			<li><a href="/webhooks">Webhooks</a></li>
			<%
					}
				%>
		</ul>
		<ul class="nav navbar-nav navbar-right">
			<%
					if (loggedIn) {
				%>
			<li><a href="/me"><span class="glyphicon glyphicon-user"></span> <%=sess.getAttribute("name")%> <%=sess.getAttribute("surname")%></a></li>
			<li><a href="/logout"><span class="glyphicon glyphicon-log-out"></span> Logout</a></li>
			<%
					}
				%>
		</ul>
	</div>
</nav>