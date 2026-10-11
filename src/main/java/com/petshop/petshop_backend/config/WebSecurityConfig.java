package com.petshop.petshop_backend.config;

import com.petshop.petshop_backend.filter.JwtFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;


@Configuration
@EnableWebSecurity
//@EnableMethodSecurity//方法级别的权限管理
public class WebSecurityConfig {
    @Autowired
    private UserDetailsService userDetailsService;//从数据库中加载用户信息

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    HandlerExceptionResolver handlerExceptionResolver;
    @Bean
    //授权管理器
    AuthenticationManager authenticationManager() throws Exception{
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();//认证提供者
        daoAuthenticationProvider.setUserDetailsService(userDetailsService); //认证提供者，它通过 UserDetailsService 加载用户信息并验证用户密码
        daoAuthenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());
        return new ProviderManager(daoAuthenticationProvider);
    }


    //过滤器链
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.formLogin(Customizer.withDefaults());
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(Customizer.withDefaults());

        http.authorizeHttpRequests(
                req-> req.requestMatchers("/user/login").permitAll()
                        .requestMatchers("/user/uniLogin").permitAll()
                        .requestMatchers("/user/test").permitAll()
                        .requestMatchers("/user/register").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        //alipay
                        .requestMatchers("/pay/alipay").permitAll()
                        .requestMatchers("/pay/paynotify").permitAll()
                        //send_email
                        .requestMatchers("/verification/sendEmailCode").permitAll()
                        .requestMatchers("/verification/verifyEmailCode").permitAll()
                        //upload
                        .requestMatchers("/oss/**").permitAll()
                        .requestMatchers("/login/**").permitAll()
                        .requestMatchers("/qq/**").permitAll()
                        //swagger
                        .requestMatchers("/doc.html").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/v2/api-docs/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
        );


        //给过滤链添加错误处理，使得我们自定义的错误处理器能够捕捉到错误
        http.exceptionHandling(exp->exp.authenticationEntryPoint(new AuthenticationEntryPoint() {
            @Override
            public void commence(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    AuthenticationException authException) throws IOException, ServletException {
                handlerExceptionResolver.resolveException(request,response, null, authException);
            }
        }));
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }



}