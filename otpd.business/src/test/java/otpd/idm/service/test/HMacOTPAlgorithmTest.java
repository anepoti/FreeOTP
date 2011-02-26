package otpd.idm.service.test;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import otpd.idm.domain.Subject;
import otpd.idm.domain.impl.OtpProfile;
import otpd.idm.domain.impl.SubjectImpl;
import otpd.idm.service.ServiceException;
import otpd.idm.service.UserService;

public class HMacOTPAlgorithmTest {

	@Autowired(required = false)
	protected ClassPathXmlApplicationContext factory;
	 

	@Before
	public void setUp() throws Exception {

		factory = new ClassPathXmlApplicationContext(
				"classpath:config/spring/test-otp-context.xml");

	}

	@After
	public void tearDown() throws Exception {

		cleanUp();
	}

	protected void createUserForTest(String vendor) throws ServiceException {

		UserService userService = (UserService) factory.getBean("userService");

		OtpProfile otpProfile = new OtpProfile();

		otpProfile.setOtpKey1("431821a5b42b2d3d3820855770317607fc3dcb70");
				
		otpProfile.setOtpModel("d6");
		otpProfile.setOtpVendor(vendor);
		otpProfile.setOtpSerial("73076067");
		otpProfile.setOtpMovingFactor("0");
		otpProfile.setOtpPIN("123456");
		otpProfile.setOtpKeyEncryption("HmacSHA1");

		Subject subject = new SubjectImpl();

		subject.setId("otpd.test");
		subject.setCommonName("otpd-test");
		subject.setFirstName("firstName");
		subject.setLastName("lastname");
		subject.setEmail("email");

		subject.setOtpProfile(otpProfile);

		userService.saveOrUpdate(subject);

	}

	private void cleanUp() throws ServiceException {

		UserService userService = (UserService) factory.getBean("userService");
		Subject subject = new SubjectImpl();

		subject.setId("otpd.test");
		subject.setCommonName("otpd-test");
		userService.delete(subject);

	}
}
