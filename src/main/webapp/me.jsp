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
			<div class="span12">
				<%
					if (!loggedIn) {
				%>
				<p class="text-error">You need to be logged to see this page</p>
				<%
					} else {
				%>
				<h3>User informations</h3>
				<dl class="dl-horizontal">
					<dt>Uid</dt>
					<dd><%=sess.getAttribute("uid")%></dd>
					<dt>First Name</dt>
					<dd><%=sess.getAttribute("name")%></dd>
					<dt>Last Name</dt>
					<dd><%=sess.getAttribute("surname")%></dd>
					<dt>Role</dt>
					<dd><%=sess.getAttribute("role")%></dd>
					<dt>Email</dt>
					<dd><%=sess.getAttribute("email")%></dd>
					<dt>Group UId</dt>
					<dd><%=sess.getAttribute("groupId")%></dd>
					<dt>Group Name</dt>
					<dd><%=sess.getAttribute("groupName")%></dd>
				</dl>
				<%
					}
				%>
			</div>
		</div>
	</div>
</body>
</html>
