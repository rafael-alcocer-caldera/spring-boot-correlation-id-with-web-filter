package rafael.alcocer.caldera.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CorrelationIdConfig {

    @Bean
    public FilterRegistrationBean<CorrelationIdFilter> filterRegistrationBean() {
        CorrelationIdFilter correlationIdFilter = new CorrelationIdFilter();
        FilterRegistrationBean<CorrelationIdFilter> filterRegistrationBean = new FilterRegistrationBean<>();

        filterRegistrationBean.setFilter(correlationIdFilter);
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(1);

        return filterRegistrationBean;
    }
}
