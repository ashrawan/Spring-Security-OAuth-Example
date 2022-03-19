<!DOCTYPE html>
<html>
<head>
<title>Welcome Letter</title>
</head>
<body>
	<h3>Hello ${firstName!''}</h3>


    <p>Seems like you have registered to our service with this email <p>

	<p>This is a verification email .</p>

    <h4>CLick the verification link below, to confirm. <h4>
    <#-- Example attribute link -->
    <p><strong>Verification Link: <br> </strong>${linkEmailVerification}?urlPath}</p>

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