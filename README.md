# SPRING BOOT CORRELATION ID WITH WEB FILTER EXAMPLE

## Synopsis

The project is a Spring Boot Application.

## Motivation

I wanted to do a Spring Boot Application, that shows how to implement the correlation id using web filter to track the request.

## Pre Requirements

You only need to add this to the application.yml:

```
logging:
  pattern: 
    console: "%-4relative [%thread] %-5level %logger{35} %X{RacCorrelationId} --- %msg %n"
```

Or this if you have application.properties:

```
logging.pattern.console=%-4relative [%thread] %-5level %logger{35} %X{RacCorrelationId} --- %msg %n
```

You also need to add the filter:

```java
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
            LOGGER.info("correlationId created: {}", correlationId);
        }

        response.addHeader(CORRELATION_ID, correlationId);
        filterChain.doFilter(request, response);

        LOGGER.info("RESPONSE status: {}, [RacCorrelationId: {}]", response.getStatus(), correlationId);
    }
}
```


You also need to add the configuration:

```java
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

```


## Notes

I replaced the following code:

```java
@RequiredArgsConstructor
@RestController
public class ThingController {
    
    private final ThingService thingService;
    
    public ThingController(ThingServiceImpl thingService) {
        this.thingService = thingService;
    }
```

With this code using lombok annotation **@RequiredArgsConstructor**.

Will create constructor receiving non-static final fields.

The annotation will not generate a constructor for the following fields:
- Initialized non-null fields
- Initialized final fields
- Static fields
- Non-final fields

```java
@RequiredArgsConstructor
@RestController
public class ThingController {
    
    private final ThingService thingService;
```

USING POSTMAN:
--------------
GET
http://localhost:8050/thing


Response:
---------
```json
{
    "id": 13,
    "name": "NEW THING NAME",
    "description": "is the pattern of narrative development that aims to make vivid a place, object, character, or group"
}
```


ECLIPSE CONSOLE:
----------------
```

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.0.0)

251  [main] INFO  r.a.c.CorrelationIdApplication  --- Starting CorrelationIdApplication using Java 17.0.7 with PID 11300 (/Users/rac/Documents/GitHub/spring-boot-correlation-id-with-web-filter/target/classes started by rac in /Users/rac/Documents/GitHub/spring-boot-correlation-id-with-web-filter) 
251  [main] INFO  r.a.c.CorrelationIdApplication  --- No active profile set, falling back to 1 default profile: "default" 
647  [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer  --- Tomcat initialized with port(s): 8050 (http) 
653  [main] INFO  o.a.catalina.core.StandardService  --- Starting service [Tomcat] 
653  [main] INFO  o.a.catalina.core.StandardEngine  --- Starting Servlet engine: [Apache Tomcat/10.1.1] 
693  [main] INFO  o.a.c.c.C.[Tomcat].[localhost].[/]  --- Initializing Spring embedded WebApplicationContext 
695  [main] INFO  o.s.b.w.s.c.ServletWebServerApplicationContext  --- Root WebApplicationContext: initialization completed in 422 ms 
856  [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer  --- Tomcat started on port(s): 8050 (http) with context path '' 
860  [main] INFO  r.a.c.CorrelationIdApplication  --- Started CorrelationIdApplication in 0.784 seconds (process running for 0.916) 
5896 [http-nio-8050-exec-1] INFO  o.a.c.c.C.[Tomcat].[localhost].[/]  --- Initializing Spring DispatcherServlet 'dispatcherServlet' 
5897 [http-nio-8050-exec-1] INFO  o.s.web.servlet.DispatcherServlet  --- Initializing Servlet 'dispatcherServlet' 
5897 [http-nio-8050-exec-1] INFO  o.s.web.servlet.DispatcherServlet  --- Completed initialization in 0 ms 
5899 [http-nio-8050-exec-1] INFO  r.a.c.filter.CorrelationIdFilter  --- request.getHeader(RacCorrelationId): null 
5902 [http-nio-8050-exec-1] INFO  r.a.c.filter.CorrelationIdFilter  --- Correlation Id with name [RacCorrelationId] created: ed667b29-0632-485b-9db3-e79a4e82907b 
5912 [http-nio-8050-exec-1] INFO  r.a.c.controller.ThingController  --- ThingController... showThing()...  
5913 [http-nio-8050-exec-1] INFO  r.a.c.service.ThingServiceImpl  --- ThingServiceImpl... generateThingData()...  
5937 [http-nio-8050-exec-1] INFO  r.a.c.filter.CorrelationIdFilter  --- RESPONSE status: 200,[RacCorrelationId]: ed667b29-0632-485b-9db3-e79a4e82907b 

```

You can see that **ed667b29-0632-485b-9db3-e79a4e82907b** is added in every LOGGER automatically.


USING POSTMAN WITH HEADER:
--------------------------
```
curl --location --request GET 'http://localhost:8050/thing' \
--header 'RacCorrelationId: 666'
```


Response:
---------
```json
{
    "id": 13,
    "name": "NEW THING NAME",
    "description": "is the pattern of narrative development that aims to make vivid a place, object, character, or group"
}
```


ECLIPSE CONSOLE:
----------------
```

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.0.0)

272  [main] INFO  r.a.c.CorrelationIdApplication  --- Starting CorrelationIdApplication using Java 17.0.7 with PID 11318 (/Users/rac/Documents/GitHub/spring-boot-correlation-id-with-web-filter/target/classes started by rac in /Users/rac/Documents/GitHub/spring-boot-correlation-id-with-web-filter) 
272  [main] INFO  r.a.c.CorrelationIdApplication  --- No active profile set, falling back to 1 default profile: "default" 
667  [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer  --- Tomcat initialized with port(s): 8050 (http) 
673  [main] INFO  o.a.catalina.core.StandardService  --- Starting service [Tomcat] 
673  [main] INFO  o.a.catalina.core.StandardEngine  --- Starting Servlet engine: [Apache Tomcat/10.1.1] 
716  [main] INFO  o.a.c.c.C.[Tomcat].[localhost].[/]  --- Initializing Spring embedded WebApplicationContext 
717  [main] INFO  o.s.b.w.s.c.ServletWebServerApplicationContext  --- Root WebApplicationContext: initialization completed in 421 ms 
870  [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer  --- Tomcat started on port(s): 8050 (http) with context path '' 
874  [main] INFO  r.a.c.CorrelationIdApplication  --- Started CorrelationIdApplication in 0.791 seconds (process running for 0.938) 
11128 [http-nio-8050-exec-1] INFO  o.a.c.c.C.[Tomcat].[localhost].[/]  --- Initializing Spring DispatcherServlet 'dispatcherServlet' 
11129 [http-nio-8050-exec-1] INFO  o.s.web.servlet.DispatcherServlet  --- Initializing Servlet 'dispatcherServlet' 
11129 [http-nio-8050-exec-1] INFO  o.s.web.servlet.DispatcherServlet  --- Completed initialization in 0 ms 
11132 [http-nio-8050-exec-1] INFO  r.a.c.filter.CorrelationIdFilter  --- request.getHeader(RacCorrelationId): 666 
11146 [http-nio-8050-exec-1] INFO  r.a.c.controller.ThingController  --- ThingController... showThing()...  
11146 [http-nio-8050-exec-1] INFO  r.a.c.service.ThingServiceImpl  --- ThingServiceImpl... generateThingData()...  
11171 [http-nio-8050-exec-1] INFO  r.a.c.filter.CorrelationIdFilter  --- RESPONSE status: 200,[RacCorrelationId]: 666 

```


## License

All work is under Apache 2.0 license