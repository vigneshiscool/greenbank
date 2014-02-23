<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML>
<html>
<head>
<link rel="stylesheet" type="text/css" href="styles/style.css">
<title>Green Bank</title>
</head>
<body>
	<div class="banner">
		<div class="pLogout"
			onclick="window.location.href='<c:url value="/j_spring_security_logout" />'">
			<img width=60 height=60 src="images/logout.png">
		</div>
		<div class="pHome"
			onclick="window.location.href='<c:url value="/LoginType.do" />'">
			<img width=60 height=60 src="images/home.png">
		</div>
		<div align="center">
			<H1>Green Bank</H1>
			<H5>(A secure banking system)</H5>
		</div>
	</div>
	<div class="banner" align="center">

		<BR>
		<c:out value="${LoginView.message}" />
		<br>What would you like to do? <br>

		<form autocomplete="off" name=form1 method="POST"
			action="SA_CreateExternalUser.do">
			<input type="submit" value="Create-User Requests">
		</form>
		<form autocomplete="off" name=form1 method="POST"
			action="SA_DeleteExternalUser.do">
			<input type="submit" value="Delete-User Requests">
		</form>
		<form autocomplete="off" name=form1 method="POST"
			action="SA_ModifyExternalUser.do">
			<input type="submit" value="Modify-User Requests">
		</form>

		<BR>
	</div>
</body>
</html>