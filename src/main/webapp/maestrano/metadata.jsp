<%@ page language="java" contentType="application/json; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.maestrano.Maestrano"%>

<%
	java.io.PrintWriter writer = response.getWriter();

	if (Maestrano.authenticate(request)) {
		writer.write(Maestrano.getDefault().toMetadata());
	} else {
		writer.write("Failed");
	}

	writer.flush();
%>