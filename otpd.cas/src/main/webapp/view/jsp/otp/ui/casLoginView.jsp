<%@ page contentType="text/html; charset=UTF-8" %>
<jsp:directive.include file="includes/top.jsp" />

				  <h1><spring:message code="screen.welcome.instructions" /></h1>
				  <p><spring:message code="screen.welcome.instructions.extended" /></p>
				  <br />
                  <fieldset>
					<form:form method="post" id="fm1" commandName="${commandName}" htmlEscape="true">
                		<!-- <spring:message code="screen.welcome.welcome" /> -->
                        <label for="username"><spring:message code="screen.welcome.label.netid" /></label>
						<c:if test="${not empty sessionScope.openIdLocalId}">
							<strong>${sessionScope.openIdLocalId}</strong>
							<input type="hidden" id="username" name="username" value="${sessionScope.openIdLocalId}" />
						</c:if>
						<c:if test="${empty sessionScope.openIdLocalId}">
							<spring:message code="screen.welcome.label.netid.accesskey" var="userNameAccessKey" />
							<form:input cssClass="required" cssErrorClass="error" id="username" size="20" tabindex="1" accesskey="${userNameAccessKey}" path="username" autocomplete="false" htmlEscape="true" />
						</c:if>
                        <br/>
                        <label for="password"><spring:message code="screen.welcome.label.password" /></label>
						<%--
						NOTE: Certain browsers will offer the option of caching passwords for a user.  There is a non-standard attribute,
						"autocomplete" that when set to "off" will tell certain browsers not to prompt to cache credentials.  For more
						information, see the following web page:
						http://www.geocities.com/technofundo/tech/web/ie_autocomplete.html
						--%>
						<spring:message code="screen.welcome.label.password.accesskey" var="passwordAccessKey" />
						<form:password cssClass="required" cssErrorClass="error" id="password" size="20" tabindex="2" path="password"  accesskey="${passwordAccessKey}" htmlEscape="true" autocomplete="off" />
						
                        <!-- NO USE input id="warn" name="warn" value="true" tabindex="3" accesskey="<spring:message code="screen.welcome.label.warn.accesskey" />" type="checkbox" />
                        <label for="warn"><spring:message code="screen.welcome.label.warn" /></label -->
                        
						<input type="hidden"  style="display: none;" name="lt" value="${flowExecutionKey}" />
						<input type="hidden" style="display: none;" name="_eventId" value="submit" />

                        <input class="entra" name="submit" accesskey="l" value="<spring:message code="screen.welcome.button.login" />" tabindex="4" type="submit" />
                        <!-- NO USE input class="entra" name="reset" accesskey="c" value="<spring:message code="screen.welcome.button.clear" />" tabindex="5" type="reset" /-->
                        
			    		<form:errors path="*" cssClass="errors" id="status" element="div" />
                      </form:form>
                    </fieldset>
                    
<jsp:directive.include file="includes/bottom.jsp" />
