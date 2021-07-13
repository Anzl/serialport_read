package com.serialport.until;

import com.serialport.entity.SerialPortParameter;
import gnu.io.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TooManyListenersException;

/**
 * @author 安兆亮
 * @date 2021/6/4 - 19:18
 */
public class SerialPortUtil {
    private static long _TEN_THOUSAND=10000;
    static int count = 0;

    public SerialPortUtil() throws IOException {
    }

    /**
     * 获得系统可用的端口名称列表(COM0、COM1、COM2等等)
     *
     * @return List<String>可用端口名称列表
     */

    @SuppressWarnings("unchecked")
    public static List<String> getSerialPortList() {
        List<String> systemPorts = new ArrayList<>();
        //获得系统可用的端口
        Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
        while (portList.hasMoreElements()) {
            String portName = portList.nextElement().getName();//获得端口的名字
            systemPorts.add(portName);
        }
        return systemPorts;
    }

    /**
     * 打开串口
     *
     * @param serialPortName 串口名称
     * @return SerialPort 串口对象
     * @throws NoSuchPortException               对应串口不存在
     * @throws PortInUseException                串口在使用中
     * @throws UnsupportedCommOperationException 不支持操作操作
     */
    public static SerialPort openSerialPort(String serialPortName)
            throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException {
        SerialPortParameter parameter = new SerialPortParameter(serialPortName);
        return openSerialPort(parameter);
    }

    /**
     * 打开串口
     *
     * @param serialPortName 串口名称
     * @param baudRate       波特率
     * @return SerialPort 串口对象
     * @throws NoSuchPortException               对应串口不存在
     * @throws PortInUseException                串口在使用中
     * @throws UnsupportedCommOperationException 不支持操作操作
     */
    public static SerialPort openSerialPort(String serialPortName, int baudRate)
            throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException {
        SerialPortParameter parameter = new SerialPortParameter(serialPortName, baudRate);
        return openSerialPort(parameter);
    }

    /**
     * 打开串口
     *
     * @param serialPortName 串口名称
     * @param baudRate       波特率
     * @param timeout        串口打开超时时间
     * @return SerialPort 串口对象
     * @throws NoSuchPortException               对应串口不存在
     * @throws PortInUseException                串口在使用中
     * @throws UnsupportedCommOperationException 不支持操作操作
     */
    public static SerialPort openSerialPort(String serialPortName, int baudRate, int timeout)
            throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException {
        SerialPortParameter parameter = new SerialPortParameter(serialPortName, baudRate);
        return openSerialPort(parameter, timeout);
    }

    /**
     * 打开串口
     *
     * @param parameter 串口参数
     * @return SerialPort 串口对象
     * @throws NoSuchPortException               对应串口不存在
     * @throws PortInUseException                串口在使用中
     * @throws UnsupportedCommOperationException 不支持操作操作
     */
    public static SerialPort openSerialPort(SerialPortParameter parameter)
            throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException {
        return openSerialPort(parameter, 2000);
    }

    /**
     * 打开串口
     *
     * @param parameter 串口参数
     * @param timeout   串口打开超时时间
     * @return SerialPort串口对象
     * @throws NoSuchPortException               对应串口不存在
     * @throws PortInUseException                串口在使用中
     * @throws UnsupportedCommOperationException 不支持操作操作
     */
    public static SerialPort openSerialPort(SerialPortParameter parameter, int timeout)
            throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException {
        //通过端口名称得到端口
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(parameter.getSerialPortName());
        //打开端口，（自定义名字，打开超时时间）
        CommPort commPort = portIdentifier.open(parameter.getSerialPortName(), timeout);
        //判断是不是串口
        if (commPort instanceof SerialPort) {
            SerialPort serialPort = (SerialPort) commPort;
            //设置串口参数（波特率，数据位8，停止位1，校验位无）
            serialPort.setSerialPortParams(parameter.getBaudRate(), parameter.getDataBits(), parameter.getStopBits(), parameter.getParity());
            System.out.println("开启串口成功，串口名称：" + parameter.getSerialPortName());
            return serialPort;
        } else {
            //是其他类型的端口
            throw new NoSuchPortException();
        }
    }


    /**
     * 关闭串口
     *
     * @param serialPort 要关闭的串口对象
     */
    public static void closeSerialPort(SerialPort serialPort) {
        if (serialPort != null) {
            serialPort.close();
            System.out.println("关闭了串口：" + serialPort.getName());
        }
    }

    /**
     * 向串口发送数据
     *
     * @param serialPort 串口对象
     * @param data       发送的数据
     */
    public static void sendData(SerialPort serialPort, byte[] data) {
        OutputStream os = null;
        try {
            //获得串口的输出流
            os = serialPort.getOutputStream();
            os.write(data);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从串口读取数据
     *
     * @param serialPort 要读取的串口
     * @return 读取的数据
     */
    public static byte[] readData(SerialPort serialPort) {
        InputStream is = null;
        byte[] bytes = null;
        try {
            //获得串口的输入流
            is = serialPort.getInputStream();
            //获得数据长度
            int bufflenth = is.available();
            while (bufflenth != 0) {
                //初始化byte数组
                bytes = new byte[bufflenth];
                is.read(bytes);
                bufflenth = is.available();
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }

    /*
    * 向文件写入数据
    * */

    public static void outputFile(File f, String filename, String str,boolean b,boolean c){
        //判断文件夹是否存在
        if(!f.exists())
           f.mkdirs();
        //创建文件
        File f1=new File(f,filename);
        try {
            f1.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //获得文件输出流
        FileOutputStream fos=null;
        try {
            fos=new FileOutputStream(f1,false);
            fos.write((str+System.getProperty("line.separator")).getBytes());
            Thread.sleep(1500);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(fos!=null)
                fos.close();
            }catch (Exception e){
                throw new RuntimeException("发生严重错误·");
            }

        }

}


    /**
     * 给串口设置监听
     *
     * @param serialPort serialPort 要读取的串口
     * @param listener   SerialPortEventListener监听对象
     * @throws TooManyListenersException 监听对象太多
     */
    public static void setListenerToSerialPort(SerialPort serialPort, SerialPortEventListener listener) throws TooManyListenersException {
        //给串口添加事件监听
        serialPort.addEventListener(listener);
        //串口有数据监听
        serialPort.notifyOnDataAvailable(true);
        //中断事件监听
        serialPort.notifyOnBreakInterrupt(true);
    }

     public static void testSystem(long times){//use 188
        for(int i=0;i<times;i++){
            long currentTime=System.currentTimeMillis();
        }
    }
}
