package design.duskwood.gameserver.service.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerInputTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testPlayerInputDeserializationWithSeq() throws Exception {
        String json = "{\"up\":true,\"down\":false,\"left\":true,\"right\":false,\"seq\":42}";
        PlayerInput input = objectMapper.readValue(json, PlayerInput.class);
        
        assertTrue(input.up());
        assertFalse(input.down());
        assertTrue(input.left());
        assertFalse(input.right());
        assertEquals(42, input.seq());
    }

    @Test
    void testPlayerInputSerialization() throws Exception {
        PlayerInput input = new PlayerInput(true, false, true, false, 42);
        String json = objectMapper.writeValueAsString(input);
        
        assertTrue(json.contains("\"seq\":42"));
        assertTrue(json.contains("\"up\":true"));
        assertTrue(json.contains("\"down\":false"));
    }
}
