package otpd.idm.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import otpd.idm.domain.Subject;
import otpd.idm.domain.impl.SubjectImpl;
import otpd.idm.service.impl.HMacOTPService;

public class HMacOTPEventAlgorithmTest extends HMacOTPAlgorithmTest {

	private HMacOTPService hMacOTPService;

	@Test
	public void resynchHOTPUserTokenTest() throws Exception {

		createUserForTest("hotp");
		hMacOTPService = (HMacOTPService) factory.getBean("hMacOTPService");

		Subject subject = new SubjectImpl();
		
		subject.setId("otpd.test");
		subject.setCommonName("otpd-test");
		subject.setFirstName("firstName");
		subject.setLastName("lastname");
		subject.setEmail("email");

		Long otpMovingFactor = hMacOTPService.resynchUserToken(subject,
				"251810", "205671");

		assertEquals(otpMovingFactor, new Long(118));

	}

	@Test
	public void validateHOTPUserTokenTest() throws Exception {
		createUserForTest("hotp");

		hMacOTPService = (HMacOTPService) factory.getBean("hMacOTPService");

		Subject subject = new SubjectImpl();

		subject.setId("otpd.test");
		subject.setCommonName("otpd-test");
		subject.setFirstName("firstName");
		subject.setLastName("lastname");
		subject.setEmail("email");

		boolean validated = hMacOTPService.validatePassword(subject, "005121");

		assertTrue(validated);

	}
	
	 
}
