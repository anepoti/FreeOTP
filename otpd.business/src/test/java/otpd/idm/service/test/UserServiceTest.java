package otpd.idm.service.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import otpd.idm.domain.Subject;
import otpd.idm.domain.impl.OtpProfile;
import otpd.idm.domain.impl.SubjectImpl;
import otpd.idm.service.ServiceException;
import otpd.idm.service.UserService;

public class UserServiceTest {

	@Autowired(required = false)
	private UserService userService;
	private ClassPathXmlApplicationContext factory;

	@Before
	public void setUp() throws Exception {
		factory = new ClassPathXmlApplicationContext(
				"classpath:config/spring/test-otp-context.xml");
	}

 	
	@Test
	public void saveTest() throws ServiceException {
		
		userService = (UserService) factory.getBean("userService");

		OtpProfile otpProfile = new OtpProfile();

		otpProfile.setOtpKey1("431821a5b42b2d3d3820855770317607fc3dcb70");
		otpProfile.setOtpModel("d6");
		otpProfile.setOtpVendor("hotp");
		otpProfile.setOtpSerial("serial");
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
	
	//@Test
	public void updateTest() throws ServiceException {
		
		userService = (UserService) factory.getBean("userService");

		OtpProfile otpProfile = new OtpProfile();

		otpProfile.setOtpKey1("431821a5b42b2d3d3820855770317607fc3dcb70");
		otpProfile.setOtpModel("d6");
		otpProfile.setOtpVendor("hotp");
		otpProfile.setOtpSerial("serial");
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
	
	@Test
	public void getUserTest() throws ServiceException {
		Subject subject = new SubjectImpl();

		subject.setId("otpd.test");
		subject.setCommonName("otpd-test");
	 
		userService = (UserService) factory.getBean("userService");
		userService.getUser(subject);
	}
	
	@Test
	public void deleteUserTest() throws ServiceException {
		
		userService = (UserService) factory.getBean("userService");
		Subject subject = new SubjectImpl();

		subject.setId("otpd.test");
		subject.setCommonName("otpd-test");
		userService.delete(subject);
	}
	
}
