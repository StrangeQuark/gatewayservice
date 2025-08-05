// Integration file: Auth
package com.strangequark.gatewayservice.utilitytests;

import com.strangequark.gatewayservice.utility.AuthUtility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.ResourceAccessException;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class AuthUtilityTest {

    @Autowired
    AuthUtility authUtility;

    @Test
    void requestNewAccessTokenTest() {
        Assertions.assertThrows(ResourceAccessException.class, () -> authUtility.requestNewAccessToken(""));
    }
}
