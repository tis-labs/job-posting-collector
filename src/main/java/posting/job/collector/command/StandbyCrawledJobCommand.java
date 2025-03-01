package posting.job.collector.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@AllArgsConstructor
public class StandbyCrawledJobCommand {
    @TargetAggregateIdentifier
    String crawledJobId; //크롤링된 식별자 : jobIdentity+ crawledAt
    LocalDate crawledAt;
    String jobTitle;
    String jobIdentity;
    String jobCompany;
    String jobFamily;
    String jobType;
    String jobUrl;
    Map<String, String> jobOptionalInformation;
}
