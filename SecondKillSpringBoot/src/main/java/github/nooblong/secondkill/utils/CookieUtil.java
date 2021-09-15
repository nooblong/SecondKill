package github.nooblong.secondkill.utils;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
public class CookieUtil {

    public static final String TICKET_NAME = "userTicket";

    /**
     * 得到cookie的值 no decode
     *
     * @param request request
     * @param cookieName cookie name
     * @return cookie
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        return getCookieValue(request, cookieName, false);
    }

    /**
     * 得到cookie的值 utf-8 decode
     *
     * @param request request
     * @param cookieName cookie name
     * @param isDecoder 是否解码
     * @return cookie
     */
    private static String getCookieValue(HttpServletRequest request, String cookieName, boolean isDecoder) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookieName == null) {
            return null;
        }
        String retValue = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                if (isDecoder) {
                    retValue = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                } else {
                    retValue = cookie.getValue();
                }
                break;
            }
        }
        return retValue;
    }

    /**
     * 设置cookie值 UTF-8 encode
     *
     * @param request request
     * @param response response
     * @param cookieName cookie name
     * @param cookieValue cookie value
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName
            , String cookieValue) {
        if (cookieValue == null) {
            cookieValue = "";
        } else {
            cookieValue = URLEncoder.encode(cookieValue, StandardCharsets.UTF_8);
        }
        Cookie cookie = new Cookie(cookieName, cookieValue);
        if (null != request) {
            String domainName = getDomainName(request);
//            log.info("domainName: " + domainName);
            if (!"localhost".equals(domainName)) {
                cookie.setDomain(domainName);
            }
        }
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * 获取域名
     * @param request request
     * @return 域名
     */
    private static String getDomainName(HttpServletRequest request) {
        String domainName = null;
        String serverName = request.getRequestURL().toString();
        if (serverName.equals("")) {
            domainName = "";
        } else {
            serverName = serverName.toLowerCase();
            if (serverName.startsWith("http://")) {
                serverName = serverName.substring(7);
            }
            int end = serverName.length();
            if (serverName.contains("/")) {
                end = serverName.indexOf("/");
            }
            serverName = serverName.substring(0, end);
            final String[] domains = serverName.split("\\.");
            int len = domains.length;
            if (len > 3) {
                domainName = domains[len - 3] + "." + domains[len - 2] + "." + domains[len - 1];
            } else if (len > 1) {
                domainName = domains[len - 2] + "." + domains[len - 1];
            } else {
                domainName = serverName;
            }
        }
        if (domainName.indexOf(":") > 0) {
            String[] ary = domainName.split(":");
            domainName = ary[0];
        }
        return domainName;
    }

    /**
     * 删除cookie值
     *
     * @param request request
     * @param response response
     * @param cookieName cookie name
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        setCookie(request, response, cookieName, "");
    }
}
