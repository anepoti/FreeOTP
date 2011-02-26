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

package otpd.idm.domain.impl;

import java.io.Serializable;

public class OtpProfile implements Serializable {

    /**
     * 
     */
    private String otpKey1= "";
    private String otpModel= "";
    private String otpVendor= "";
    private String otpSerial = "";
    private String otpPIN = "0000";
    private String otpMovingFactor = "";
    private String otpKeyEncryption = "";

    public String getOtpKeyEncryption() {
		return otpKeyEncryption;
	}

	public void setOtpKeyEncryption(String otpKeyEncryption) {
		this.otpKeyEncryption = otpKeyEncryption;
	}

	public String getOtpMovingFactor() {
		return otpMovingFactor;
	}

	public void setOtpMovingFactor(String otpMovingFactor) {
		this.otpMovingFactor = otpMovingFactor;
	}


	public String getOtpSerial() {
        return otpSerial;
    }

    public void setOtpSerial(String otpSerial) {
        this.otpSerial = otpSerial;
    }
    
    public String getOtpPIN() {
        return otpPIN;
    }

    public void setOtpPIN(String otpPIN) {
        this.otpPIN = otpPIN;
    }

    public String getOtpKey1() {
        return otpKey1;
    }

    public void setOtpKey1(String otpKey1) {
        this.otpKey1 = otpKey1;
    }

    public String getOtpModel() {
        return otpModel;
    }

    public void setOtpModel(String otpModel) {
        this.otpModel = otpModel;
    }

    public String getOtpVendor() {
        return otpVendor;
    }

    public void setOtpVendor(String otpVendor) {
        this.otpVendor = otpVendor;
    }

    public OtpProfile() {
    }

    @Override
    public String toString() {
        StringBuffer otpDTOStr = new StringBuffer("OTP=[");

        otpDTOStr.append(" key1 = " + otpKey1);
        otpDTOStr.append(", Model = " + otpModel);
        otpDTOStr.append(", Vendor = " + otpVendor);
        otpDTOStr.append(", Serial = " + otpSerial);
        otpDTOStr.append(", Pin = " + otpPIN);
        otpDTOStr.append(", MovingFactor = " + otpMovingFactor);
        otpDTOStr.append(", KeyEncryption = " + otpKeyEncryption);
        
        otpDTOStr.append(" ]");

        return otpDTOStr.toString();
    }
}
