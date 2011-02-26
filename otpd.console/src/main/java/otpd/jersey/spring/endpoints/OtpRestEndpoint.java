/*
*  
*	FreeOTP OATH OneTimePassword Solution - Copyright (c) 2010, Alessandro Nepoti (alessandro.nepoti@wedjaa.net) 
*	All rights reserved.
*	
*	Redistribution and use in source and binary forms, with or without
*	modification, are permitted provided that the following conditions are met:
*	* Redistributions of source code must retain the above copyright
*	  notice, this list of conditions and the following disclaimer.
*	* Redistributions in binary form must reproduce the above copyright
*	  notice, this list of conditions and the following disclaimer in the
*	  documentation and/or other materials provided with the distribution.
*	* Neither the name of the <organization> nor the
*	  names of its contributors may be used to endorse or promote products
*	  derived from this software without specific prior written permission.
*	
*	THIS SOFTWARE IS PROVIDED BY COPYRIGHT HOLDERS ''AS IS'' AND ANY
*	EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
*	WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
*	DISCLAIMED. IN NO EVENT SHALL <copyright holder> BE LIABLE FOR ANY
*	DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
*	(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
*	LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
*	ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
*	(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
*	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*
*/


package otpd.jersey.spring.endpoints;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import otpd.idm.domain.Subject;
import otpd.idm.domain.impl.SubjectImpl;
import otpd.idm.service.ServiceException;
import otpd.jersey.spring.endpoints.exception.RestDefaultException;
import otpd.jersey.spring.services.otp.OtpManagementService;

import com.sun.jersey.api.core.InjectParam;

@Path("/otp")
@Component
@Scope("request")
public class OtpRestEndpoint {

	final Logger logger = LoggerFactory.getLogger(OtpRestEndpoint.class);

	@InjectParam
	private OtpManagementService otpManagementService;

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("/resynch")
	public Response resynch(

	@FormParam(value = "commoname") String commonName,
			@FormParam(value = "otppassword1") String password1,
			@FormParam(value = "otppassword2") String password2) {

		Response response;

		Subject userProfile = new SubjectImpl();

		userProfile.setCommonName(commonName);

		if (logger.isDebugEnabled()) {

			logger.debug("userProfile : " + userProfile);
			logger.debug("password1 : " + password1);
			logger.debug("password2 : " + password2);
		}

		try {
			otpManagementService.resynchUserToken(userProfile, password1,
					password2);
			response = Response.status(Status.OK).build();

		} catch (ServiceException e) {
			logger.error( "read : " + e.getLocalizedMessage());
			throw new RestDefaultException();
		}

		return response;
	}

}
