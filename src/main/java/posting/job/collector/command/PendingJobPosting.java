package posting.job.collector.command;

import java.time.LocalDateTime;

public record PendingJobPosting(
        String jobId,
        String jobTitle,
        LocalDateTime crawledAt
) {
}
