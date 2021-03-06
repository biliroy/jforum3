/*
 * Copyright (c) JForum Team. All rights reserved.
 *
 * The software in this package is published under the terms of the LGPL
 * license a copy of which has been included with this distribution in the
 * license.txt file.
 *
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.jforum.entities.Group;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import br.com.caelum.vraptor.Result;


/**
 * @author Rafael Steil
 */
public class GroupInteractionFilterTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private UserSession userSession = context.mock(UserSession.class);
	private Result mockResult = context.mock(Result.class);

	@Test
	public void filterForumListing() {
		context.checking(new Expectations() {{
			User u1 = new User(); User u2 = new User(); User u3 = new User();

			Group g1 = new Group(); g1.setId(1);
			Group g2 = new Group(); g2.setId(2);
			Group g3 = new Group(); g3.setId(3);

			u1.addGroup(g1);
			u2.addGroup(g1); u2.addGroup(g2);
			u3.addGroup(g3);

			final UserSession us1 = new UserSession(null); us1.setSessionId("1"); us1.setUser(u1);
			final UserSession us2 = new UserSession(null); us2.setSessionId("2"); us2.setUser(u2);
			final UserSession us3 = new UserSession(null); us3.setSessionId("3"); us3.setUser(u3);

			one(userSession).getUser(); will(returnValue(u1));

			Map<String, List<UserSession>> m = new HashMap<String, List<UserSession>>();
			m.put("onlineUsers", Arrays.asList(us1, us2, us3));
			one(mockResult).included(); will(returnValue(m));

			one(mockResult).include("totalLoggedUsers", 2);
			one(mockResult).include("onlineUsers", new HashSet<UserSession>(Arrays.asList(us1, us2)));
		}});

		GroupInteractionFilter filter = new GroupInteractionFilter();
		filter.filterForumListing(mockResult, userSession);
		context.assertIsSatisfied();
	}
}