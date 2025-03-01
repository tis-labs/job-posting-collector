package posting.job.collector.service.normalizer;

import posting.job.collector.domain.CrawledJobPosting;
import posting.job.collector.domain.RawJobPosting;

import java.util.ArrayList;
import java.util.List;

public class SocarCorpJobNormalizer implements RawJobNormalizer{
    @Override
    public List<CrawledJobPosting> normalize(List<RawJobPosting> rawJobPostings) {
        List<CrawledJobPosting> crawledJobPostings = new ArrayList<>();

        for (RawJobPosting rawJobPosting : rawJobPostings) {
            CrawledJobPosting crawledJobPosting = new CrawledJobPosting();
            crawledJobPosting.setJobCompany(rawJobPosting.getJobCompany());
            crawledJobPosting.setJobTitle(rawJobPosting.getJobTitle());
            crawledJobPosting.setJobFamily(rawJobPosting.getJobFamily());
            crawledJobPosting.setJobType(rawJobPosting.getJobType());
            crawledJobPosting.setJobUrl(rawJobPosting.getJobUrl());
            crawledJobPosting.setJobOptionalInformation(rawJobPosting.getJobOptionalInformation());
            crawledJobPosting.setJobIdentity(crawledJobPosting.generateUniqueId());

            crawledJobPostings.add(crawledJobPosting);
        }

        return crawledJobPostings;
    }
}
