package com.reisbloghub.utils;

import com.Reisblog.utils.IpUtils;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IpUtilsTest {

    @Test
    public void testGetClientIp_WithXForwardedFor() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "8.8.8.8, 10.0.0.1, 192.168.1.1");
        String ip = IpUtils.getClientIp(request);
        assertEquals("8.8.8.8", ip);
    }

    @Test
    public void testGetClientIp_WithXRealIP() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Real-IP", "9.9.9.9");
        String ip = IpUtils.getClientIp(request);
        assertEquals("9.9.9.9", ip);
    }

    @Test
    public void testGetClientIp_WithProxyClientIP() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Proxy-Client-IP", "10.10.10.10");
        String ip = IpUtils.getClientIp(request);
        assertEquals("10.10.10.10", ip);
    }

    @Test
    public void testGetClientIp_WithWLProxyClientIP() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("WL-Proxy-Client-IP", "11.11.11.11");
        String ip = IpUtils.getClientIp(request);
        assertEquals("11.11.11.11", ip);
    }

    @Test
    public void testGetClientIp_WithHTTPClientIP() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("HTTP_CLIENT_IP", "12.12.12.12");
        String ip = IpUtils.getClientIp(request);
        assertEquals("12.12.12.12", ip);
    }

    @Test
    public void testGetClientIp_WithHTTPXForwardedFor() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("HTTP_X_FORWARDED_FOR", "13.13.13.13");
        String ip = IpUtils.getClientIp(request);
        assertEquals("13.13.13.13", ip);
    }

    @Test
    public void testGetClientIp_NoHeaders_ShouldReturnRemoteAddr() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.0.100");
        String ip = IpUtils.getClientIp(request);
        assertEquals("192.168.0.100", ip);
    }

    @Test
    public void testGetClientIp_LocalIPv6_ShouldConvertTo127_0_0_1() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("0:0:0:0:0:0:0:1");
        String ip = IpUtils.getClientIp(request);
        assertEquals("127.0.0.1", ip);
    }

    @Test
    public void testGetClientIp_WithUnknownHeader_ShouldFallback() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "unknown");
        request.setRemoteAddr("10.0.0.2");
        String ip = IpUtils.getClientIp(request);
        assertEquals("10.0.0.2", ip);
    }
}