<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML>
<html>
<head>
<link rel="stylesheet" type="text/css" href="styles/style.css">
<title>Green Bank</title>
</head>
<body>
	<div class="banner" >
		<div class="pLogout" onclick="window.location.href='<c:url value="/j_spring_security_logout" />'" >
		Logout</div>
		<div class="pHome" onclick="window.location.href='<c:url value="/LoginType.do" />'" >
		Home</div>
		<div align="center">
		<H1>Green Bank</H1>
		<H5>(A secure banking system)</H5>
		</div>
	</div>
	<div class="banner" align="center">
	<BR>Process Request
	<br> ${LoginView.message}
	<BR>
	</div>
</body>
</html>