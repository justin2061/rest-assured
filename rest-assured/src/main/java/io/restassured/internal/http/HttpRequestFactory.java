/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.restassured.internal.http;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.http.Method.*;
import static io.restassured.internal.assertion.AssertParameter.notNull;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.apache.commons.lang3.StringUtils.upperCase;

/**
 * Enumeration of valid HTTP methods that may be used in REST Assured.
 *
 * @author <a href='mailto:tomstrummer+httpbuilder@gmail.com'>Tom Nichols</a> (original author)
 * @author Johan Haleby
 */
public class HttpRequestFactory {
    private static final Map<String, Class<? extends HttpRequestBase>> HTTP_METHOD_TO_HTTP_REQUEST_TYPE =
            new HashMap<String, Class<? extends HttpRequestBase>>() {{
                put(PUT.name(), HttpPut.class);
                put(POST.name(), HttpPost.class);
                put(PATCH.name(), HttpPatch.class);
            }};

    /**
     * Get the HttpRequest class that represents this request type.
     *
     * @return a non-abstract class that implements {@link HttpRequest}
     */
    static HttpRequestBase createHttpRequest(URI uri, String httpMethod) {
        String method = notNull(upperCase(trimToNull(httpMethod)), "Http method");
        Class<? extends HttpRequestBase> type = HTTP_METHOD_TO_HTTP_REQUEST_TYPE.get(method);
        final HttpRequestBase httpRequest;
        if (type == null) {
            httpRequest = new CustomHttpMethod(method, uri);
        } else {
            try {
                httpRequest = type.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            httpRequest.setURI(uri);
        }
        return httpRequest;
    }
}