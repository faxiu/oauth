package com.esp.oauth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author hekai
 * @Date 2019/3/28 17:47
 */
@Service
public class AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private AuthorizationServerTokenServices authorizationServerTokenServices;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception)throws IOException, ServletException {

        String grantType = request.getParameter("grant_type");
        if("refresh_token".equals(grantType)){
            String header = request.getHeader("Authorization");
            if (header != null && header.toLowerCase().startsWith("Basic ")) {
                throw new UnapprovedClientAuthenticationException("请求头中无client信息");
            }

            String[] tokens = AuthenticationSuccessHandler.extractAndDecodeHeader(header, request);
            assert tokens.length == 2;

            String clientId = tokens[0];
            String clientIdSecret = tokens[1];

            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
            if(clientDetails == null){
                throw new UnapprovedClientAuthenticationException("clientId对应的信息不存在：" + clientId);
            }else if(!StringUtils.pathEquals(clientDetails.getClientSecret(), clientIdSecret)){
                throw new UnapprovedClientAuthenticationException("clientSecret不匹配：" + clientIdSecret);
            }

            String refreshTokenValue = request.getParameter("refresh_token");
            if(StringUtils.isEmpty(refreshTokenValue)){
                throw new UnapprovedClientAuthenticationException("refresh_token不存在");
            }

            TokenRequest tokenRequest = new TokenRequest(MapUtils.EMPTY_MAP, clientId, clientDetails.getScope(), "custom");

            OAuth2AccessToken accessToken = authorizationServerTokenServices.refreshAccessToken(refreshTokenValue, tokenRequest);

            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(accessToken));
        }else {
            this.returnJson(response,exception);
        }
    }

    private void returnJson(HttpServletResponse response,

                            AuthenticationException exception)throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().println("{\"exceptionId\":\"null\",\"messageCode\":\"401\"," +
                "\"message\": \""+ exception.getMessage() +"\",\"serverTime\": " + System.currentTimeMillis() +"}");

    }
}
