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

import org.springframework.beans.factory.annotation.Required;

import otpd.idm.domain.Subject;
import otpd.idm.service.ServiceException;
import otpd.idm.service.UserService;

/**
 * 
 * @author anepoties
 */
public class HMacOTPEventValidator extends HMacOTPAlgorithm {

	//private UserService userService;
	private int startMovingFactor;
	private int resyncWindow;

	private boolean resyncOnValidate;

	public HMacOTPEventValidator() {
	}


	@Required
	public void setUserService(UserService userService) {
		this.userService = userService;
	}


	@Required
	public void setStartMovingFactor(int startMovingFactor) {

		this.startMovingFactor = startMovingFactor;

	}

	public boolean isResyncOnValidate() {
		return resyncOnValidate;
	}

	@Required
	public void setResyncOnValidate(boolean resyncOnValidate) {
		this.resyncOnValidate = resyncOnValidate;
	}

	public int getStartMovingFactor() {

		return startMovingFactor;

	}

	@Required
	public void setResyncWindow(int resyncWindow) {

		this.resyncWindow = resyncWindow;

	}

	public int getResyncWindow() {

		return resyncWindow;

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

		logger.debug("subject " + subject);
		logger.debug("password " + password);
		
		String cryptoAlgorithm = subject.getOtpProfile().getOtpKeyEncryption();
		String crypto = (cryptoAlgorithm != null) ? cryptoAlgorithm : DEFAULT_CRYPTO;
		

		int movingfactor = Integer.parseInt(subject.getOtpProfile()
				.getOtpMovingFactor());

		logger.debug("movingfactor " + movingfactor);

		if (movingfactor != -1)
			startMovingFactor = movingfactor - 1;

		int codeDigits = getCodeDigit(subject);

		logger.debug("codeDigits " + codeDigits);

		boolean isValidOtp = false;
		try {
			isValidOtp = validatePassword(password, subject
					.getOtpProfile().getOtpKey1(), startMovingFactor,
					codeDigits, DEFAULT_ADD_CHECKSUM, DEFAULT_TRUNCATION_OFFSET,crypto);

			logger.debug("validPassword " + isValidOtp);

			if (isResyncOnValidate()) {
				startMovingFactor = resyncMovingFactor(subject.getOtpProfile()
						.getOtpKey1(), movingfactor, codeDigits, password,crypto);

				if (startMovingFactor > -1) {
					updateMovingFactor(subject, startMovingFactor);
					isValidOtp = true;
				}

			}

		} catch (InvalidKeyException e) {
			throw new ServiceException(e.getLocalizedMessage());
		} catch (NoSuchAlgorithmException e) {
			throw new ServiceException(e.getLocalizedMessage());
		}

		return isValidOtp;

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
	public long resyncMovingFactor(Subject subject, String... passwords) throws ServiceException {

		String secret = subject.getOtpProfile().getOtpKey1();
		
		String cryptoAlgorithm = subject.getOtpProfile().getOtpKeyEncryption();
		String crypto = (cryptoAlgorithm != null) ? cryptoAlgorithm : DEFAULT_CRYPTO;
	
		String password1 = passwords[0];
		String password2 = passwords[1];
		

		if (logger.isDebugEnabled()) {

			logger.debug("password1 : " + password1);
			logger.debug("password2 : " + password2);
			logger.debug("secret : " + secret);

		}

		int movingFactor = Integer.parseInt(subject.getOtpProfile()
				.getOtpMovingFactor());

		if (movingFactor != -1)
			startMovingFactor = movingFactor;

		int codeDigits = getCodeDigit(subject);

		boolean ok1 = false;
		boolean ok2 = false;

		byte[] secretAsBytesArray = new BigInteger(secret, 16).toByteArray();

		int counter = 1;

		while (!ok2 & counter <= resyncWindow) {
			String otpPassword;
			try {
				otpPassword = generateOTP(secretAsBytesArray, movingFactor,
						codeDigits, DEFAULT_ADD_CHECKSUM,
						DEFAULT_TRUNCATION_OFFSET, crypto);

				if (logger.isDebugEnabled()) {
					logger.debug("otpPassword : " + otpPassword);
				}

				if (ok1) {
					if (otpPassword.equals(password2)) {
						ok2 = true;
						break;
					}
					ok1 = false;
				}
				if (!ok1 && otpPassword.equals(password1)) {
					ok1 = true;
				}
				movingFactor++;
				counter++;

			} catch (InvalidKeyException e) {
				throw new ServiceException(e.getLocalizedMessage());
			} catch (NoSuchAlgorithmException e) {
				throw new ServiceException(e.getLocalizedMessage());
			}
		}

		if (ok2) {
			if (logger.isDebugEnabled()) {
				logger.debug("otpPassword : granted");
			}
			return ++movingFactor;
		} else {
			return -1;
		}
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
	protected String generatePassword(String secret, long movingFactor, String crypto)
			throws ServiceException {

		return generatePassword(secret, movingFactor, DEFAULT_CODE_DIGITS,
				DEFAULT_ADD_CHECKSUM, DEFAULT_TRUNCATION_OFFSET, crypto);
	}

	/**
	 * Validates an HMAC-SHA1 OTP generated password using the OATH standard.
	 * 
	 * @param password
	 *            the one time password
	 * @param secret
	 *            device secret key, in hexadecimal notation
	 * @param movingFactor
	 *            the counter, time, or other value that changes on a per use
	 *            basis.
	 * @param codeDigits
	 *            the number of digits in the OTP, not including the checksum,
	 *            if any.
	 * @param addChecksum
	 *            a flag that indicates if a checksum digit should be appended
	 *            to the OTP.
	 * @param truncationOffset
	 *            the offset into the MAC result to begin truncation. If this
	 *            value is out of the range of 0 ... 15, then dynamic truncation
	 *            will be used. Dynamic truncation is when the last 4 bits of
	 *            the last byte of the MAC are used to determine the start
	 *            offset.
	 * @throws NoSuchAlgorithmException
	 *             if no provider makes either HmacSHA1 or HMAC-SHA-1 digest
	 *             algorithms available.
	 * @throws InvalidKeyException
	 *             The secret provided was not a valid HMAC-SHA-1 key.
	 * 
	 * @return true if the password is valid for the given secret and
	 *         movingFactor, false otherwise
	 */
	protected boolean validatePassword(String password, String secret,
			long movingFactor, int codeDigits, boolean addChecksum,
			int truncationOffset, String crypto) throws ServiceException {

		boolean result = false;
		byte[] secretAsBytesArray = new BigInteger(secret, 16).toByteArray();

		String n;
		try {
			n = generateOTP(secretAsBytesArray, movingFactor, codeDigits,
					addChecksum, truncationOffset, crypto);

			result = n.equals(password);

		} catch (InvalidKeyException e) {
			throw new ServiceException(e.getLocalizedMessage());
		} catch (NoSuchAlgorithmException e) {
			throw new ServiceException(e.getLocalizedMessage());
		}
		return result;
	}

	/**
	 * Generates an HMAC-SHA1 OTP using the OATH standard.
	 * 
	 * @param secret
	 *            device secret key, in hexadecimal notation
	 * @param movingFactor
	 *            the counter, time, or other value that changes on a per use
	 *            basis.
	 * @param codeDigits
	 *            the number of digits in the OTP, not including the checksum,
	 *            if any.
	 * @param addChecksum
	 *            a flag that indicates if a checksum digit should be appended
	 *            to the OTP.
	 * @param truncationOffset
	 *            the offset into the MAC result to begin truncation. If this
	 *            value is out of the range of 0 ... 15, then dynamic truncation
	 *            will be used. Dynamic truncation is when the last 4 bits of
	 *            the last byte of the MAC are used to determine the start
	 *            offset.
	 * @throws NoSuchAlgorithmException
	 *             if no provider makes either HmacSHA1 or HMAC-SHA-1 digest
	 *             algorithms available.
	 * @throws InvalidKeyException
	 *             The secret provided was not a valid HMAC-SHA-1 key.
	 * 
	 * @return true if the password is valid for the given secret and
	 *         movingFactor, false otherwise
	 */
	protected String generatePassword(String secret, long movingFactor,
			int codeDigits, boolean addChecksum, int truncationOffset, String crypto)
			throws ServiceException {

		byte[] secretAsBytesArray = new BigInteger(secret, 16).toByteArray();

		String n;
		try {
			n = generateOTP(secretAsBytesArray, movingFactor, codeDigits,
					addChecksum, truncationOffset, crypto);
		} catch (InvalidKeyException e) {
			throw new ServiceException(e.getLocalizedMessage());
		} catch (NoSuchAlgorithmException e) {
			throw new ServiceException(e.getLocalizedMessage());
		}

		return n;
	}

	/**
	 * This method generates an OTP value for the given set of parameters.
	 * 
	 * @param secret
	 *            the shared secret
	 * @param movingFactor
	 *            the counter, time, or other value that changes on a per use
	 *            basis.
	 * @param codeDigits
	 *            the number of digits in the OTP, not including the checksum,
	 *            if any.
	 * @param addChecksum
	 *            a flag that indicates if a checksum digit should be appended
	 *            to the OTP.
	 * @param truncationOffset
	 *            the offset into the MAC result to begin truncation. If this
	 *            value is out of the range of 0 ... 15, then dynamic truncation
	 *            will be used. Dynamic truncation is when the last 4 bits of
	 *            the last byte of the MAC are used to determine the start
	 *            offset.
	 * @throws NoSuchAlgorithmException
	 *             if no provider makes either HmacSHA1 or HMAC-SHA-1 digest
	 *             algorithms available.
	 * @throws InvalidKeyException
	 *             The secret provided was not a valid HMAC-SHA-1 key.
	 * 
	 * @return A numeric String in base 10 that includes {@link codeDigits}
	 *         digits plus the optional checksum digit if requested.
	 */
	private String generateOTP(byte[] secret, long movingFactor,
			int codeDigits, boolean addChecksum, int truncationOffset, String crypto)
			throws NoSuchAlgorithmException, InvalidKeyException {

		// put movingFactor value into text byte array
		String result = null;
		int digits = addChecksum ? (codeDigits + 1) : codeDigits;
		byte[] text = new byte[8];
		for (int i = text.length - 1; i >= 0; i--) {
			text[i] = (byte) (movingFactor & 0xff);
			movingFactor >>= 8;
		}
		// compute hmac hash
		byte[] hash = hmac_sha(crypto,secret, text);
		// put selected bytes into result int
		int offset = hash[hash.length - 1] & 0xf;
		if ((0 <= truncationOffset) && (truncationOffset < (hash.length - 4))) {
			offset = truncationOffset;
		}
		int binary = ((hash[offset] & 0x7f) << 24)
				| ((hash[offset + 1] & 0xff) << 16)
				| ((hash[offset + 2] & 0xff) << 8) | (hash[offset + 3] & 0xff);
		int otp = binary % DIGITS_POWER[codeDigits];
		if (addChecksum) {
			otp = (otp * 10) + calcChecksum(otp, codeDigits);
		}
		result = Integer.toString(otp);
		while (result.length() < digits) {
			result = "0" + result;
		}
		return result;
	}

	private int resyncMovingFactor(String secret, int movingFactor,
			int codeDigits, String password, String crypto) throws InvalidKeyException,
			NoSuchAlgorithmException {

		if (logger.isDebugEnabled()) {

			logger.debug("password1 : " + password);
			logger.debug("secret : " + secret);

		}

		boolean ok = false;

		byte[] secretAsBytesArray = new BigInteger(secret, 16).toByteArray();

		int counter = 1;

		while (!ok & counter <= resyncWindow) {
			String otpPassword = generateOTP(secretAsBytesArray, movingFactor,
					codeDigits, DEFAULT_ADD_CHECKSUM, DEFAULT_TRUNCATION_OFFSET, crypto);

			if (logger.isDebugEnabled()) {
				logger.debug("otpPassword : " + otpPassword);
			}
			if (otpPassword.equals(password)) {
				ok = true;
				break;
			}
			movingFactor++;
			counter++;
		}

		if (ok) {
			if (logger.isDebugEnabled()) {
				logger.debug("otpPassword : granted");
			}
			return movingFactor;
		} else {
			return -1;
		}
	}

}
