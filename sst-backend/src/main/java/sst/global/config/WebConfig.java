package sst.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry
        	/* 브라우저 접근 경로 */
            .addResourceHandler("/uploads/**")
            /* 실제 로컬 파일 저장 경로 */
            .addResourceLocations("file:uploads/");
    }
}
