package org.zapodot.jackson.java8;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class JavaOptionalModuleTest {

    public static class Bean {

        public static final String PRESENT_VALUE = "present";
        @JsonProperty
        private Optional<String> empty = Optional.empty();

        @JsonProperty
        private Optional<String> notSet;

        @JsonProperty
        private Optional<String> present = Optional.of(PRESENT_VALUE);


    }

    @Test
    public void testAutoDetect() throws Exception {
        assertEquals(1L, ObjectMapper.findModules().stream().filter(m -> m instanceof JavaOptionalModule).count());

    }

    @Test
    public void testRegisterManually() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaOptionalModule());
        final Bean bean = objectMapper
                .readValue("{\"empty\":null,\"notSet\":null}", Bean.class);
        assertNotNull(bean.empty);
        assertEquals(Optional.empty(), bean.empty);
        assertNotNull(bean.notSet);
        assertEquals(Optional.empty(), bean.notSet);
        assertEquals(Optional.of(Bean.PRESENT_VALUE), bean.present);

    }

    @Test
    public void testSerialize() throws Exception {
        final Bean bean = new Bean();
        final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        final String jsonText = mapper.writeValueAsString(bean);
        final JsonNode node = mapper.readTree(jsonText);
        assertTrue(node.get("empty").isNull());
        assertTrue(node.get("notSet").isNull());
        assertEquals(Bean.PRESENT_VALUE, node.get("present").asText());
    }

    @Test
    public void testSerializeNonNull() throws Exception {
        final Bean bean = new Bean();
        final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaOptionalModule());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        final String jsonText = objectMapper.writeValueAsString(bean);
        final JsonNode node = objectMapper.readTree(jsonText);
        assertNull(node.get("empty"));
        assertNull(node.get("notSet"));
        assertEquals(Bean.PRESENT_VALUE, node.get("present").asText());

    }

    @Test
    public void testDeserialize() throws Exception {
        final Bean bean = new ObjectMapper().findAndRegisterModules()
                                            .readValue("{\"empty\":null,\"notSet\":null}", Bean.class);
        assertNotNull(bean.empty);
        assertEquals(Optional.empty(), bean.empty);
        assertNotNull(bean.notSet);
        assertEquals(Optional.empty(), bean.notSet);
        assertEquals(Optional.of(Bean.PRESENT_VALUE), bean.present);

    }

    @Test
    public void testSerializeIgnoreNull() throws Exception {
        final Bean originalBean = new Bean();
        final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        final String json = objectMapper.writeValueAsString(originalBean);
        assertEquals("{\"present\":\"present\"}", json);
    }
}