package com.example.scout.socket;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Jason Zhu on 2017-04-25.
 * Email: cloud_happy@163.com
 */

public class TcpClient implements Runnable {

    private TCPTextSendListener mTcpTextSendListener;

    private String serverIP = "192.168.11.123";
    private int serverPort = 2001;
    private PrintWriter pw;
    private InputStream is;
    private DataInputStream dis;
    private boolean isRun = true;
    private Socket socket = null;
    byte buff[] = new byte[4096];
    private String rcvMsg;
    private int rcvLen;


    public TcpClient(String ip, int port, TCPTextSendListener listener) {
        this.serverIP = ip;
        this.serverPort = port;
        this.mTcpTextSendListener = listener;
    }

    public void closeSelf() {
        isRun = false;
    }

    public void send(String msg) {
        pw.print(msg);
        pw.flush();

    }

    @Override
    public void run() {
        try {
            socket = new Socket(serverIP, serverPort);
            // 客户端socket在接收数据时，有两种超时：1. 连接服务器超时，即连接超时；2. 连接服务器成功后，接收服务器数据超时，即接收超时
            // 设置 socket 读取数据流的超时时间
            socket.setSoTimeout(5000);
            // 发送数据包，默认为 false，即客户端发送数据采用 Nagle 算法；
            // 但是对于实时交互性高的程序，建议其改为 true，即关闭 Nagle 算法，客户端每发送一次数据，无论数据包大小都会将这些数据发送出去
//            socket.setTcpNoDelay(true);
            // 设置客户端 socket 关闭时，close() 方法起作用时延迟 30 秒关闭，如果 30 秒内尽量将未发送的数据包发送出去
//            socket.setSoLinger(true, 30);
            // 设置输出流的发送缓冲区大小，默认是4KB，即4096字节
//            socket.setSendBufferSize(4096);
            // 设置输入流的接收缓冲区大小，默认是4KB，即4096字节
//            socket.setReceiveBufferSize(4096);
            // 作用：每隔一段时间检查服务器是否处于活动状态，如果服务器端长时间没响应，自动关闭客户端socket
            // 防止服务器端无效时，客户端长时间处于连接状态
//            socket.setKeepAlive(true);
            // 客户端向服务器端发送数据，获取客户端向服务器端输出流
            pw = new PrintWriter(socket.getOutputStream(), true);

            // 代表可以立即向服务器端发送单字节数据
//            socket.setOOBInline(true);
            // 数据不经过输出缓冲区，立即发送
//            socket.sendUrgentData(0x44);//"D"

            is = socket.getInputStream();
            dis = new DataInputStream(is);

        } catch (IOException e) {
            e.printStackTrace();
            if (mTcpTextSendListener != null) {
                mTcpTextSendListener.onOtherFail(e);
            }
        }
        while (isRun) {
            try {

                rcvLen = is.read(buff);
                rcvMsg = new String(buff, 0, rcvLen, "utf-8");

                if (mTcpTextSendListener != null) {
                    mTcpTextSendListener.onReadSuccess(rcvMsg);
                }
            } catch (IOException e) {
                if (mTcpTextSendListener != null) {
                    mTcpTextSendListener.onReadFail(e);
                }
            }

        }
        try {
            pw.close();
            is.close();
            dis.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            if (mTcpTextSendListener != null) {
                mTcpTextSendListener.onOtherFail(e);
            }
        }
    }


}
