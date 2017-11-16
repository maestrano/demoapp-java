<%@ page import="com.maestrano.account.Bill"%>
<%@ page import="java.util.*"%>
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
				<p class="text-error">You need to be logged in to see your Maestrano bills</p>
				<%
					} else {
						List<Bill> bills = (List<Bill>) request.getAttribute("billList");
						if (bills == null) {
				%>
				<p class="text-error">Could not retrieve the Bills.</p>
				<%
					} else {
				%>
				<p>
					Below are the bills related to the group:
					<%=sess.getAttribute("groupName")%></p>
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
						<%
							for (Bill bill : bills) {
						%>
						<tr>
							<td><%=bill.getId()%></td>
							<td><%=bill.getDescription()%></td>
							<td><%=bill.getPriceCents()%></td>
							<td><%=bill.getCurrency()%></td>
							<td><%=bill.getCreatedAt()%></td>
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