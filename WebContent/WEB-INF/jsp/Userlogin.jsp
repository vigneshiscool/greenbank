<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<link rel="stylesheet" type="text/css" href="styles/style.css">
<title>Login Page</title>
<style>
.errorblock {
	color: #ff0000;
	background-color: #ffEEEE;
	border: 3px solid #ff0000;
	padding: 8px;
	margin: 16px;
}
</style>
</head>
<body onload='document.f.j_username.focus();'>
	<div class="banner" align="center">
		<H1>Green Bank</H1>
		<H5>(A secure banking system)</H5>
	</div>
	<div class="banner" align="center">

		<c:if test="${not empty error}">
			<div class="errorblock">
				Your login attempt was not successful, try again.<br /> Caused :
				${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}
			</div>
		</c:if>

		<form name='f' action="<c:url value='j_spring_security_check' />"
			method='POST'>

			<table>
				<tr>
					<td>User:</td>
					<td><input type='text' name='j_username' value=''></td>
				</tr>
				<tr>
					<td>Password:</td>
					<td><input type='password' name='j_password' /></td>
				</tr>
				<tr>
					<td colspan='1'><input name="submit" type="submit"
						value="submit" /></td>

					<td colspan='1'><input name="reset" type="reset" /></td>
				</tr>
			</table>

		</form>
	</div>
</body>
</html>