package posting.job.collector.service.normalizer;

import posting.job.collector.domain.JobPosting;
import posting.job.collector.domain.RawJobPosting;

import java.util.List;

public interface RawJobNormalizer {
    List<JobPosting> normalize(List<RawJobPosting> rawJob);
}
