package com.example.scout.socket;

import com.seabreeze.log.Print;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangyuanyuan on 2017/11/1.
 */

public class TcpSocketManager {

    public final static int SEND_PORT = 2005;
    public final static String SEND_ADDRESS = "192.168.11.123";

    private static TcpSocketManager mInstance;

    private ThreadPoolExecutor executorService;

    private TCPTextSendListener mTcpTextSendListener;

    private SendRunnable mSendRunnable;

    public static TcpSocketManager getInstance() {
        if (mInstance == null) {
            synchronized (TcpSocketManager.class) {
                if (mInstance == null)
                    mInstance = new TcpSocketManager();
            }
        }
        return mInstance;
    }

    public synchronized ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(3, Integer.MAX_VALUE, 10, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>());
        }
        return executorService;
    }


    public void startTcp(TCPTextSendListener tcpTextSendListener) {
        mTcpTextSendListener = tcpTextSendListener;
        mSendRunnable = new SendRunnable(SEND_PORT);
        getExecutorService().execute(mSendRunnable);
    }

    public void sendTextMessageByTcp(String msg) {
        mSendRunnable.setData(msg);
    }

    private class SendRunnable extends Thread {


        private int mPort;

        private boolean isSendMsg;
        private StringBuffer buffer;
        private String info;

        public SendRunnable(int port) {
            this.mPort = port;
            buffer = new StringBuffer();
        }

        public void setData(String msg) {
            buffer.append(msg);
            isSendMsg = true;
        }

        @Override
        public void run() {
            try {
                Socket tcpSocket = new Socket(SEND_ADDRESS, mPort);
                OutputStream outputStream = tcpSocket.getOutputStream();
                InputStream inputStream = tcpSocket.getInputStream();
                BufferedReader mInput = new BufferedReader(new InputStreamReader(inputStream));

                while (true) {

                    String tcpMsg = null;
                    if (isSendMsg) {
                        isSendMsg = false;

                        tcpMsg = buffer.toString();
                        buffer.delete(0, buffer.length());
                    }

                    if (tcpMsg != null) {

//                        outputStream.write(tcpMsg.getBytes());
                        byte[] b = hexStringToBytes(tcpMsg);
                        tcpMsg = "ab";
                        Print.e(tcpMsg);
                        outputStream.write(tcpMsg.getBytes());
                        outputStream.flush();

//                        PrintWriter printWriter=new PrintWriter(outputStream);//将输出流包装成打印流
//                                   printWriter.print("服务端你好，我是Balla_兔子");
//                        printWriter.flush();
//                        tcpSocket.shutdownOutput();//关闭输出流


//                        String line = mInput.readLine();
//                        if (mTcpTextSendListener != null)
//                            mTcpTextSendListener.onSuccess(tcpMsg);
//                        inputStream.close();
//                        outputStream.close();
//                    tcpSocket.close();
                    }
                }

//                while (true) {
//                    String temp ;//临时变量
//                    while ((temp = mInput.readLine()) != null) {
//                        info += temp;
//                        System.out.println("客户端接收服务端发送信息：" + info);
//                    }
//                }

            } catch (Exception e) {
                e.printStackTrace();
                if (mTcpTextSendListener != null)
                    mTcpTextSendListener.onFail(e);
            }
        }
    }

    /**
     * TCP发送字符串的监听回调接口
     */
    public interface TCPTextSendListener {
        void onFail(Exception e);

        void onSuccess(String result);
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

}
