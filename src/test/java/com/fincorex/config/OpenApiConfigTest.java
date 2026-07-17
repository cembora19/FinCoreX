package com.fincorex.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OpenApiConfigTest {

    @Test
    void shouldExposeJwtBearerSecurityScheme() {
        var openApi = new OpenApiConfig().finCoreXOpenAPI();
        var scheme = openApi.getComponents().getSecuritySchemes().get("bearerAuth");

        assertNotNull(scheme);
        assertEquals("http", scheme.getType().toString().toLowerCase());
        assertEquals("bearer", scheme.getScheme());
        assertEquals("JWT", scheme.getBearerFormat());
        assertEquals(true, openApi.getSecurity().getFirst().containsKey("bearerAuth"));
    }
}
