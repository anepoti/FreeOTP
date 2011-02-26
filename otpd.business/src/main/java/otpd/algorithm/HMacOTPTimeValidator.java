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

package otpd.algorithm;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Required;

import otpd.idm.domain.Subject;
import otpd.idm.service.ServiceException;
import otpd.idm.service.UserService;

public class HMacOTPTimeValidator extends HMacOTPAlgorithm {

	private int timeStepWindow;
	private boolean resyncOnValidate;

	public boolean isResyncOnValidate() {
		return resyncOnValidate;
	}

	@Required
	public void setResyncOnValidate(boolean resyncOnValidate) {
		this.resyncOnValidate = resyncOnValidate;
	}

	@Required
	public void setUserService(UserService userService) {

		this.userService = userService;
	}

	@Required
	public void setTimeStepWindow(int timeStepWindow) {
		this.timeStepWindow = timeStepWindow;
	}

	/**
	 * Calculates the moving factor for the next OTP of a given device.
	 * 
	 * @param subject
	 *            a valid subject
	 * @param password1
	 *            a valid one time password
	 * @param password2
	 *            a valid one time password (must be the immediate next one to
	 *            password1)
	 * @throws ServiceException
	 * @return the movingFactor of the next valid password if password1 and
	 *         password2 were found, -1 otherwise
	 */
	@Override
	public long resyncMovingFactor(Subject subject, String... passwords)
			throws ServiceException {
		int codeDigits = getCodeDigit(subject);
		
		String cryptoAlgorithm = subject.getOtpProfile().getOtpKeyEncryption();
		String crypto = (cryptoAlgorithm != null) ? cryptoAlgorithm : DEFAULT_CRYPTO;
	
		Map<String, Long> totpMap = getGeneratedTotp(subject.getOtpProfile()
				.getOtpKey1(), codeDigits, crypto);

		Long totpTimeInMillisec = totpMap.get(passwords[0]);

		Long deltaTimeInMillisec = getDeltaTimeFactor(totpMap.get(passwords[0]));
		updateMovingFactor(subject, deltaTimeInMillisec);

		return deltaTimeInMillisec;

	}

	/**
	 * Validates an HMAC-SHA1 OTP generated password using the OATH standard.
	 * 
	 * @param subject
	 *            the subject
	 * @param password
	 *            device one time password
	 * @throws ServiceException
	 * @return true if the password is valid for the given secret and
	 *         movingFactor, false otherwise
	 */
	public boolean validatePassword(Subject subject, String password)
			throws ServiceException {

		boolean validOneShotTimePassword = false;
		int codeDigits = getCodeDigit(subject);
		
		String cryptoAlgorithm = subject.getOtpProfile().getOtpKeyEncryption();
		String crypto = (cryptoAlgorithm != null) ? cryptoAlgorithm : DEFAULT_CRYPTO;
	

		Map<String, Long> totpMap = getGeneratedTotp(subject.getOtpProfile()
				.getOtpKey1(), codeDigits, crypto);

		Long totpTimeInMillisec = totpMap.get(password);

		if (totpTimeInMillisec != null) {

			validOneShotTimePassword = true;
			if (isResyncOnValidate()) {
				Long deltaTimeInMillisec = getDeltaTimeFactor(totpMap
						.get(password));
				updateMovingFactor(subject, deltaTimeInMillisec);
			}
		}
		return validOneShotTimePassword;
	}

	/**
	 * This method generates an TOTP value for the given set of parameters.
	 * 
	 * @param key
	 *            the shared secret, HEX encoded
	 * @param time
	 *            a value that reflects a time
	 * @param returnDigits
	 *            number of digits to return
	 * @param crypto
	 *            the crypto function to use
	 * 
	 * @return A numeric String in base 10 that includes
	 *         {@link truncationDigits} digits
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	private String generateTOTP(String key, String time, int codeDigits,
			String crypto) throws InvalidKeyException, NoSuchAlgorithmException {
		String result = null;

		// Using the counter
		// First 8 bytes are for the movingFactor
		// Complaint with base RFC 4226 (HOTP)
		while (time.length() < 16)
			time = "0" + time;

		// Get the HEX in a Byte[]
		byte[] msg = hexStr2Bytes(time);
		byte[] k = hexStr2Bytes(key);

		byte[] hash = hmac_sha(crypto, k, msg);

		// put selected bytes into result int
		int offset = hash[hash.length - 1] & 0xf;

		int binary = ((hash[offset] & 0x7f) << 24)
				| ((hash[offset + 1] & 0xff) << 16)
				| ((hash[offset + 2] & 0xff) << 8) | (hash[offset + 3] & 0xff);

		int otp = binary % DIGITS_POWER[codeDigits];

		result = Integer.toString(otp);
		while (result.length() < codeDigits) {
			result = "0" + result;
		}
		return result;
	}

	private Long calendarCalculator(Calendar cal, int sec) {
		cal.add(Calendar.SECOND, sec);
		return new Long(cal.getTimeInMillis());
	}

	private Map getGeneratedTotp(String seed, int codeDigits, String crypto)
			throws ServiceException {

		List<Long> dateListAsLong = new ArrayList();
		Map<String, Long> timeOtpGeneratedMap = new HashMap();
		String timeSteps = "0";

		// Get a Calendar for current locale and UTC time zone
		Calendar now = Calendar.getInstance();
		now.setTimeZone(TimeZone.getTimeZone("UTC"));

		for (int i = 1; i <= timeStepWindow; ++i) {
			dateListAsLong.add(calendarCalculator(now, i));
		}

		for (int i = 1; i <= timeStepWindow; ++i) {
			dateListAsLong.add(calendarCalculator(now, (i * -1)));
		}

		Iterator<Long> datesIter = dateListAsLong.iterator();

		while (datesIter.hasNext()) {

			String totp;
			Long timeKey = datesIter.next();
			timeSteps = Long.toHexString(timeKey);

			while (timeSteps.length() < 16)
				timeSteps = "0" + timeSteps;

			try {

				totp = generateTOTP(seed, timeSteps, codeDigits, crypto);
				timeOtpGeneratedMap.put(totp, timeKey);

			} catch (InvalidKeyException e) {
				throw new ServiceException(e.getLocalizedMessage());
			} catch (NoSuchAlgorithmException e) {
				throw new ServiceException(e.getLocalizedMessage());
			}
		}

		return timeOtpGeneratedMap;
	}

	private Long getDeltaTimeFactor(long calculatedTotpTimeInMillis) {
		// Get a Calendar for current locale and UTC time zone
		Calendar now = Calendar.getInstance();
		now.setTimeZone(TimeZone.getTimeZone("UTC"));

		long actualTimeInMillis = now.getTimeInMillis();

		return actualTimeInMillis - calculatedTotpTimeInMillis;
	}
}
