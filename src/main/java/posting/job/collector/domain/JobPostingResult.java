package posting.job.collector.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class JobPostingResult {
    private List<JobPosting> jobPostings;
    private int totalCount;
    private String lastUpdated;
}
