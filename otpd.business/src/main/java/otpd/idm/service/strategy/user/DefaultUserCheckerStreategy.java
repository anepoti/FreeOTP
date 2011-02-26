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
package otpd.idm.service.strategy.user;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import otpd.idm.domain.Subject;

public class DefaultUserCheckerStreategy extends UserCheckerStrategy {

	@Override
	protected String getIdStrategy(Subject subject) {
		String defaultId = "";
		try {
			defaultId = merge(subject);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return defaultId;
	}

	private String merge(Subject target) throws Exception {

		BeanInfo beanInfo = Introspector.getBeanInfo(target.getClass());
		StringBuilder idBuilder = new StringBuilder();
		for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {

			if (descriptor.getWriteMethod() != null) {

				if (descriptor.getName().equalsIgnoreCase("firstname")) {

					Object firstnameValue = descriptor.getReadMethod().invoke(
							target);

					idBuilder.append(firstnameValue);
					idBuilder.append(".");

				}
				if (descriptor.getName().equalsIgnoreCase("lastname")) {

					Object lastanmeValue = descriptor.getReadMethod().invoke(
							target);
					idBuilder.append(lastanmeValue);

				}
			}
		}
		return idBuilder.toString();
	}
}
