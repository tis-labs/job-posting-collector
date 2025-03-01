package posting.job.collector.command;

import java.time.LocalDate;
import java.util.Map;

public record PendingJobPostingEvent(
        String crawledJobId,
        LocalDate crawledAt,
        String jobTitle,
        String jobIdentity,
        String jobCompany,
        String jobFamily,
        String jobType,
        String jobUrl,
        Map<String, String> jobOptionalInformation,
        CrawledStatus crawledStatus
) {

}
