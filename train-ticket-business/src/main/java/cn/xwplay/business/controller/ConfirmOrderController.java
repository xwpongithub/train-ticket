package cn.xwplay.business.controller;

import cn.xwplay.business.req.ConfirmOrderDoReq;
import cn.xwplay.business.service.ConfirmOrderService;
import cn.xwplay.common.response.CommonResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/confirm-order")
@Slf4j
public class ConfirmOrderController {

//    @Resource
//    private BeforeConfirmOrderService beforeConfirmOrderService;

//    @Autowired
//    private StringRedisTemplate redisTemplate;

//    @Value("${spring.profiles.active}")
//    private String env;

    private final ConfirmOrderService confirmOrderService;

    // 接口的资源名称不要和接口路径一致，会导致限流后走不到降级方法中
//    @SentinelResource(value = "confirmOrderDo", blockHandler = "doConfirmBlock")
    @PostMapping("do")
    public CommonResp<Object> doConfirm(@Valid @RequestBody ConfirmOrderDoReq req) {
//        if (!env.equals("dev")) {
//            // 图形验证码校验
//            String imageCodeToken = req.getImageCodeToken();
//            String imageCode = req.getImageCode();
//            String imageCodeRedis = redisTemplate.opsForValue().get(imageCodeToken);
//            log.info("从redis中获取到的验证码：{}", imageCodeRedis);
//            if (ObjectUtils.isEmpty(imageCodeRedis)) {
//                return new CommonResp<>(false, "验证码已过期", null);
//            }
//            // 验证码校验，大小写忽略，提升体验，比如Oo Vv Ww容易混
//            if (!imageCodeRedis.equalsIgnoreCase(imageCode)) {
//                return new CommonResp<>(false, "验证码不正确", null);
//            } else {
//                // 验证通过后，移除验证码
//                redisTemplate.delete(imageCodeToken);
//            }
//        }
//        var id = beforeConfirmOrderService.beforeDoConfirm(req);
//        return new CommonResp<>(String.valueOf(id));

        confirmOrderService.doConfirm(req);
        return new CommonResp<>();
    }

//    @GetMapping("/query-line-count/{id}")
//    public CommonResp<Integer> queryLineCount(@PathVariable Long id) {
//        Integer count = confirmOrderService.queryLineCount(id);
//        return new CommonResp<>(count);
//    }
//
//    @GetMapping("/cancel/{id}")
//    public CommonResp<Integer> cancel(@PathVariable Long id) {
//        Integer count = confirmOrderService.cancel(id);
//        return new CommonResp<>(count);
//    }
//
//    /** 降级方法，需包含限流方法的所有参数和BlockException参数，且返回值要保持一致
//     * @param req
//     * @param e
//     */
//    public CommonResp<Object> doConfirmBlock(ConfirmOrderDoReq req, BlockException e) {
//        LOG.info("ConfirmOrderController购票请求被限流：{}", req);
//        // throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
//        CommonResp<Object> commonResp = new CommonResp<>();
//        commonResp.setSuccess(false);
//        commonResp.setMessage(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION.getDesc());
//        return commonResp;
//
//    }

}
