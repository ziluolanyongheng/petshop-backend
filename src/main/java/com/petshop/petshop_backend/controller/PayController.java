package com.petshop.petshop_backend.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.mybatisflex.core.query.QueryWrapper;
import com.petshop.petshop_backend.config.AlipayConfig;
import com.petshop.petshop_backend.entity.PetOrder;
import com.petshop.petshop_backend.entity.WsPushMessages;
import com.petshop.petshop_backend.entity.base.Result;
import com.petshop.petshop_backend.service.PetOrderService;
import com.petshop.petshop_backend.service.WsPushMessagesService;
import com.petshop.petshop_backend.utils.EncryptUtil;
import com.petshop.petshop_backend.utils.EncryptorUtil;
import com.petshop.petshop_backend.utils.QRCodeUtil;
import com.petshop.petshop_backend.websocket.WebSocketServer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//支付接口
@RestController
@RequestMapping("/pay")
@Tag(name = "8 支付宝沙箱支付接口")
public class PayController {
    @Value("${pay.alipay.APP_ID}")
    String APP_ID;
    @Value("${pay.alipay.APP_PRIVATE_KEY}")
    String APP_PRIVATE_KEY;
    @Value("${pay.alipay.ALIPAY_PUBLIC_KEY}")
    String ALIPAY_PUBLIC_KEY;

    @Value("${pay.server_address}")
    String SERVER_ADDRESS;

    @Value("${pay.secretKey}")
    String SECRET_KEY;


    @Autowired
    private PetOrderService petOrderService;

    @Autowired
    private WsPushMessagesService wsPushMessagesService;

    @Autowired
    private WebSocketServer webSocketServer;

    @GetMapping("/qrcode")
    @ApiOperationSupport(order = 10)
    @Operation(summary = "根据订单号生成订单支付二维码")
    public Result generatePayQRCode(@RequestParam String orderNo) throws Exception {

        EncryptorUtil encryptor = new EncryptorUtil(SECRET_KEY);

        //加密的订单号
        String encryptedOrderNo = encryptor.encrypt(orderNo);

        QRCodeUtil qrCodeUtil = new QRCodeUtil();
        String qrCode = qrCodeUtil.createQRCode(SERVER_ADDRESS + "/pay/alipay?orderNo="+encryptedOrderNo, 200, 200);
//        String qrCode = qrCodeUtil.createQRCode(SERVER_ADDRESS + "/pay/alipay", 200, 200);

        String decrypt = encryptor.decrypt(encryptedOrderNo);
        return Result.success(qrCode);
    }

    @GetMapping("/alipay")
    @ApiOperationSupport(order = 11)
    @Operation(summary = "二维码支付接收的接口")
    public void doPost(HttpServletRequest httpRequest,
                       HttpServletResponse httpResponse) throws Exception {
        // 从 URL 查询参数获取 orderNo
        String encodeOrderNo = httpRequest.getParameter("orderNo");
        EncryptorUtil encryptor = new EncryptorUtil(SECRET_KEY);
        String orderNo = encryptor.decrypt(encodeOrderNo);


        QueryWrapper queryWrapper = new QueryWrapper().eq(PetOrder::getOrderNo, orderNo);
        PetOrder petOrder = petOrderService.getOne(queryWrapper);
        String title = "宠物商城订单支付"+orderNo;

        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.URL, APP_ID, APP_PRIVATE_KEY, AlipayConfig.FORMAT, AlipayConfig.CHARSET, ALIPAY_PUBLIC_KEY,AlipayConfig.SIGNTYPE);
        //获得初始化的AlipayClient
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
        //alipayRequest.setReturnUrl("http://domain.com/CallBack/return_url.jsp");
        alipayRequest.setNotifyUrl(SERVER_ADDRESS+"/pay/paynotify");//在公共参数中设置回跳和通知地址
        alipayRequest.setBizContent("{" +
                "    \"out_trade_no\":\""+orderNo+"\"," +
                "    \"total_amount\":\""+petOrder.getOrderTotal()+"\"," +
                "    \"subject\":\""+title+"\"," +
                "    \"product_code\":\"QUICK_WAP_WAY\"" +
                "  }");//填充业务参数
        String form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
        httpResponse.setContentType("text/html;charset=" + AlipayConfig.CHARSET);
        httpResponse.getWriter().write(form);//直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();

    }

    @PostMapping("/paynotify")
    @ApiOperationSupport(order = 12)
    @Operation(summary = "回调接口")
    public void paynotify(HttpServletRequest request,HttpServletResponse response) throws IOException, AlipayApiException {
        Map<String,String> params = new HashMap<String,String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }

        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
        //计算得出通知验证结果
        //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
        boolean verify_result = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET, "RSA2");

        if(verify_result) {//验证成功

            //请在这里加上商户的业务逻辑程序代码

            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

            //交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");

            //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
            if (trade_status.equals("TRADE_FINISHED")) {//交易结束
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                //如果有做过处理，不执行商户的业务程序

                //注意：
                //如果签约的是可退款协议，退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
                //如果没有签约可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
            } else if (trade_status.equals("TRADE_SUCCESS")) {//交易成功
                System.out.println(trade_status);
                //判断该笔订单是否在商户网站中已经做过处理
                QueryWrapper wrapper = new QueryWrapper().eq(PetOrder::getOrderNo, out_trade_no);
                PetOrder order = petOrderService.getOne(wrapper);
                if(order != null) {
                    //说明订单存在,修改为已支付
                    order.setOrderStatus(1);
                    petOrderService.updateById(order);
                    //给对应的前端界面发送一则支付成功通知
                    String business_type = "order_payment";
                    QueryWrapper queryWrapper = new QueryWrapper();
                    queryWrapper.eq(WsPushMessages::getBusinessId, out_trade_no);
                    queryWrapper.eq(WsPushMessages::getBusinessType, business_type);

                    WsPushMessages wsPushMessages = wsPushMessagesService.getOne(queryWrapper);
                    String pushToken = wsPushMessages.getPushToken();
                    webSocketServer.sendToOneClient(pushToken+"支付成功",pushToken);
                }else{
                    //不存在暂时不做任何处理
                    System.out.println("订单号不存在，不做任何处理");
                }

                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                //如果有做过处理，不执行商户的业务程序

                //注意：
                //如果签约的是可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
            }
            response.getWriter().write("success");
        }else{
            response.getWriter().write("fail");
        }
    }
}
