package com.tjxjnoobie.website;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminLoginIntegrationTest extends WebsiteIntegrationTestSupport {

    @Test
    void loginPageRendersCsrfBackedLocalAdminForm() throws Exception {
        mockMvc.perform(get("/admin/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("action=\"/admin/login\"")))
                .andExpect(content().string(containsString("name=\"_csrf\"")));
    }

    @Test
    void localAdminLoginRequiresCsrfAndSucceedsWhenTokenIsPresent() throws Exception {
        mockMvc.perform(post("/admin/login")
                        .param("username", "storeadmin")
                        .param("password", "StoreAdmin!123"))
                .andExpect(status().isForbidden());

        MockHttpSession session = (MockHttpSession) mockMvc.perform(post("/admin/login")
                        .with(csrf())
                        .param("username", "storeadmin")
                        .param("password", "StoreAdmin!123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"))
                .andExpect(authenticated().withUsername("storeadmin"))
                .andReturn()
                .getRequest()
                .getSession(false);

        mockMvc.perform(get("/admin").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Financial Overview")));
    }
}
