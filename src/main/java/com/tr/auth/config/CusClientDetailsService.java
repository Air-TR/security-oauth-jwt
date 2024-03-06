//package com.tr.auth.config;
//
//import com.google.common.collect.Lists;
//import com.google.common.collect.Sets;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.provider.ClientDetails;
//import org.springframework.security.oauth2.provider.ClientDetailsService;
//import org.springframework.security.oauth2.provider.ClientRegistrationException;
//import org.springframework.security.oauth2.provider.client.BaseClientDetails;
//import org.springframework.stereotype.Service;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static java.util.stream.Collectors.toList;
//
///**
// * @Author: TR
// */
//@Service
//public class CusClientDetailsService implements ClientDetailsService {
//
//    @Override
//    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
//        BaseClientDetails details = new BaseClientDetails();
//        details.setScope(Arrays.asList("all"));
////        details.setAutoApproveScopes(Arrays.asList("read", "write"));
//        details.setClientId("auth");
//        details.setClientSecret("$2a$10$VVhz0JEm3uNQPxdx3vgdDuYgxe4e6X7SfNlewXfdtchirGUgfybTS");
//        details.setAccessTokenValiditySeconds(3600);
//        details.setRefreshTokenValiditySeconds(36000);
////        List<SimpleGrantedAuthority> authorities = Arrays.stream(app.getAuthorities()
////                .split(splitRegex)).map(SimpleGrantedAuthority::new).collect(toList());
////        details.setAuthorities(authorities);
//        details.setAuthorizedGrantTypes(Arrays.asList("password"));
////        details.setRegisteredRedirectUri(Sets.newHashSet(app.getRedirectUrls().split(splitRegex)));
//        return details;
//    }
//
//}
