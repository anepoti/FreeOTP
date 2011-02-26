package otpd.idm.service.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import otpd.idm.domain.Subject;
import otpd.idm.domain.impl.OtpProfile;
import otpd.idm.domain.impl.SubjectImpl;
import otpd.idm.service.ServiceException;
import otpd.idm.service.UserService;
import otpd.idm.service.impl.HMacOTPService;
import otpd.idm.service.strategy.synch.StateStrategyContext;

public class StrategyServiceTest {

	@Autowired(required = false)
	private ClassPathXmlApplicationContext factory;
	private StateStrategyContext stateStrategyContext;
	private HMacOTPService hMacOTPService;

	@Before
	public void setUp() throws Exception {

		factory = new ClassPathXmlApplicationContext(
				"classpath:spring/test-otp-context.xml");

		createUserForTest();

	}

	@After
	public void tearDown() throws Exception {
		cleanUp();
	}

	@Test
	public void resynchUserTokenTest() throws Exception {

		stateStrategyContext = (StateStrategyContext) factory
				.getBean("strategyContext");

		hMacOTPService = (HMacOTPService) factory.getBean("hMacOTPService");

		Subject subject = new SubjectImpl();

		subject.setCommonName("otpd");
	
		Long nextFactor = hMacOTPService.resynchUserToken(subject, "162619", "477965");
		
		stateStrategyContext.getOtpStateStrategy().writeOtpState(nextFactor, subject);
		
		assertEquals(nextFactor, new Long(90));

	}

	private void createUserForTest() throws ServiceException {

		UserService userService = (UserService) factory.getBean("userService");

		OtpProfile otpProfile = new OtpProfile();

		otpProfile.setOtpKey1("2107136f49ec3985ce3cd3c11a1dd54507b4e599");
		otpProfile.setOtpModel("d6");
		otpProfile.setOtpVendor("hotp");
		otpProfile.setOtpSerial("serial");
		otpProfile.setOtpMovingFactor("0");

		Subject subject = new SubjectImpl();

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

		subject.setCommonName("otpd-test");
		userService.delete(subject);

	}

}
