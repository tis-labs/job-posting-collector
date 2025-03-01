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

import java.time.LocalDate;
import java.util.Map;

@Getter
@Aggregate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PendingJobPosting {
    @AggregateIdentifier
    private String crawledJobId;
    private LocalDate crawledAt;
    private String jobTitle;
    private String jobIdentity ;
    private String jobCompany;
    private String jobFamily;
    private String jobType;
    private String jobUrl;
    private Map<String, String> jobOptionalInformation;
    private CrawledStatus crawledStatus;

    @CommandHandler
    public PendingJobPosting(StandbyCrawledJobCommand standbyCrawledJobCommand) {
        var event = new PendingJobPostingEvent(
                standbyCrawledJobCommand.getCrawledJobId(),
                standbyCrawledJobCommand.getCrawledAt(),
                standbyCrawledJobCommand.getJobTitle(),
                standbyCrawledJobCommand.getJobIdentity(),
                standbyCrawledJobCommand.getJobCompany(),
                standbyCrawledJobCommand.getJobFamily(),
                standbyCrawledJobCommand.getJobType(),
                standbyCrawledJobCommand.getJobUrl(),
                standbyCrawledJobCommand.getJobOptionalInformation(),
                CrawledStatus.PENDING

        );
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler //event 발생시 처리(이벤트 발생할 때 마다 호출)
    public void on(PendingJobPostingEvent pendingJobPostingEvent) {
        this.crawledJobId = pendingJobPostingEvent.crawledJobId();
        this.jobTitle = pendingJobPostingEvent.jobTitle();
        this.jobIdentity = pendingJobPostingEvent.jobIdentity();
        this.jobCompany = pendingJobPostingEvent.jobCompany();
        this.jobFamily = pendingJobPostingEvent.jobFamily();
        this.jobType = pendingJobPostingEvent.jobType();
        this.jobUrl = pendingJobPostingEvent.jobUrl();
        this.jobOptionalInformation = pendingJobPostingEvent.jobOptionalInformation();
    }
}
