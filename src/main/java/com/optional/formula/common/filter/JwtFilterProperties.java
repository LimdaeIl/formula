package com.optional.formula.common.filter;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.jwt.filter")
public class JwtFilterProperties {
    private List<String> includePaths;
    private List<String> excludePaths;
}
