package com.scienjus.Test;

import com.alibaba.fastjson.JSONObject;
import com.scienjus.config.ResultStatus;
import com.scienjus.model.ResultModel;
import com.scienjus.utils.HttpUtils;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
/**
 * 获取和删除token的请求地址，在Restful设计中其实就对应着登录和退出登录的资源映射
 * @author ScienJus
 * @date 2015/7/30.
 */
@RestController
public class SendMsgTest {
    private final static Logger logger = (Logger) LoggerFactory.getLogger(SendMsgTest.class);


    @RequestMapping(value = "sendmsg" ,method = RequestMethod.POST,produces = "application/json; charset=utf-8")
    @ApiOperation(value = "短信发送")
    public ResponseEntity<ResultModel> login(@RequestParam String phoneNum) {

        String host = "https://feginesms.market.alicloudapi.com";
        String path = "/codeNotice";
        String method = "GET";
        String appcode = "5022e749e4514bac907b6c3001adf5c9";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> map = new HashMap<String, String>();
        String verifyCode = String.valueOf(new Random().nextInt(899999) + 100000);//生成短信验证码
        map.put("param", verifyCode);//验证码
        map.put("phone", phoneNum);//电话号码
        map.put("sign", "1");//自定义模板id
        map.put("skin", "1");//短信签名

        try {
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, map);
        } catch (Exception e) {
            logger.info("",e.getMessage(),e);
        }
        return new ResponseEntity<>(ResultModel.error(ResultStatus.TOKEN_OUT_TIME), HttpStatus.OK);
    }

}
