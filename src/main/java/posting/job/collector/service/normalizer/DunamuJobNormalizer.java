package posting.job.collector.service.normalizer;

import posting.job.collector.domain.JobPosting;
import posting.job.collector.domain.RawJobPosting;

import java.util.ArrayList;
import java.util.List;

public class DunamuJobNormalizer implements RawJobNormalizer {

    @Override
    public List<JobPosting> normalize(List<RawJobPosting> rawJob) {
        List<JobPosting> jobPostings = new ArrayList<>();

        for (RawJobPosting rawJobPosting : rawJob) {
            JobPosting jobPosting = new JobPosting();
            jobPosting.setJobCompany(rawJobPosting.getJobCompany());
            jobPosting.setJobTitle(rawJobPosting.getJobTitle());
            jobPosting.setJobFamily(rawJobPosting.getJobFamily());
            jobPosting.setJobType(rawJobPosting.getJobType());
            jobPosting.setJobUrl(rawJobPosting.getJobUrl());
            jobPosting.setJobOptionalInformation(rawJobPosting.getJobOptionalInformation());
            jobPosting.setJobIdentity(jobPosting.generateUniqueId());

            jobPostings.add(jobPosting);
        }

        return jobPostings;
    }
}
