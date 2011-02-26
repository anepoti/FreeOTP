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

import org.springframework.beans.factory.annotation.Required;

import otpd.idm.domain.Subject;
import otpd.idm.service.ServiceException;
import otpd.idm.service.UserService;

/**
 * 
 * @author anepoties
 */
public class HMacOTPOcraValidator extends HMacOTPAlgorithm {

	private UserService userService;

	public HMacOTPOcraValidator() {
	}

	@Required
	public void setUserService(UserService userService) {
		super.userService = userService;
		this.userService = userService;
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

		return false;

	}

	/**
	 * Calculates the moving factor for the next OTP of a given device, using
	 * the last two valid passwords. This method looks up for the two given
	 * consecutive passwords, starting at startMovingFactor and checking the
	 * next resyncWindow valid passwords.
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
	public long resyncMovingFactor(Subject subject, String... passwords)
			throws ServiceException {

		return -1;
	}

	/**
	 * Generates an HMAC-SHA1 OTP using the OATH standard. This method assumes
	 * an 8 digit OTP, without checksum and dynamic truncation.
	 * 
	 * @param secret
	 *            device secret key, in hexadecimal notation
	 * @param movingFactor
	 *            the counter, time, or other value that changes on a per use
	 *            basis.
	 * @throws NoSuchAlgorithmException
	 *             if no provider makes either HmacSHA1 or HMAC-SHA-1 digest
	 *             algorithms available.
	 * @throws InvalidKeyException
	 *             The secret provided was not a valid HMAC-SHA-1 key.
	 * 
	 * @return true if the password is valid for the given secret and
	 *         movingFactor, false otherwise
	 */
	protected String generatePassword(String secret, long movingFactor)
			throws ServiceException {

		return null;
	}

	/**
	 * This method generates an OCRA HOTP value for the given set of parameters.
	 * 
	 * @param ocraSuite
	 *            the OCRA Suite
	 * @param key
	 *            the shared secret, HEX encoded
	 * @param counter
	 *            the counter that changes on a per use basis, HEX encoded
	 * @param question
	 *            the challenge question, HEX encoded
	 * @param password
	 *            a password that can be used, HEX encoded
	 * @param sessionInformation
	 *            Static information that identifies the current session, Hex
	 *            encoded
	 * @param timeStamp
	 *            a value that reflects a time
	 * 
	 * @return A numeric String in base 10 that includes
	 *         {@link truncationDigits} digits
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public String generateOCRA(String ocraSuite, String key, String counter,
			String question, String password, String sessionInformation,
			String timeStamp) throws InvalidKeyException,
			NoSuchAlgorithmException {

		int codeDigits = 0;
		String crypto = "";
		String result = null;
		int ocraSuiteLength = (ocraSuite.getBytes()).length;
		int counterLength = 0;
		int questionLength = 0;
		int passwordLength = 0;
		int sessionInformationLength = 0;
		int timeStampLength = 0;

		if (ocraSuite.toLowerCase().indexOf("sha1") > 1)
			crypto = "HmacSHA1";
		if (ocraSuite.toLowerCase().indexOf("sha256") > 1)
			crypto = "HmacSHA256";
		if (ocraSuite.toLowerCase().indexOf("sha512") > 1)
			crypto = "HmacSHA512";

		// How many digits should we return
		String oS = ocraSuite.substring(ocraSuite.indexOf(":"), ocraSuite
				.indexOf(":", ocraSuite.indexOf(":") + 1));
		codeDigits = Integer.decode(oS.substring(oS.lastIndexOf("-") + 1, oS
				.length()));

		// The size of the byte array message to be encrypted
		// Counter
		if (ocraSuite.toLowerCase().indexOf(":c") > 1) {
			// Fix the length of the HEX string
			while (counter.length() < 16)
				counter = "0" + counter;
			counterLength = 8;
		}
		// Question
		if ((ocraSuite.toLowerCase().indexOf(":q") > 1)
				|| (ocraSuite.toLowerCase().indexOf("-q") > 1)) {
			while (question.length() < 256)
				question = question + "0";
			questionLength = 128;
		}

		// Password
		if ((ocraSuite.toLowerCase().indexOf(":p") > 1)
				|| (ocraSuite.toLowerCase().indexOf("-p") > 1)) {
			while (password.length() < 40)
				password = "0" + password;
			passwordLength = 20;
		}

		// sessionInformation
		if ((ocraSuite.toLowerCase().indexOf(":s") > 1)
				|| (ocraSuite.toLowerCase().indexOf("-s",
						ocraSuite.indexOf(":", ocraSuite.indexOf(":") + 1)) > 1)) {
			while (sessionInformation.length() < 128)
				sessionInformation = "0" + sessionInformation;

			sessionInformationLength = 64;
		}
		// TimeStamp
		if ((ocraSuite.toLowerCase().indexOf(":t") > 1)
				|| (ocraSuite.toLowerCase().indexOf("-t") > 1)) {
			while (timeStamp.length() < 16)
				timeStamp = "0" + timeStamp;
			timeStampLength = 8;
		}

		// Remember to add "1" for the "00" byte delimiter
		byte[] msg = new byte[ocraSuiteLength + counterLength + questionLength
				+ passwordLength + sessionInformationLength + timeStampLength
				+ 1];

		// Put the bytes of "ocraSuite" parameters into the message
		byte[] bArray = ocraSuite.getBytes();
		for (int i = 0; i < bArray.length; i++) {
			msg[i] = bArray[i];
		}

		// Delimiter
		msg[bArray.length] = 0x00;

		// Put the bytes of "Counter" to the message
		// Input is HEX encoded
		if (counterLength > 0) {
			bArray = hexStr2Bytes(counter);
			for (int i = 0; i < bArray.length; i++) {
				msg[i + ocraSuiteLength + 1] = bArray[i];
			}
		}

		// Put the bytes of "question" to the message
		// Input is text encoded
		if (question.length() > 0) {
			bArray = hexStr2Bytes(question);
			for (int i = 0; i < bArray.length; i++) {
				msg[i + ocraSuiteLength + 1 + counterLength] = bArray[i];
			}
		}

		// Put the bytes of "password" to the message
		// Input is HEX encoded
		if (password.length() > 0) {
			bArray = hexStr2Bytes(password);
			for (int i = 0; i < bArray.length; i++) {
				msg[i + ocraSuiteLength + 1 + counterLength + questionLength] = bArray[i];
			}
		}

		// Put the bytes of "sessionInformation" to the message
		// Input is text encoded
		if (sessionInformation.length() > 0) {
			bArray = hexStr2Bytes(sessionInformation);
			for (int i = 0; i < 128 && i < bArray.length; i++) {
				msg[i + ocraSuiteLength + 1 + counterLength + questionLength
						+ passwordLength] = bArray[i];
			}
		}

		// Put the bytes of "time" to the message
		// Input is text value of minutes
		if (timeStamp.length() > 0) {
			bArray = hexStr2Bytes(timeStamp);
			for (int i = 0; i < 8 && i < bArray.length; i++) {
				msg[i + ocraSuiteLength + 1 + counterLength + questionLength
						+ passwordLength + sessionInformationLength] = bArray[i];
			}
		}

		byte[] hash;
		bArray = hexStr2Bytes(key);

		hash = hmac_sha(crypto, bArray, msg);

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

}
