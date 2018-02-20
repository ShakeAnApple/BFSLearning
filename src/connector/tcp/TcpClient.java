package connector.tcp;

import java.io.*;
import java.net.Socket;

/**
 * Created by Eskimos on 16.01.2018.
 */
public class TcpClient implements Closeable {
    private final BufferedReader _input;
    private final DataOutputStream _output;
    private final Socket _socket;

    public TcpClient(String host, int port) throws IOException {
        _socket = new Socket(host, port);
        _input = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
        _output = new DataOutputStream(_socket.getOutputStream());
    }

    public void write(String ... strings) throws IOException {
        for (Object obj : strings) {
            _output.writeChars(obj.toString() + ";");
        }
        _output.flush();
    }

    public String readLine() throws IOException {
        return _input.readLine();
    }

    @Override
    public void close() throws IOException {
        _output.close();
        _input.close();
        _socket.close();
    }
}
