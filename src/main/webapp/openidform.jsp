<%@ page import="java.util.*"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>OpenID HTML FORM Redirection</title>
</head>
<!--<body onload="document.forms['openid-form-redirection'].submit();">-->
<body>
	<p>Redirecting you to <%= request.getAttribute("opEndpoint") %> </p>

	<form name="openid-form-redirection" action="<%= request.getAttribute("opEndpoint") %>" method="post" accept-charset="utf-8">
		<%
                Map pm= (Map)request.getAttribute("parameterMap");
                Iterator keyit=pm.keySet().iterator();
                Object key;
                Object value;
                while (keyit.hasNext())
                {
                    key=keyit.next();
                    value=pm.get(key);
            %>
		<input type="hidden" name="<%= key%>" value="<%= value%>" />
		<%
                }
        %>
		<button type="submit">Continue...</button>
	</form>
</body>
</html>