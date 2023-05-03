/**
 * Copyright [2023] [RAFAEL ALCOCER CALDERA]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rafael.alcocer.caldera.filter;

import java.io.IOException;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CorrelationIdFilter implements Filter {

    private static final String CORRELATION_ID = "RacCorrelationId";

    private static final Logger LOGGER = LoggerFactory.getLogger(CorrelationIdFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String correlationId = request.getHeader(CORRELATION_ID);
        LOGGER.info("request.getHeader(" + CORRELATION_ID + "): {}", correlationId);

        if (StringUtils.isBlank(correlationId)) {
            correlationId = UUID.randomUUID().toString();
            LOGGER.info("Correlation Id with name [" + CORRELATION_ID + "] created: {}", correlationId);
        }

        response.addHeader(CORRELATION_ID, correlationId);
        filterChain.doFilter(request, response);

        LOGGER.info("RESPONSE status: {},[" + CORRELATION_ID + "]: {}", response.getStatus(), correlationId);
    }
}
