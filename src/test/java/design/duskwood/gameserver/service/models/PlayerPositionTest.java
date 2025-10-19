package design.duskwood.gameserver.service.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerPositionTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testPlayerPositionSerializationWithLastSeq() throws Exception {
        PlayerPosition position = new PlayerPosition(10.5, 20.3, 0.1, 0.2, 42);
        String json = objectMapper.writeValueAsString(position);
        
        assertTrue(json.contains("\"x\":10.5"));
        assertTrue(json.contains("\"y\":20.3"));
        assertTrue(json.contains("\"lastSeq\":42"));
    }

    @Test
    void testPlayerPositionDeserialization() throws Exception {
        String json = "{\"x\":10.5,\"y\":20.3,\"vx\":0.1,\"vy\":0.2,\"lastSeq\":42}";
        PlayerPosition position = objectMapper.readValue(json, PlayerPosition.class);
        
        assertEquals(10.5, position.getX());
        assertEquals(20.3, position.getY());
        assertEquals(0.1, position.getVx());
        assertEquals(0.2, position.getVy());
        assertEquals(42, position.getLastSeq());
    }
}
