package com.serialport.controller;

import com.serialport.until.SerialPortUtil;
import gnu.io.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.TooManyListenersException;

/**
 * @author 安兆亮
 * @date 2021/6/4 - 19:19
 */
@Controller
public class SerialPortUtilTest {
    private String sendStr;

    public String getSendStr() {
        return sendStr;
    }

    public void setSendStr(String sendStr) {
        this.sendStr = sendStr;
    }

    /**
     * 测试获取串口列表
     */
    @Autowired
    public void getSystemPortList() {

        List<String> portList = SerialPortUtil.getSerialPortList();
        System.out.println(portList);

    }

    /**
     * 测试串口打开，读，写操作
     */
    @Autowired
    public void serialPortAction() {
        try {
            final SerialPort serialPort = SerialPortUtil.openSerialPort("COM3", 115200);
            //设置串口的listener
            SerialPortUtil.setListenerToSerialPort(serialPort, event -> {
                //数据通知
                String t0;
                File f = new File("E:\\Vue\\vue-navigation\\public");
                if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                    byte[] bytes = SerialPortUtil.readData(serialPort);

                    //Data变量保存读取到的数据
                    String Data = new String(bytes);
                    //将[]里的字符串，即坐标存到str中
//                    String str = Data.substring(94, 103);
                    //发送给后端的数据
//                    sendStr = Data.substring(70, 109);
                    //写入t0与标签的距离到文件
//                    t0 = Data.substring(70, 74);
//                    coordinate("post",sendStr);
//                    SerialPortUtil.outputFile(f, "coordinates.txt", str + "," + t0, true, true);
                    System.out.println("写入成功");
                    System.out.println("收到的数据长度：" + bytes.length);
                    System.out.println("收到的数据：" + Data);

                }
            });
        } catch (NoSuchPortException | PortInUseException | UnsupportedCommOperationException | TooManyListenersException e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用微服务
     */
    // HttpURLConnection 方式调用Restful接口
    // 调用接口
    @RequestMapping(value = "local/{param}")
    public @ResponseBody
    String coordinate(@PathVariable String param, String str) {
        try {
            String url = "http://10.101.1.116:8083/nodeList/local";
            URL restServiceURL = new URL(url);
            HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL
                    .openConnection();
            //param 输入小写，转换成 GET POST DELETE PUT
            httpConnection.setRequestMethod(param.toUpperCase());
//            httpConnection.setRequestProperty("Accept", "application/json");
            if ("post".equals(param)) {
                //打开输出开关
                httpConnection.setDoOutput(true);
                httpConnection.setDoInput(true);

                //传递参数
                String input = "&urlString=" + URLEncoder.encode(str, "UTF-8");
//                input+="&name="+ URLEncoder.encode("啊啊啊", "UTF-8");
                OutputStream outputStream = httpConnection.getOutputStream();
                outputStream.write(input.getBytes());
                outputStream.flush();
            }
            if (httpConnection.getResponseCode() != 200) {
                throw new RuntimeException(
                        "HTTP GET Request Failed with Error code : "
                                + httpConnection.getResponseCode());
            }
            BufferedReader responseBuffer = new BufferedReader(
                    new InputStreamReader((httpConnection.getInputStream())));
            String output;
            System.out.println("Output from Server:  \n");
            while ((output = responseBuffer.readLine()) != null) {
                System.out.println(output);
            }
            httpConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("传输成功");
        return "success";
    }
}
