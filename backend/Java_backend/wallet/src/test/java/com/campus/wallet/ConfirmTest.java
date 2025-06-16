package com.campus.wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ConfirmTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "U87654321", roles = {"USER"})
    void testconfirm() throws Exception {
        String requestBody = "{\"orderID\": \"0188e00d-5c3c-7f4b-8e0d-5c3c7f4b8e16\"}";

        mockMvc.perform(post("/user/account/confirmReceipt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }
}
