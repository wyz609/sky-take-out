package com.sky.controller.notify;

/**
 * Class name: PayNotifyController
 * Package: com.sky.controller.notify
 * Description:
 *
 * @Create: 2025/5/2 16:17
 * @Author: jay
 * @Version: 1.0
 */

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.properties.WeChatProperties;
import com.sky.service.OrderService;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HashMap;

import static org.apache.poi.util.HexRead.readData;

/**
 * 支付回调相关接口
 */
@RestController
@RequestMapping("/notify")
@Slf4j
public class PayNotifyController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private WeChatProperties weChatProperties;

    /**
     * 支付成功回调
     * @param request 请求
     * @param response 响应
     * @throws Exception 异常
     */
    public void paySuccessNotify(HttpServletRequest request, HttpServletResponse response)throws Exception{
        // 读取请求中的数据
        String body = readData(request);
        // 记录日志
        log.info("支付成功回调，参数：{}",body);

        // 数据加密
        String plainText = decryptData(body);
        log.info("支付成功回调，解密后的参数：{}",plainText);

        JSONObject jsonObject = JSON.parseObject(plainText);
        String outTradeNo = jsonObject.getString("out_trade_no"); // 商户平台订单号
        String transactionId = jsonObject.getString("transaction_id");// 微信支付订单号

        log.info("商户平台订单号:" + outTradeNo);
        log.info("微信支付订单号:" + transactionId);

        orderService.paySuccess(outTradeNo);

        responseToWeixin(response);

    }

// 响应微信请求
    private void responseToWeixin(HttpServletResponse response) throws IOException {
        // 设置响应状态码为200
        response.setStatus(200);
        // 创建一个HashMap对象
        HashMap<Object,Object> map = new HashMap<>();
        // 向HashMap中添加键值对
        map.put("code","SUCCESS");
        map.put("message","SUCCESS");
        // 设置响应头Content-Type为application/json
        response.setHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
        // 将HashMap对象转换为JSON字符串，并写入响应输出流
        response.getOutputStream().write(JSONUtils.toJSONString(map).getBytes(StandardCharsets.UTF_8));
        // 刷新响应输出流
        response.flushBuffer();
    }

// 解密数据
    private String decryptData(String body) throws GeneralSecurityException {
    // TODO: 实现解密数据的逻辑
        // 将body转换为JSONObject对象
        JSONObject resultObject = JSON.parseObject(body);
        // 获取resource对象
        JSONObject resource = resultObject.getJSONObject("resource");
        // 获取ciphertext
        String ciphertext = resource.getString("ciphertext");
        // 获取associated_data
        String associatedData = resource.getString("associated_data");
        // 获取nonce
        String nonce = resource.getString("nonce");

        // 解密数据
        // 创建AesUtil对象，传入apiV3Key
        AesUtil aesUtil = new AesUtil(weChatProperties.getApiV3Key().getBytes(StandardCharsets.UTF_8));
        // 解密数据，传入associated_data、nonce、ciphertext
        String plainText = aesUtil.decryptToString(associatedData.getBytes(StandardCharsets.UTF_8), nonce.getBytes(StandardCharsets.UTF_8), ciphertext);

        return plainText;
    }

    /**
     * 读取请求中的数据
     * @param request 请求
     * @return 数据
     * @throws Exception 异常
     */
    private String readData(HttpServletRequest request)throws Exception{

        // 获取请求中的数据流
        BufferedReader reader = request.getReader();
        // 创建一个StringBuilder对象，用于存储读取的数据
        StringBuilder stringBuilder = new StringBuilder();
        // 定义一个字符串变量，用于存储每一行的数据
        String line = null;

        // 循环读取每一行的数据
        while((line = reader.readLine()) != null){
            // 如果StringBuilder对象中已经有数据，则在每一行数据前添加一个换行符
            if(stringBuilder.length() > 0) {
                stringBuilder.append("\n");
            }
            // 将每一行的数据添加到StringBuilder对象中
            stringBuilder.append(line);
        }
        // 返回读取的数据
    return stringBuilder.toString();
    }


}

