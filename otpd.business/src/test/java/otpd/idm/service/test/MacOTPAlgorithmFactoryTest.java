package otpd.idm.service.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import otpd.algorithm.HMacOTPAlgorithm;
import otpd.idm.service.impl.HMacOTPAlgorithmFactory;

public class MacOTPAlgorithmFactoryTest {

	@Autowired(required = false)
	protected ClassPathXmlApplicationContext factory;

	@Before
	public void setUp() throws Exception {

		factory = new ClassPathXmlApplicationContext(
				"classpath:spring/test-otp-context.xml");

	}
	
	@Test
	public void factoryTest(){
		HMacOTPAlgorithmFactory manager = (HMacOTPAlgorithmFactory) factory.getBean("hMacOTPAlgorithmFactory"); 
		
		HMacOTPAlgorithm hMacOTPAlgorithm = manager.getAlgorthmMap().get("hotp");
		
		assertNotNull(hMacOTPAlgorithm);
		 
	}

}
