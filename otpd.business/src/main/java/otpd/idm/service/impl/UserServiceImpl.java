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

package otpd.idm.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.picketlink.idm.api.Attribute;
import org.picketlink.idm.api.IdentitySearchCriteria;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.UnsupportedCriterium;
import org.picketlink.idm.api.User;
import org.picketlink.idm.api.query.UserQuery;
import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.impl.api.IdentitySearchCriteriaImpl;
import org.picketlink.idm.impl.api.SimpleAttribute;
import org.picketlink.idm.impl.api.query.UserQueryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import otpd.idm.domain.Subject;
import otpd.idm.domain.impl.OtpProfile;
import otpd.idm.domain.impl.SubjectImpl;
import otpd.idm.service.OtpdSessionFactory;
import otpd.idm.service.ServiceException;
import otpd.idm.service.UserService;
import otpd.idm.service.strategy.user.UserCheckerStrategyContext;

public class UserServiceImpl implements UserService {

	final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	private OtpdSessionFactory otpIdentitySessionFactory;
	private String identityIdmRealm;
	private String idAttributeName;

	private IdentitySession identitySession;
	private UserCheckerStrategyContext userStrategyContext;

	public void setUserStrategyContext(
			UserCheckerStrategyContext userStrategyContext) {
		this.userStrategyContext = userStrategyContext;
	}

	public void setIdentityIdmRealm(String identityIdmRealm) {
		this.identityIdmRealm = identityIdmRealm;
	}

	public void setIdAttributeName(String idAttributeName) {
		this.idAttributeName = idAttributeName;
	}

	public void setOtpIdentitySessionFactory(
			OtpdSessionFactory identitySessionFactory) {

		if (logger.isDebugEnabled()) {
			logger.debug("identitySessionFactory : " + identitySessionFactory);
		}

		this.otpIdentitySessionFactory = identitySessionFactory;
	}

	public Subject getUser(Subject subject) throws ServiceException {
		IdentitySession identitySession;
		Subject user = null;

		String id = getUserIdAttribute(subject);

		if (logger.isDebugEnabled()) {
			logger.debug("getUser : " + id);
		}

		try {

			identitySession = getIdentitySession();

			User identityUser = identitySession.getPersistenceManager()
					.findUser(id);

			if (logger.isDebugEnabled()) {
				logger.debug("identityUser : " + identityUser);
			}

			if (identityUser != null) {

				Map<String, Attribute> attributes = identitySession
						.getAttributesManager().getAttributes(identityUser);

				user = copyAttributesToSubject(attributes, identityUser);
			}

		} catch (IdentityException e) {
			throw new ServiceException(e.getMessage());
		}

		return user;
	}

	public List<Subject> getUserByCommonName(String commonName)
			throws ServiceException {

		IdentitySession identitySession;
		User user;
		List usersList = new ArrayList();

		if (logger.isDebugEnabled()) {
			logger.debug("getUserByCommonName : " + commonName);
		}

		try {
			identitySession = getIdentitySession();

			IdentitySearchCriteriaImpl criteria = (IdentitySearchCriteriaImpl) new IdentitySearchCriteriaImpl()
					.nameFilter("*");

			criteria.attributeValuesFilter(idAttribute_COMMONNAME,
					new String[] { commonName });

			UserQuery query = new UserQueryImpl(criteria, null, null, null,
					null);

			Collection<User> identityUsers = identitySession.execute(query);

			for (User identityUser : identityUsers) {

				Map<String, Attribute> attributes = identitySession
						.getAttributesManager().getAttributes(identityUser);

				Subject subject = copyAttributesToSubject(attributes,
						identityUser);
				usersList.add(subject);
			}

		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}

		return usersList;
	}

	public List<Subject> getUsers() throws ServiceException {

		IdentitySession identitySession;
		List usersList = new ArrayList();

		if (logger.isDebugEnabled()) {
			logger.debug("getUsers ");
		}

		try {
			identitySession = getIdentitySession();

			Collection<User> identityUsers = identitySession
					.getPersistenceManager().findUser(
							(IdentitySearchCriteria) null);

			for (User identityUser : identityUsers) {

				Map<String, Attribute> attributes = identitySession
						.getAttributesManager().getAttributes(identityUser);

				Subject subject = copyAttributesToSubject(attributes,
						identityUser);
				usersList.add(subject);
			}

		} catch (IdentityException e) {
			throw new ServiceException(e.getMessage());
		}
		return usersList;
	}

	public void delete(Subject user) throws ServiceException {
		IdentitySession identitySession;

		if (logger.isDebugEnabled()) {
			logger.debug("delete : " + user);
		}

		try {

			identitySession = getIdentitySession();

			identitySession.beginTransaction();

			String id = getUserIdAttribute(user);

			User identityUser = identitySession.getPersistenceManager()
					.findUser(id);

			if (identityUser != null) {
				identitySession.getPersistenceManager().removeUser(
						identityUser, true);
			}

			identitySession.getTransaction().commit();
			identitySession.close();

		} catch (IdentityException e) {
			throw new ServiceException(e.getMessage());
		}

	}

	public Subject saveOrUpdate(Subject subject) throws ServiceException {

		if (logger.isDebugEnabled()) {
			logger.debug("saveOrUpdate : " + subject);
		}

		try {
			User identityUser = IsSubjectInRepository(subject);

			if (identityUser != null) {
				update(subject, identityUser);
			} else {
				save(subject);
			}

		} catch (IdentityException e) {
			throw new ServiceException(e.getMessage());
		} catch (UnsupportedCriterium e) {
			throw new ServiceException(e.getMessage());
		}

		return subject;
	}

	private Subject copyAttributesToSubject(Map<String, Attribute> attributes,
			User identityUser) throws IdentityException {

		Subject user = new SubjectImpl();
		OtpProfile otpProfile = new OtpProfile();

		user.setId(attributes.get("uid").getValue().toString());
		user.setCommonName(attributes.get("cn").getValue().toString());
		user.setFirstName(attributes.get("givenName").getValue().toString());
		user.setLastName(attributes.get("sn").getValue().toString());
		user.setEmail(attributes.get("mail").getValue().toString());

		if (attributes.get("otpkey1").getValue().toString() != null)
			otpProfile.setOtpKey1(attributes.get("otpkey1").getValue()
					.toString());

		if (attributes.get("otpModel").getValue().toString() != null)
			otpProfile.setOtpModel(attributes.get("otpModel").getValue()
					.toString());

		if (attributes.get("otpVendor").getValue().toString() != null)
			otpProfile.setOtpVendor(attributes.get("otpVendor").getValue()
					.toString());

		if (attributes.get("otpSerial").getValue().toString() != null)
			otpProfile.setOtpSerial(attributes.get("otpSerial").getValue()
					.toString());

		if (attributes.get("otpMovingFactor").getValue().toString() != null)
			otpProfile.setOtpMovingFactor(attributes.get("otpMovingFactor")
					.getValue().toString());

		if (attributes.get("otpPIN").getValue().toString() != null)
			otpProfile
					.setOtpPIN(attributes.get("otpPIN").getValue().toString());

		if (attributes.get("otpKeyEncryption").getValue().toString() != null)
			otpProfile.setOtpKeyEncryption(attributes.get("otpKeyEncryption")
					.getValue().toString());

		user.setOtpProfile(otpProfile);

		return user;
	}

	private void save(Subject subject) throws IdentityException {

		IdentitySession identitySession = getIdentitySession();
		identitySession.beginTransaction();

		String id = getUserIdAttribute(subject);

		User identityUser = identitySession.getPersistenceManager().createUser(
				id);

		Attribute[] otpInfo = getOtpAttributes(subject);
		Attribute[] userInfo = getUserAttributes(subject);

		identitySession.getAttributesManager().updateAttributes(identityUser,
				otpInfo);

		identitySession.getAttributesManager().updateAttributes(identityUser,
				userInfo);

		identitySession.getTransaction().commit();
		identitySession.close();
	}

	private void update(Subject subject, User identityUser)
			throws IdentityException {

		IdentitySession identitySession = getIdentitySession();
		identitySession.beginTransaction();

		Attribute[] otpInfo = getOtpAttributes(subject);

		identitySession.getAttributesManager().updateAttributes(identityUser,
				otpInfo);

		identitySession.getTransaction().commit();
		identitySession.close();
	}

	private Attribute[] getOtpAttributes(Subject subject) {

		Attribute[] otpInfoAttributes = new Attribute[] {
				new SimpleAttribute("otpkey1", subject.getOtpProfile()
						.getOtpKey1()),
				new SimpleAttribute("otpModel", subject.getOtpProfile()
						.getOtpModel()),
				new SimpleAttribute("otpVendor", subject.getOtpProfile()
						.getOtpVendor()),
				new SimpleAttribute("otpSerial", subject.getOtpProfile()
						.getOtpSerial()),
				new SimpleAttribute("otpPIN", subject.getOtpProfile()
						.getOtpPIN()),
				new SimpleAttribute("otpKeyEncryption", subject.getOtpProfile()
						.getOtpKeyEncryption()),
				new SimpleAttribute("otpMovingFactor", subject.getOtpProfile()
						.getOtpMovingFactor()) };

		return otpInfoAttributes;
	}

	private Attribute[] getUserAttributes(Subject subject) {

		String checkedId = userStrategyContext.checkedId(subject);
		subject.setId(checkedId);

		Attribute[] userInfoAttributes = new Attribute[] {
				new SimpleAttribute("uid", subject.getId()),
				new SimpleAttribute("cn", subject.getCommonName()),
				new SimpleAttribute("sn", subject.getLastName()),
				new SimpleAttribute("givenName", subject.getFirstName()),
				new SimpleAttribute("mail", subject.getEmail()) };

		return userInfoAttributes;
	}

	private IdentitySession getIdentitySession() throws IdentityException {

		if (identitySession == null)
			identitySession = otpIdentitySessionFactory
					.getIdentitySessionFactory().createIdentitySession(
							identityIdmRealm);
		else {

			identitySession.clear();
		}

		return identitySession;
	}

	private User IsSubjectInRepository(Subject subject)
			throws UnsupportedCriterium, IdentityException {

		User user = null;

		String[] attributeValues = new String[1];
		attributeValues[0] = getUserIdAttribute(subject);

		String idAttribute = (idAttributeName.equalsIgnoreCase(idAttribute_UID)) ? idAttribute_UID
				: idAttribute_COMMONNAME;

		IdentitySearchCriteria criteria = new IdentitySearchCriteriaImpl();
		criteria.attributeValuesFilter(idAttribute, attributeValues);

		IdentitySession identitySession = getIdentitySession();

		Collection<User> users = identitySession.getPersistenceManager()
				.findUser(criteria);

		if (users.size() > 0)
			user = users.iterator().next();

		return user;
	}

	private String getUserIdAttribute(Subject subject) {

		String idAttribute = (idAttributeName.equalsIgnoreCase(idAttribute_UID)) ? idAttribute_UID
				: idAttribute_COMMONNAME;

		String id = null;

		if (idAttribute.equalsIgnoreCase(idAttribute_UID)) {
			subject.setId(userStrategyContext.checkedId(subject));
			id = subject.getId();
		} else
			id = subject.getCommonName();

		return id;
	}
}
