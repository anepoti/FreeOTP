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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

 

import otpd.idm.domain.Subject;
import otpd.idm.service.ServiceException;

public class FileStateStrategy extends StateStrategy {
	
	
	private String otpFileFullPath = "";
	private String fEncoding;

	public String getOtpFileFullPath() {
		return otpFileFullPath;
	}

	public void setOtpFileFullPath(String fFilePath) {
		this.otpFileFullPath = fFilePath;
	}

	 

	private void write(long otpMovingFactor, Subject subject) throws IOException {
		Writer out = null;
		
		if (logger.isDebugEnabled()) {
			logger.debug("otpMovingFactor : " + otpMovingFactor);
			logger.debug("subject : " + subject);
			logger.debug("file : " 
					+ otpFileFullPath + File.separator + subject.getCommonName());
	 		
		} 
		String otpStateLine = generateOtpstateLine(subject, otpMovingFactor);
		
		if (otpStateLine != null) {
			try {
				out = new OutputStreamWriter(new FileOutputStream(
						otpFileFullPath + File.separator + subject.getCommonName()));
				out.write(otpStateLine);
			} finally {
				out.close();
			}
		}
	}

	@Override
	public boolean writeOtpState(long otpMovingFactor, Subject subject)
			throws ServiceException {
		try {
			write(otpMovingFactor, subject);

		} catch (Exception e) {
			throw new ServiceException(e.getLocalizedMessage());
		}
		return false;
	}
	
	

}
