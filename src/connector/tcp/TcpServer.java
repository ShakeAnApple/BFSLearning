package connector.tcp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Eskimos on 16.01.2018.
 */
public class TcpServer implements Closeable {
    private final ServerSocket _serverSocket;
    private final Socket _socket;
    private final BufferedReader _input;
    private final PrintWriter _output;

    public TcpServer(int port) throws IOException {
        _serverSocket = new ServerSocket(port);
        _socket = _serverSocket.accept();
        _input = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
        _output = new PrintWriter(_socket.getOutputStream());
        _output.flush();
    }

    public void write(String ... strings) throws IOException {
        StringBuilder res = new StringBuilder();
        for (String str : strings) {
            res.append(str).append(";");
        }
        _output.println(res.toString());
        _output.flush();
    }

    public void writeLine(String s){
        _output.println(s);
        _output.flush();
    }

    public boolean canRead() throws IOException {
        return _input.ready();
    }

    public String readLine() throws IOException {
        return _input.readLine();
    }

    @Override
    public void close() throws IOException {
        _output.close();
        _input.close();
        _socket.close();
        _serverSocket.close();
    }
}