<% HttpSession sess = request.getSession(); %>

<!doctype html>

<html>
<head>
  <meta charset="utf-8">
  <title>Embedded Jetty Template</title>

  <meta content="IE=edge,chrome=1" http-equiv="X-UA-Compatible">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">

  <link href="//netdna.bootstrapcdn.com/bootstrap/2.3.2/css/bootstrap.min.css" rel="stylesheet">

<body>
  <div class="navbar navbar-fixed-top">
    <div class="navbar-inner">
      <div class="container">
      </div>
    </div>
  </div>

  <div class="container">
    <div class="row">
      <div class="span8 offset2" style="text-align:center;">
        <% if (sess.getAttribute("loggedIn") != null && (Boolean) sess.getAttribute("loggedIn")) { %>
          <h4>Hello, <%= sess.getAttribute("name") %> <%= sess.getAttribute("surname") %></h4>
          <br/>
          <p>You logged in via group <%= sess.getAttribute("groupName") %></p>
        <% } else { %>
          <a class="btn btn-large" href="/maestrano/auth/saml/init">Login</a>
        <% } %>
      </div>
    </div>
  </div>
</div>

<script src="//code.jquery.com/jquery-1.9.1.min.js"></script>
<script src="//netdna.bootstrapcdn.com/bootstrap/2.3.2/js/bootstrap.min.js"></script>
</body>
</html>
