package com.serialport;

import com.serialport.until.SerialPortUtil;
import gnu.io.*;
import org.junit.Test;

import java.util.List;
import java.util.TooManyListenersException;

/**
 * @author 安兆亮
 * @date 2021/6/4 - 19:19
 */
public class SerialPortUtilTest {
    /**
     * 测试获取串口列表
     */
    @Test
    public void getSystemPortList() {

        List<String> portList = SerialPortUtil.getSerialPortList();
        System.out.println(portList);

    }

    /**
     * 测试串口打开，读，写操作
     */
    @Test
    public void serialPortAction() {
        try {
            final SerialPort serialPort = SerialPortUtil.openSerialPort("COM3", 115200);
            //启动一个线程每2s向串口发送数据，发送1000次hello
            new Thread(() -> {
                while (true) {
                    String s = "hello";
                    byte[] bytes = s.getBytes();
                    SerialPortUtil.sendData(serialPort, bytes);//发送数据
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            //设置串口的listener
            SerialPortUtil.setListenerToSerialPort(serialPort, event -> {
                //数据通知
                String cellxy;
                if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
//                    byte[] bytes = SerialPortUtil.readData(serialPort);
//                    //Data变量保存读取到的数据
//                    String Data =new String(bytes);
//                    //将第一个逗号后的字符串，即坐标存到str中，后期调用微服务将str传过去
//                    String str = Data.substring(0,Data.indexOf(","));
//                    cellxy =Data.substring(str.length()+1, Data.length());
//                    System.out.println("收到的数据长度：" + bytes.length);
//                    System.out.println("收到的数据：" + Data);
//                    System.out.println(cellxy);
                }
            });
            try {
                // sleep 一段时间保证线程可以执行完
                Thread.sleep(3 * 30 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (NoSuchPortException | PortInUseException | UnsupportedCommOperationException | TooManyListenersException e) {
            e.printStackTrace();
        }
    }
}
