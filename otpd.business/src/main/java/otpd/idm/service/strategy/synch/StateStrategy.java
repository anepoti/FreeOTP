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


package otpd.idm.service.strategy.synch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import otpd.idm.domain.Subject;
import otpd.idm.service.ServiceException;

public abstract class StateStrategy {
	
	final Logger logger = LoggerFactory.getLogger(StateStrategy.class);
	private static final String COLON = ":";
	
	public abstract boolean writeOtpState(long otpMovingFactor, Subject subject) throws ServiceException;

	protected String generateOtpstateLine(Subject userProfile,
			Long nextMovingFactor) {
		// syncdata = "5:#{@user}:#{@counter}:::0:0:0:\n"

		StringBuilder syncDataBuilder = new StringBuilder();

		syncDataBuilder.append("5");
		syncDataBuilder.append(COLON);
		syncDataBuilder.append(userProfile.getCommonName());
		syncDataBuilder.append(COLON);

		String otpStateLinePadded = padLeft(nextMovingFactor.longValue(), 16);

		syncDataBuilder.append(otpStateLinePadded);

		syncDataBuilder.append(COLON);
		syncDataBuilder.append(COLON);
		syncDataBuilder.append(COLON);
		syncDataBuilder.append("0");
		syncDataBuilder.append(COLON);
		syncDataBuilder.append("0");
		syncDataBuilder.append(COLON);
		syncDataBuilder.append("0");
		syncDataBuilder.append(COLON);
		syncDataBuilder.append("\n");

		return syncDataBuilder.toString();
	}

	private String padLeft(long counter, int n) {
		return String.format("%0" + n + "x", counter);

	}
}
