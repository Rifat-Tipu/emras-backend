package com.emras.api_gateway.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.*;

/**
 * Wraps HttpServletRequest to allow adding custom headers.
 *
 * HttpServletRequest headers are read-only by default.
 * The Gateway needs to add X-User-Id and X-User-Role headers
 * after JWT validation so downstream services can read them.
 */
public class MutableHttpServletRequest extends HttpServletRequestWrapper {

    private final Map<String, String> customHeaders = new HashMap<>();

    public MutableHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    public void addHeader(String name, String value) {
        customHeaders.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        if (customHeaders.containsKey(name)) {
            return customHeaders.get(name);
        }
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (customHeaders.containsKey(name)) {
            return Collections.enumeration(List.of(customHeaders.get(name)));
        }
        return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> names = new HashSet<>(customHeaders.keySet());
        Enumeration<String> original = super.getHeaderNames();
        while (original.hasMoreElements()) {
            names.add(original.nextElement());
        }
        return Collections.enumeration(names);
    }
}