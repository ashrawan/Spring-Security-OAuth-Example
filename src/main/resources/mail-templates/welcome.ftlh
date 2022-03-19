<!DOCTYPE html>
<html>
<head>
<title>Welcome Letter</title>
</head>
<body>
	<h3>Hello ${firstName!''}, Welcome to the team</h3>

	<p>Congratulations on joining us !!! .</p>
	<p>Welcome to the team and cheers on your new position.
	Your skills and experience will be a definite asset to us, and we hope to achieve great things together.
	We will contact you soon for the follow ups to get you started. <br>
	In the meanwhile, you can look through the setups list we will be going through. </p>


    <#-- Example List of Item List<String> setupItemList;  -->
    <#list setupItemList>
    	<h4>An assigned personal or a team member will be available to guide you through each of this steps.</h4>
        <ol>
            <#items as setupItem>
                <li>${setupItem}</li>
            </#items>
        </ol>
    <#else>
        <p> Have a coffee and relax :) </p>
    </#list>

    <#-- Example attribute link -->
    <h4>Visit Site: ${visitOfficialSite}?urlPath}</h4>

    <p>If you have any questions or concerns, please contact ${assignedSupportStaff!'official support'}. </p>



    <#-- =========================  Common Block TODO: fragment ============================ -->
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