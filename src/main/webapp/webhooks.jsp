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
<%@include file="head.jsp"%>
<body>
	<jsp:include page="nav.jsp" />
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
							<th>Entity</th>
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
</body>
</html>