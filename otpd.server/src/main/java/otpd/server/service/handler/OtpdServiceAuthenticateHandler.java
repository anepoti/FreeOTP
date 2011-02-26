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


package otpd.server.service.handler;

import java.security.NoSuchAlgorithmException;

import net.jradius.dictionary.Attr_UserName;
import net.jradius.dictionary.Attr_UserPassword;
import net.jradius.exception.RadiusException;
import net.jradius.handler.PacketHandlerBase;
import net.jradius.log.RadiusLog;
import net.jradius.packet.RadiusPacket;
import net.jradius.packet.attribute.AttributeList;
import net.jradius.server.JRadiusRequest;
import net.jradius.server.JRadiusServer;
import otpd.idm.domain.Subject;
import otpd.idm.domain.impl.SubjectImpl;
import otpd.idm.service.ServiceException;
import otpd.idm.service.impl.HMacOTPService;

public class OtpdServiceAuthenticateHandler extends PacketHandlerBase {

	private HMacOTPService hMacOTPService;

	@Override
	public boolean handle(JRadiusRequest jRequest) {

		boolean isAuthenticated = false;

		RadiusLog.debug("OtpdServiceAuthenticateHandler handle: " + jRequest);
		try {
			/*
			 * Gather some information about the JRadius request
			 */
			int type = jRequest.getType();
			AttributeList ci = jRequest.getConfigItems();
			RadiusPacket req = jRequest.getRequestPacket();
			RadiusPacket rep = jRequest.getReplyPacket();

			RadiusLog.debug("JRadiusRequest Session: "
					+ jRequest.getSessionKey());
			/*
			 * Find the username in the request packet
			 */
			String username = (String) req
					.getAttributeValue(Attr_UserName.TYPE);

			/*
			 * Find the password in the request packet
			 */

			byte[] ret = (byte[]) req.getAttributeValue(Attr_UserPassword.TYPE);

			String password = new String(ret);

			/*
			 * y See if this is a local user, otherwise we will reject (though,
			 * you may want to return "ok" if you have modules configured after
			 * jradius in FreeRADIUS)
			 */

			if (username == null) {
				// Unknown username, so let the RADIUS server sort it out.
				RadiusLog.info("Ignoring unknown username: " + username);
				isAuthenticated = false;

			}

			switch (type) {

			case JRadiusServer.JRADIUS_authenticate: {
				/*
				 * We know the user, lets inform FreeRADIUS of the user's
				 * password so that FreeRADIUS may perform the required
				 * authentication checks.
				 */
				// ci.add(new Attr_AuthType(Attr_AuthType.Local)); // Auth
				// locally
				RadiusLog.debug("password: " + new String(ret));
				RadiusLog.debug("username: " + username);

				hMacOTPService = (HMacOTPService) jRequest
						.getApplicationContext().getBean("hMacOTPService");

				try {
					if (isValidToken(hMacOTPService, username, password)) {
						isAuthenticated = true;

						jRequest.setReturnValue(JRadiusServer.RLM_MODULE_OK);

					} else {
						jRequest
								.setReturnValue(JRadiusServer.RLM_MODULE_REJECT);
						isAuthenticated = false;
					}

				} catch (Exception e) {
					isAuthenticated = false;
					
					e.printStackTrace();
					RadiusLog.error(e.getMessage());
					jRequest.setReturnValue(JRadiusServer.RLM_MODULE_REJECT);
				}
			}
				break;
			}
		} catch (RadiusException e) {
			isAuthenticated = false;
			
			e.printStackTrace();
			RadiusLog.error(e.getMessage());
		}

		/*
		 * Everything worked out well, from the perspective of this module.
		 */

		return isAuthenticated;
	}

	private boolean isValidToken(HMacOTPService hMacOTPService,
			String username, String password) throws Exception,
			NoSuchAlgorithmException, ServiceException {

		Subject subject = new SubjectImpl();
		subject.setId(username);
		subject.setCommonName(username);

		return hMacOTPService.validatePassword(subject, password);
	}
}
