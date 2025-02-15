package posting.job.collector.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class StandbyCrawledJobCommand {
    @TargetAggregateIdentifier
    String jobId;
    String jobTitle;
    LocalDateTime crawledAt;
}
