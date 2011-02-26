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

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import otpd.idm.domain.Subject;
import otpd.idm.service.ServiceException;
import otpd.idm.service.UserService;

public abstract class HMacOTPAlgorithm {
	final Logger logger = LoggerFactory.getLogger(HMacOTPEventValidator.class);

	protected final static String HOTP_DIGIT_6 = "d6";
	protected final static String HOTP_DIGIT_8 = "d8";

	protected final static String DEFAULT_CRYPTO = "HmacSHA1";
	
	protected static final int DEFAULT_CODE_DIGITS = 6;
	protected static final boolean DEFAULT_ADD_CHECKSUM = false;
	protected static final int DEFAULT_TRUNCATION_OFFSET = 16;

	protected static final int[] doubleDigits = { 0, 2, 4, 6, 8, 1, 3, 5, 7, 9 };
	protected static final int[] DIGITS_POWER = { 1, 10, 100, 1000, 10000,
			100000, 1000000, 10000000, 100000000 };

	protected UserService userService;
	
	protected String crypto = DEFAULT_CRYPTO;

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

	public abstract boolean validatePassword(Subject subject, String password)
			throws ServiceException;

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
	public abstract long resyncMovingFactor(Subject subject,
			String... passwords) throws ServiceException;



	/**
	 * This method uses the JCE to provide the crypto algorithm. HMAC computes a
	 * Hashed Message Authentication Code with the crypto hash algorithm as a
	 * parameter.
	 * 
	 * @param crypto
	 *            the crypto algorithm (HmacSHA1, HmacSHA256, HmacSHA512)
	 * @param keyBytes
	 *            the bytes to use for the HMAC key
	 * @param text
	 *            the message or text to be authenticated.
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	protected byte[] hmac_sha(String crypto, byte[] keyBytes, byte[] text)
			throws NoSuchAlgorithmException, InvalidKeyException {

		Mac hmac;
		hmac = Mac.getInstance(crypto);
		SecretKeySpec macKey = new SecretKeySpec(keyBytes, "RAW");
		hmac.init(macKey);
		return hmac.doFinal(text);

	}

	protected int getCodeDigit(Subject validatedProfile) {

		int codeDigits = -1;
		String model = validatedProfile.getOtpProfile().getOtpModel();

		if (model.contains(HOTP_DIGIT_6))
			codeDigits = 6;
		if (model.contains(HOTP_DIGIT_8))
			codeDigits = 8;

		return codeDigits;
	}

	/**
	 * This method converts HEX string to Byte[]
	 * 
	 * @param hex
	 *            the HEX string
	 * 
	 * @return A byte array
	 */
	protected byte[] hexStr2Bytes(String hex) {
		// Adding one byte to get the right conversion
		// values starting with "0" can be converted
		byte[] bArray = new BigInteger("10" + hex, 16).toByteArray();

		// Copy all the REAL bytes, not the "first"
		byte[] ret = new byte[bArray.length - 1];
		for (int i = 0; i < ret.length; i++)
			ret[i] = bArray[i + 1];
		return ret;
	}

	
	/**
	 * Calculates the checksum using the credit card algorithm. This algorithm
	 * has the advantage that it detects any single mistyped digit and any
	 * single transposition of adjacent digits.
	 * 
	 * @param num
	 *            the number to calculate the checksum for
	 * @param digits
	 *            number of significant places in the number
	 * 
	 * @return the checksum of num
	 */
	protected int calcChecksum(long num, int digits) {
		boolean doubleDigit = true;
		int total = 0;
		while (0 < digits--) {
			int digit = (int) (num % 10);
			num /= 10;
			if (doubleDigit) {
				digit = doubleDigits[digit];
			}
			total += digit;
			doubleDigit = !doubleDigit;
		}
		int result = total % 10;
		if (result > 0) {
			result = 10 - result;
		}
		return result;
	}

	protected void updateMovingFactor(Subject userProfile, long movingfactor)
			throws ServiceException {

		Subject subject = null;

		subject = userService.getUser(userProfile);
		subject.getOtpProfile().setOtpMovingFactor("" + (movingfactor + 1));

		userService.saveOrUpdate(subject);

	}
}
