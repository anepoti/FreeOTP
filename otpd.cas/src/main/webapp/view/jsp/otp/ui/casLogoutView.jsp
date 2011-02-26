<jsp:directive.include file="includes/top.jsp" />

		<div id="msg" class="success">
			<h1><spring:message code="screen.logout.header" /></h1>

			<p><spring:message code="screen.logout.success" /></p>
			<p><spring:message code="screen.logout.security" /></p>
		
			<%--
			 Implementation of support for the "url" parameter to logout as recommended in CAS spec section 2.3.1.
			 A service sending a user to CAS for logout can specify this parameter to suggest that we offer
			 the user a particular link out from the logout UI once logout is completed.  We do that here.
			--%>
			<c:if test="${not empty param['url']}">
			<h1>
				<spring:message code="screen.logout.redirect" arguments="${fn:escapeXml(param.url)}" />
			</h1>
			</c:if>
		
		<%-- 
		 Single-Sign-Out (session invalidate + remove JSESSIONID cookie) 
		--%>
		<% session.invalidate(); %>
		<script type="text/javascript" src="themes/otp/js/logout.js"></script>
		<script type="text/javascript">
				eraseCookie('JSESSIONID','/otp');
				eraseCookie('CASTGC','/cas');
		</script>
		
<jsp:directive.include file="includes/bottom.jsp" />
