package cn.xwplay.business.config;

import com.baomidou.mybatisplus.extension.incrementer.PostgreKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

//    @Bean
//    MybatisPlusInterceptor mybatisPlusInterceptor() {
//        var interceptor = new MybatisPlusInterceptor();
//        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
//        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
//        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.POSTGRE_SQL));
//        return interceptor;
//    }

    @Bean
    PostgreKeyGenerator postgreKeyGenerator() {
        return new PostgreKeyGenerator();
    }

}
