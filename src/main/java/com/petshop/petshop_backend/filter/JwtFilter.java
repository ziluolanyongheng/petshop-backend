package com.petshop.petshop_backend.filter;


import com.petshop.petshop_backend.entity.PetUser;
import com.petshop.petshop_backend.entity.base.LoginUser;
import com.petshop.petshop_backend.mapper.PetUserMapper;
import com.petshop.petshop_backend.utils.BaseContext;
import com.petshop.petshop_backend.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    PetUserMapper petUserMapper;

    @Autowired
    HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try{
//            String requestURI = request.getRequestURI();
//            if (requestURI.startsWith("/doc.html")) {
//                filterChain.doFilter(request, response);
//                return;
//            }

            // 3. 放行请求（继续执行后续过滤器或Servlet）


            String token = request.getHeader("Authorization");

//            Enumeration<String> headerNames = request.getHeaderNames();
//            System.out.println(headerNames.toString());
            if (token == null || token.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }
            Integer userId = (Integer) JwtUtil.parseToken(token, "id");
            PetUser petUser = petUserMapper.selectOneById(userId);
            if( petUser == null ){
                filterChain.doFilter(request, response);
                return;
            }
            LoginUser loginUser = new LoginUser(petUser);
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginUser, null, null);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request, response );
        }
        catch (Exception e){
            handlerExceptionResolver.resolveException(request, response, null, e );
        }
    }
}
