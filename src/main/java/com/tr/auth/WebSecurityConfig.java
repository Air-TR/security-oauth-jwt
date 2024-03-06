package com.tr.auth;

import com.tr.auth.config.CusTokenEnhancer;
import com.tr.auth.config.TokenFilter;
import com.tr.auth.config.CusUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

/**
 * @Author TR
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${enable.security}")
    private Boolean securityEnable;

    @Value("${security.ex-path}")
    private String securityExPath;

    @Autowired
    private CusUserDetailsService userDetailsService;

    @Resource
    private TokenFilter tokenFilter;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证用户的来源（从数据库获取）
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    /**
     * 将不需要鉴权的请求路径写在 antMatchers() 中，这边是真正让 SpringSecurity 过滤放行的地方，而不在 TokenFilter 判断（TokenFilter 仅做 Token 校验）
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (!securityEnable) securityExPath = "/**";
        http.authorizeRequests()
            .antMatchers(securityExPath.split(","))
            .permitAll().and()
            .authorizeRequests().anyRequest().authenticated()
            .and().csrf().disable().cors();
        // 把 token 校验过滤器添加到过滤器链中
        http.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * 认证管理器，登录时认证使用
     *  调用这个 Bean 的 authenticate 方法会由 Security 自动做认证
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new CusTokenEnhancer();
    }

}
