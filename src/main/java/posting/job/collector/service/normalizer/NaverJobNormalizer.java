package posting.job.collector.service.normalizer;

import posting.job.collector.domain.CrawledJobPosting;
import posting.job.collector.domain.RawJobPosting;

import java.util.ArrayList;
import java.util.List;

public class NaverJobNormalizer implements RawJobNormalizer{

    private static final String NAVER_JOB_DETAIL_URL = "https://recruit.navercorp.com/rcrt/view.do?annoId=";
    @Override
    public List<CrawledJobPosting> normalize(List<RawJobPosting> rawJobPostings) {
        List<CrawledJobPosting> crawledJobPostings = new ArrayList<>();

        for (RawJobPosting rawJobPosting : rawJobPostings) {
            CrawledJobPosting crawledJobPosting = new CrawledJobPosting();
            crawledJobPosting.setJobCompany(getCompanyName(rawJobPosting.getJobCompany()));
            crawledJobPosting.setJobTitle(rawJobPosting.getJobTitle());
            crawledJobPosting.setJobFamily(rawJobPosting.getJobFamily());
            crawledJobPosting.setJobType(rawJobPosting.getJobType());
            crawledJobPosting.setJobUrl(NAVER_JOB_DETAIL_URL+ extractId(rawJobPosting.getJobUrl()));
            crawledJobPosting.setJobOptionalInformation(rawJobPosting.getJobOptionalInformation());
            crawledJobPosting.setJobIdentity(crawledJobPosting.generateUniqueId());
            crawledJobPosting.setCrawledAt(crawledJobPosting.generateCrawledAt());
            crawledJobPosting.setCrawledJobId(crawledJobPosting.generateCrawledJobId());

            crawledJobPostings.add(crawledJobPosting);
        }

        return crawledJobPostings;
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
