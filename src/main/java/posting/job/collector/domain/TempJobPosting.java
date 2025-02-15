package posting.job.collector.domain;

import java.time.LocalDateTime;

public record TempJobPosting(
        String jobId,
        String jobTitle,
        LocalDateTime crawledAt
) {
}
