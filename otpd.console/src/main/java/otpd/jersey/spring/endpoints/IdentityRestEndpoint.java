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

import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


 
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import otpd.idm.domain.Subject;
import otpd.idm.domain.impl.OtpProfile;
import otpd.idm.domain.impl.SubjectImpl;
import otpd.idm.service.ServiceException;
import otpd.jersey.spring.dao.SubjectRestResult;
 
import otpd.jersey.spring.endpoints.exception.RestBadConditionException;
import otpd.jersey.spring.endpoints.exception.RestDefaultException;
import otpd.jersey.spring.services.idm.IdentityManagementService;

import com.sun.jersey.api.core.InjectParam;

@Path("/identity")
@Component
@Scope("request")
 
public class IdentityRestEndpoint {

	final Logger logger = LoggerFactory.getLogger(IdentityRestEndpoint.class);
	
	@InjectParam
	private IdentityManagementService identityManagementService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/read")
	 
	public SubjectRestResult read(@QueryParam("commoname") String commonName) {

		if (commonName == null)
			throw new RestBadConditionException();

		SubjectRestResult subjectRestResult = new SubjectRestResult();

		Subject subjectRequest = new SubjectImpl();
		subjectRequest.setCommonName(commonName);

		Subject subjectResponse;

		try {

			subjectResponse = identityManagementService
					.readUser(subjectRequest);

			if (subjectResponse != null) {

				subjectRestResult
						.setCommonName(subjectResponse.getCommonName());
				subjectRestResult.setFirstName(subjectResponse.getFirstName());
				subjectRestResult.setLastName(subjectResponse.getLastName());
				subjectRestResult.setMail(subjectResponse.getEmail());

				subjectRestResult.setOtpSerial(subjectResponse.getOtpProfile()
						.getOtpSerial());
				subjectRestResult.setOtpModel(subjectResponse.getOtpProfile()
						.getOtpModel());
				subjectRestResult.setOtpVendor(subjectResponse.getOtpProfile()
						.getOtpVendor());
			}

		} catch (ServiceException e) {

			logger.error("read : " + e.getLocalizedMessage());
			throw new RestDefaultException();
		}

		return subjectRestResult;
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("/create")
	 
	public Response create(

			@FormParam(value = "commoname") String commonName,
			@FormParam(value = "name") String givenName,
			@FormParam(value = "surname") String lastName,
			@FormParam(value = "email") String mail) {
		
	
		Response response;

		Subject subjectRequest = new SubjectImpl();

		subjectRequest.setCommonName(commonName);
		subjectRequest.setEmail(mail);
		subjectRequest.setFirstName(givenName);
		subjectRequest.setLastName(lastName);

		subjectRequest.setOtpProfile(getDefaultOtpProfile());

		try {

			Subject responseSubject = 
				identityManagementService.createOrUpdateUser(subjectRequest);
			
			response = Response.created(URI.create("/" 
					+ responseSubject.getCommonName())).build();

		} catch (ServiceException e) {
			logger.error( "read : " + e.getLocalizedMessage());
			throw new RestDefaultException();
		}

		return response;
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("/create/wizard")
	 
	public Response bundledWizard(

			@FormParam(value = "commoname") String commonName,
			@FormParam(value = "name") String givenName,
			@FormParam(value = "surname") String lastName,
			@FormParam(value = "email") String mail,
			@FormParam(value = "otpserial") String otpserial,
			@FormParam(value = "otpkey1") String otpkey1,
			@FormParam(value = "otpmodel") String otpmodel,
			@FormParam(value = "otpvendor") String otpvendor) {
		
	
		Response response;
		
		
		Subject subjectRequest = new SubjectImpl();
		OtpProfile otpProfile = new OtpProfile();

		subjectRequest.setCommonName(commonName);
		subjectRequest.setEmail(mail);
		subjectRequest.setFirstName(givenName);
		subjectRequest.setLastName(lastName);
		
		otpProfile.setOtpKey1(otpkey1);
		otpProfile.setOtpModel(otpmodel);
		otpProfile.setOtpSerial(otpserial);
		otpProfile.setOtpVendor(otpvendor);

		subjectRequest.setOtpProfile(otpProfile);

		try {

			Subject responseSubject = 
				identityManagementService.createOrUpdateUser(subjectRequest);
			
			response = Response.created(URI.create("/" 
					+ responseSubject.getCommonName())).build();

		} catch (ServiceException e) {
			logger.error("read : " + e.getLocalizedMessage());
			throw new RestDefaultException(e.getMessage());
		}
		
 		return response;
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("/update")
 
	public Response update(

	@FormParam(value = "commoname") String commonName,
			@FormParam(value = "name") String givenName,
			@FormParam(value = "surname") String lastName,
			@FormParam(value = "email") String mail,
			@FormParam(value = "otpKey") String otpKey1,
			@FormParam(value = "otpSerial") String otpSerial) {

		Response response;

		Subject subjectRequest = new SubjectImpl();
		OtpProfile otpProfile = getDefaultOtpProfile();

		subjectRequest.setCommonName(commonName);
		subjectRequest.setEmail(mail);
		subjectRequest.setFirstName(givenName);
		subjectRequest.setLastName(lastName);

		otpProfile.setOtpKey1(otpKey1);
		otpProfile.setOtpSerial(otpSerial);

		subjectRequest.setOtpProfile(otpProfile);

		try {

			Subject responseSubject = 
				identityManagementService.createOrUpdateUser(subjectRequest);
			response = Response.created(URI.create("/" 
					+ responseSubject.getCommonName())).build();

		} catch (ServiceException e) {
			logger.error("update : " + e.getLocalizedMessage());
			throw new RestDefaultException();
		}

		return response;
	}

	@GET
	@Path("/delete")
	 
	public Response delete(@QueryParam("commonName") String commonName) {

		if (commonName == null)
			throw new RestBadConditionException();

		Response response;

		SubjectRestResult subjectRestResult = new SubjectRestResult();

		Subject subjectRequest = new SubjectImpl();
		subjectRequest.setCommonName(commonName);

		try {

			identityManagementService.deleteUser(subjectRequest);
			response = Response.status(Status.OK).build();

		} catch (ServiceException e) {

			logger.error( "read : " + e.getLocalizedMessage());
			throw new RestDefaultException();
		}

		return response;
	}

	private OtpProfile getDefaultOtpProfile() {

		OtpProfile otpProfile = new OtpProfile();

		otpProfile.setOtpKey1("d6c98f76c99d73e52f801d9eb74c59da4127e560");
		otpProfile.setOtpModel("d6");
		otpProfile.setOtpVendor("hotp");
		otpProfile.setOtpSerial("serial");

		return otpProfile;

	}
}
