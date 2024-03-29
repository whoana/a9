server:
  port: AGENTPORT

logging:
  config: ${apple.mint.home}/config/logback-spring.xml
  level:
    root: debug

spring:
  devtools:
    livereload:
      enabled: true
    restart:
      enabled: true
      poll-interval: "2s"
      quiet-period: "1s"
  application:
    name: a9
  main:
    banner-mode: CONSOLE
  messages:
    always-use-message-format: false
    basename: messages/error, messages/message
    cache-second: -1
    encoding: UTF-8
  task:
    execution:
      pool:
        max-size: 100
        queue-capacity: 200
        keep-alive: 10s
    scheduling:
      pool:
        size: 10
management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: ALWAYS
    shutdown:
      enabled: true
    metrics:
      enabled: true
    restart:
      enabled: true
---
spring:
  profiles: development

logging:
  level:
    root: debug

---
spring:
  profiles: production

logging:
  level:
    root: info
