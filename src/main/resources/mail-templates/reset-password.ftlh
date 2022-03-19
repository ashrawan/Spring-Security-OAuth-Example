<!DOCTYPE html>
<html>
<head>
<title>Welcome Letter</title>
</head>
<body>
	<h3>Hello ${firstName!''}</h3>

	<p>This is a verification for password reset email .</p>

    <p>Seems like you have requested for password reset in our system <p>


    <h4>CLick the reset link below, to change your password. <h4>
    <#-- Example attribute link -->
    <p><strong> Link: <br> </strong>${linkPasswordReset}?urlPath}</p>

    <br>
    <p>If this wasn't you, please simply ignore this email. </p>


    <#-- ========================= Common Block TODO: fragment ============================ -->
    <br>
    <h3>${REGCompanyName}</h3>
    <p>
        SB2 ${REGCompanyStreet} <br>
        ${REGCompanyCountry} <br>
        <strong>Phone:</strong> ${REGCompanyPhone}
    </p>

	<p>© Copyright  ${REGCompanyName}  ${.now?time?string('YYYY')}. All Rights Reserved.</p>

</body>
</html>