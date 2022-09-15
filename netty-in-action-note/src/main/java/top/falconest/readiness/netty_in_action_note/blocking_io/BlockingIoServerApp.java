package top.falconest.readiness.netty_in_action_note.blocking_io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;


public class BlockingIoServerApp {
  /**
   * 单线程阻塞IO服务端 <br>
   * 阻塞方法：{@link ServerSocket#accept()}，{@link BufferedReader#readLine()}
   */
  public void serve(int portNumber) throws IOException {
    // 创建一个新的ServerSocket，用以监听指定端口上的连接请求
    ServerSocket serverSocket = new ServerSocket(portNumber);
    // 对accept()方法的调用将被阻塞，直到一个连接建立
    Socket clientSocket = serverSocket.accept();
    System.out.println("accept");
    // in和out两个流对象都派生于套接字clientSocket的流对象
    BufferedReader in = new BufferedReader(
        new InputStreamReader(clientSocket.getInputStream())
    );
    PrintWriter out =
        new PrintWriter(clientSocket.getOutputStream(), true);
    String request, response;
    // 单线程处理，readLine会阻塞直到读取到换行符。
    while ((request = in.readLine()) != null) {
      System.out.println("request: " + request);
      if ("Done".equals(request)) {
        break;
      }
      response = processRequest(request);
      out.println(response);
    }
  }

  public void serveAsync(int portNumber) throws IOException {
    ServerSocket serverSocket = new ServerSocket(portNumber);
    while (true) {
      final Socket clientSocket = serverSocket.accept();
      System.out.println("accept");
      CompletableFuture.runAsync(() -> {
        try {
          BufferedReader in = new BufferedReader(
              new InputStreamReader(clientSocket.getInputStream())
          );
          PrintWriter out =
              new PrintWriter(clientSocket.getOutputStream(), true);
          String request, response;
          // 单线程处理，readLine会阻塞直到读取到换行符。
          while ((request = in.readLine()) != null) {
            System.out.println("request: " + request);
            if ("Done".equals(request)) {
              break;
            }
            response = processRequest(request);
            out.println(response);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      });
    }


  }

  private String processRequest(String request) {
    return request + ": Processed.";
  }

  static class Configuration {
    public static String host = "127.0.0.1";
    public static int port = 8080;
  }

  // todo
  static void run() throws IOException {
//    new BlockingIoServerApp().serve(Configuration.port);
    new BlockingIoServerApp().serveAsync(Configuration.port);
  }

  public static void main(String[] args) throws IOException {
    run();
  }
}
