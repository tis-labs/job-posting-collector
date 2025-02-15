package posting.job.collector.command;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.LocalDateTime;

@Getter
@Aggregate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CrawledJobPosting {
    @AggregateIdentifier
    private String jobId;
    private String jobTitle;
    private CrawledStatus crawledStatus;
    private LocalDateTime crawledAt;

    @CommandHandler
    public CrawledJobPosting(StandbyCrawledJobCommand command) {
        var event = new PendingJobPosting(command.getJobId(), command.getJobTitle(), command.getCrawledAt());
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(PendingJobPosting pendingJobPosting) {
        this.jobId = pendingJobPosting.jobId();
        this.crawledStatus = CrawledStatus.PENDING;
    }
}
