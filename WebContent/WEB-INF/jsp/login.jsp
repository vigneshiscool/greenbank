<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
<link rel="stylesheet" type="text/css" href="styles/style.css">
<title>Green Bank</title>
</head>
<body>
	<div class="banner" >
		<div class="pLogout" onclick="window.location.href='<c:url value="/j_spring_security_logout" />'" >
		<img width=60 height=60 src="images/logout.png"></div>
		<div class="pHome" onclick="window.location.href='<c:url value="/LoginType.do" />'" >
		<img width=60 height=60 src="images/home.png"></div>
		<div align="center">
		<H1>Green Bank</H1>
		<H5>(A secure banking system)</H5>
		</div>
	</div>
	<div class="banner" align="center">
		<form name=form1 method="POST" action="LoginType.do">
		UserID <input type="text" name="UserID" id="UserID"><br>
		Password <input type="password" name="Password" id="Password"><br>
		<input type="submit" value="Submit">
	</form>
	<BR>
	<c:out value="${message}" />
	<BR>
	</div>
</body>
</html>