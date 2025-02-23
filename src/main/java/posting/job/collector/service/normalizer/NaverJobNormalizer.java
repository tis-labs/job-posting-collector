package posting.job.collector.service.normalizer;

import posting.job.collector.domain.JobPosting;
import posting.job.collector.domain.RawJobPosting;

import java.util.ArrayList;
import java.util.List;

public class NaverJobNormalizer implements RawJobNormalizer{

    private static final String NAVER_JOB_DETAIL_URL = "https://recruit.navercorp.com/rcrt/view.do?annoId=";
    @Override
    public List<JobPosting> normalize(List<RawJobPosting> rawJobPostings) {
        List<JobPosting> jobPostings = new ArrayList<>();

        for (RawJobPosting rawJobPosting : rawJobPostings) {
            JobPosting jobPosting = new JobPosting();
            jobPosting.setJobCompany(getCompanyName(rawJobPosting.getJobCompany()));
            jobPosting.setJobTitle(rawJobPosting.getJobTitle());
            jobPosting.setJobFamily(rawJobPosting.getJobFamily());
            jobPosting.setJobType(rawJobPosting.getJobType());
            jobPosting.setJobUrl(NAVER_JOB_DETAIL_URL+ extractId(rawJobPosting.getJobUrl()));
            jobPosting.setJobOptionalInformation(rawJobPosting.getJobOptionalInformation());
            jobPosting.setJobIdentity(jobPosting.generateUniqueId());

            jobPostings.add(jobPosting);
        }

        return jobPostings;
    }

    private String extractId(String onclick) {
        if (onclick != null && onclick.contains("show(")) {
            return onclick.replaceAll("[^0-9]", "");
        }
        return "";
    }

        private String getCompanyName(String className) {
        return switch (className) {
            case "snow" -> "SNOW";
            case "nfin" -> "NAVER FINANCIAL";
            case "navercloud" -> "NAVER Cloud";
            case "webtoon" -> "NAVER WEBTOON";
            default -> className;
        };
    }



}
