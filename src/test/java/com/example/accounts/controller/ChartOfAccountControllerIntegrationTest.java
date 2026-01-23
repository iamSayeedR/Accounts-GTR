package com.example.accounts.controller;

import com.example.accounts.accounts_service.AccountsServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = AccountsServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ChartOfAccountControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Test
        public void testChartOfAccountsDimensions() throws Exception {
                // Check Non-Current Assets (1010000)
                mockMvc.perform(get("/api/chart-of-accounts/code/1010000")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        public void testGetDescriptionsByParentGroup() throws Exception {
                mockMvc.perform(get("/api/chart-of-accounts/descriptions/parent-group/Current Assets")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        public void testGetParentGroupDropdownOptions() throws Exception {
                // Fetch and Print Result
                String result = mockMvc.perform(get("/api/chart-of-accounts/dropdown/parent-groups")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                System.out.println("\n\n✅ [PARENT GROUP DROPDOWN OUTPUT]:\n" + result + "\n\n");
        }

        @Test
        public void testGetSectionDropdownOptions() throws Exception {
                // Fetch and Print Result
                String result = mockMvc.perform(get("/api/chart-of-accounts/dropdown/sections")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                System.out.println("\n\n✅ [SECTION DROPDOWN OUTPUT]:\n" + result + "\n\n");
        }
}
