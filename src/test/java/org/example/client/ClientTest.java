package org.example.client;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    @Test
    void test_new_client_server_not_running_exception_thrown() {
        assertThrows(IOException.class, () -> {
            new Client(new Socket("localhost", 12345), "Username");
        });
    }

}