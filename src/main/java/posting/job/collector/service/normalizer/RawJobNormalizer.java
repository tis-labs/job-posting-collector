package posting.job.collector.service.normalizer;

import posting.job.collector.domain.CrawledJobPosting;
import posting.job.collector.domain.RawJobPosting;

import java.util.List;

public interface RawJobNormalizer {
    List<CrawledJobPosting> normalize(List<RawJobPosting> rawJob);
}
