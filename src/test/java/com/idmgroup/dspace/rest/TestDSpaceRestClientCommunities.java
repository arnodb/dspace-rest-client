package com.idmgroup.dspace.rest;

import static com.idmgroup.dspace.rest.jersey.JerseyTestUtils.user;
import static com.idmgroup.dspace.rest.TestConstants.DEMO_DSPACE_ADMIN;
import static com.idmgroup.dspace.rest.TestConstants.DEMO_DSPACE_PASSWORD;
import static com.idmgroup.dspace.rest.TestConstants.DEMO_DSPACE_URL;
import static com.idmgroup.dspace.rest.TestConstants.TEST_COMMUNITY_NAME;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.matchers.Matches;
import org.springframework.web.client.RestTemplate;

import com.idmgroup.dspace.rest.jersey.Community;
import com.idmgroup.dspace.rest.jersey.User;

public class TestDSpaceRestClientCommunities {

    private void clean() {
        DSpaceRestClient client = newClient(DEMO_DSPACE_URL);
        client.login(user(DEMO_DSPACE_ADMIN, DEMO_DSPACE_PASSWORD));
        cleanCommunitiesByName(client, TEST_COMMUNITY_NAME);
    }

    private void cleanCommunitiesByName(DSpaceRestClient client, String communityName) {
        int offset = 0;
        while (true) {
            Community[] slice = client.getCommunities(null, 20, offset, null, null, null);
            if (slice != null && slice.length > 0) {
                for (Community com : slice) {
                    if (communityName.equals(com.getName())) {
                        client.deleteCommunity(com.getId(), null, null, null);
                    }
                }
            } else {
                break;
            }
            offset += 20;
        }
    }

    private DSpaceRestClient newClient(String url) {
        RestTemplate restTemplate = new RestTemplate();
        return new DSpaceRestClient(url, restTemplate);
    }

    @Before
    public void setUp() {
        clean();
    }

    @After
    public void tearDown() {
        clean();
    }

    @Test
    public void testCreateCommunity() {
        DSpaceRestClient client = newClient(DEMO_DSPACE_URL);
        client.login(user(DEMO_DSPACE_ADMIN, DEMO_DSPACE_PASSWORD));
        try {
            Community community = new Community();
            community.setName(TEST_COMMUNITY_NAME);
            Community result = client.createCommunity(null, null, null, community);
            assertNotNull("created community", result);
            assertNotNull("created community ID", result.getId());
            assertTrue("created community ID > 0", result.getId() > 0);
            assertThat("created community handle", result.getHandle(), new Matches("[0-9]+/[0-9]+"));
        } finally {
            client.logout();
        }
    }

}