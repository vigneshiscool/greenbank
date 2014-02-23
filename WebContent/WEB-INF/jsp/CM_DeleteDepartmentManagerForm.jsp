<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML>
<html>
<head>
<title>CM Delete</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
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
	<BR>	
	<br> ${LoginView.message}
	<BR>
	<BR>
	</div>
</body>
</html>