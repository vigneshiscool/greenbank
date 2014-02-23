<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML>
<html>
<head>
<title>Green Bank</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
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


		<BR> ${LoginView.message} <br>What would you like to do? <br>
		<form autocomplete="off" name=form1 method="POST"
			action="MU_ViewAccountDetails.do">
			<input type="submit" value="View Account Details">
		</form>
		<form autocomplete="off" name=form1 method="POST"
			action="MU_ViewTransactionDetails.do">
			<input type="submit" value="View Transaction Log">
		</form>
		<form autocomplete="off" name=form1 method="POST"
			action="MU_NewDepositForm.do">
			<input type="submit" value="Deposit">
		</form>
		<form autocomplete="off" name=form1 method="POST"
			action="MU_NewWithdrawForm.do">
			<input type="submit" value="Withdraw">
		</form>
		<form autocomplete="off" name=form1 method="POST"
			action="MU_NewTransferForm.do">
			<input type="submit" value="Transfer">
		</form>
		<form autocomplete="off" name=form1 method="POST"
			action="MU_SubmitPendingPayments.do">
			<input type="submit" value="Submit Pending Payments">
		</form>


		<BR>
	</div>
</body>
</html>