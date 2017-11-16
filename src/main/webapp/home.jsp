<%@ page import="com.maestrano.Maestrano"%>
<%@ page import="com.maestrano.configuration.Preset"%>
<%@ page import="java.util.Map"%>
<%
	HttpSession sess = request.getSession();
%>
<%
	Boolean loggedIn = (sess.getAttribute("loggedIn") != null && (Boolean) sess.getAttribute("loggedIn"));
%>
<!doctype html>
<html>
<%@include file="head.jsp"%>
<body>
	<jsp:include page="nav.jsp" />
	<div class="container" style="margin-top: 60px;">
		<div class="row">
			<div class="span8 offset2">
				<%
					if (loggedIn) {
				%>
				<h4>
					Hello
					<%=sess.getAttribute("name")%>
					<%=sess.getAttribute("surname")%>
				</h4>
				<br />
				<p>
					You logged in via group <b><%=sess.getAttribute("groupName")%></b>
				</p>
				<p>
					On the marketplace: <b><%=sess.getAttribute("marketplace")%></b>
				</p>
				<%
					} else {
				%>
				<h3>Sandbox</h3>
				Please go to <a href="http://sandbox.maestrano.com">http://sandbox.maestrano.com</a> to test this application.
				<%
					if (Maestrano.getConfigurations().isEmpty()) {
				%>
				<p>No marketplace found.</p>
				<%
					} else {
				%>
				<h3>Discovered Marketplaces</h3>
				<%
					for (Map.Entry<String, Preset> entry : Maestrano.getConfigurations().entrySet()) {
								String host = entry.getValue().getApi().getHost();
				%>
				<h4><%=entry.getKey()%></h4>
				<a href="<%=host%>"><%=host%></a>
				<%
					}
						}
					}
				%>
			</div>
		</div>
	</div>
</body>
</html>
