package com.kamelia.ugeoverflow.core

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebMvc
class MvcConfig : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/static/css/**").addResourceLocations("classpath:/static/css/")
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(UserInterceptor())
    }
}
