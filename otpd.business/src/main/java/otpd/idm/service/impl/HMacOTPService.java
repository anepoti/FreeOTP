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

package otpd.idm.service.impl;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import otpd.algorithm.HMacOTPAlgorithm;
import otpd.idm.domain.Subject;
import otpd.idm.service.ServiceException;
import otpd.idm.service.UserService;

public class HMacOTPService {

	final Logger logger = LoggerFactory.getLogger(HMacOTPService.class);

	private UserService userService;
	private HMacOTPAlgorithmFactory hMacOTPAlgorithmFactory;

	public HMacOTPService() {
	}

	public void setHMacOTPAlgorithmFactory(
			HMacOTPAlgorithmFactory hMacOTPAlgorithmFactory) {
		this.hMacOTPAlgorithmFactory = hMacOTPAlgorithmFactory;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	/**
	 * Resynchronize the moving factor for the next OTP of a given device, using
	 * the last two valid passwords. This method looks up for the two given
	 * consecutive passwords, starting at startMovingFactor and checking the
	 * next resyncWindow valid passwords.
	 * 
	 * @param userProfile
	 *            a valid UserProfile needed to store otpstate file using the
	 *            common name.
	 * @param password1
	 *            a valid one time password
	 * @param password2
	 *            a valid one time password (must be the immediate next one to
	 *            password1) digest algorithms available.
	 * @throws Exception
	 *             The secret provided was not a valid HMAC-SHA-1 key. Due to
	 *             otpstate file writing problem.
	 * 
	 * @return the otpStateLine found, null otherwise
	 */

	public Long resynchUserToken(Subject userProfile, String password1,
			String password2) throws ServiceException {

		Long movingFactor = -1L;
		// Verify if userProfile is a valid profile.
		Subject validatedProfile = getValidatedProfile(userProfile);
		if (validatedProfile != null) {

			// Get the vendor : event ( hotp ) or time ( totp )
			String otpVendor = validatedProfile.getOtpProfile().getOtpVendor();

			HMacOTPAlgorithm hMacOTPAlgorithm = hMacOTPAlgorithmFactory
					.getAlgorthmMap().get(otpVendor);

			// Calculate the next moving factor
			movingFactor = new Long(hMacOTPAlgorithm.resyncMovingFactor(
					validatedProfile, password1, password2));
		}

		return movingFactor;

	}

	/**
	 * Validate password
	 * 
	 * @param userProfile
	 *            a valid UserProfile needed to store otpstate file using the
	 *            common name.
	 * @param password
	 *            a valid one time password
	 * @throws Exception
	 *             The secret provided was not a valid HMAC-SHA-1 key.
	 * 
	 */
	public boolean validatePassword(Subject userProfile, String password)
			throws ServiceException, InvalidKeyException,
			NoSuchAlgorithmException {

		boolean isValidPassword = false;

		Subject validatedProfile = getValidatedProfile(userProfile);

		if (validatedProfile != null) {

			// Get the vendor : event ( hotp ) or time ( totp )
			String otpVendor = validatedProfile.getOtpProfile().getOtpVendor();

			HMacOTPAlgorithm hMacOTPAlgorithm = hMacOTPAlgorithmFactory
					.getAlgorthmMap().get(otpVendor);

			isValidPassword = hMacOTPAlgorithm.validatePassword(
					validatedProfile, password);
		}

		return isValidPassword;
	}

	private Subject getValidatedProfile(Subject userProfile)
			throws ServiceException {
		Subject subject = null;

		subject = userService.getUser(userProfile);

		return subject;
	}

}
