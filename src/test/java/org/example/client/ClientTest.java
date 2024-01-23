package org.example.client;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    @Test
    void test_new_client_server_not_running_exception_thrown() {
        assertThrows(RuntimeException.class, () -> {
            provideInput("");
            new Client();
        });
    }

    void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

}