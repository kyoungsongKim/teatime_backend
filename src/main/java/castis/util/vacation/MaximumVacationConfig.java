package castis.util.vacation;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@ConfigurationProperties(prefix = "vacation")
@Configuration
@Data
public class MaximumVacationConfig {
    Map<Byte, Float> maximumVacationRule = new HashMap<Byte, Float>();

}