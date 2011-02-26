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

package otpd.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StartUpOtpdServices implements IOtpdService {
	final static Logger logger = LoggerFactory
			.getLogger(StartUpOtpdServices.class);
	private AbstractApplicationContext context;

	public static void main(String argv[]) {

		StartUpOtpdServices startUpOtpdServices = new StartUpOtpdServices();

		Runtime.getRuntime().addShutdownHook(
				new OtpdServiceShutdownThread(startUpOtpdServices));

		try {
			startUpOtpdServices.start();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		}
	}

	public void start() throws Exception {
		context = new ClassPathXmlApplicationContext(
				new String[] { "classpath:config/spring/otpd-jradius-context.xml" });
		
		context.registerShutdownHook();
		logger.info("OTPD Server started");

	}

	 
	public void stop() throws Exception {
		context.destroy();
		context = null;
		logger.info("OTPD Server stopped");

	}
}
