package cn.xwplay.member.aspect;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.filter.PropertyFilter;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;

@Aspect
@Component
@Slf4j
public class LogAspect {

    private static final String LOG_ID = "LOG_ID";

    @Pointcut("execution(public * cn.xwplay..*Controller.*(..))")
    public void controllerPointcut() {}

    @Before("controllerPointcut()")
    public void controllerBefore(JoinPoint joinPoint) {
        var logId = System.currentTimeMillis()+randomString(3);
        MDC.put(LOG_ID,logId);
        var attrs = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        var request = attrs.getRequest();
        var signature = joinPoint.getSignature();
        var name = signature.getName();

        log.info("------------MDC:{}请求进入-----------",logId);
        log.info("请求URL:[{}] - {}",request.getMethod(),request.getRequestURL());
        log.info("执行类-方法:{}.{}",signature.getDeclaringTypeName(),name);
        log.info("远程地址:{}",request.getRemoteAddr());

        var args = joinPoint.getArgs();
        var arguments = new Object[args.length];
        for (var i = 0; i < args.length; i++) {
            if (args[i] instanceof ServletRequest || args[i] instanceof ServletResponse
            || args[i] instanceof MultipartFile) {
                continue;
            }
            arguments[i] = args[i];
        }
        // 排除字段，敏感或太长的字段不显示
        // 排除字段，敏感或太长的字段不显示
        PropertyFilter filter = (object, propName, value) -> {
            // 在这里定义需要保留的属性条件
            return !"mobile".equals(propName);
        };
        log.info("请求参数：{}", JSON.toJSONString(arguments,filter));
    }

    @Around("controllerPointcut()")
    public Object controllerAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
      var startTime = System.currentTimeMillis();
      var result =proceedingJoinPoint.proceed();
        // 排除字段，敏感或太长的字段不显示
        PropertyFilter filter = (object, name, value) -> {
            // 在这里定义需要保留的属性条件
            return !"mobile".equals(name);
        };
        log.info("返回结果：{}", JSON.toJSONString(result,filter));
        log.info("---------------请求结束，耗时：{}ms -------------",System.currentTimeMillis() - startTime);
        return result;
    }

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String randomString(int len) {
        // 生成长度为3的随机字符串
        var sb = new StringBuilder(len);
        for (var i = 0; i < len; i++) {
            var randomIndex = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }
}
