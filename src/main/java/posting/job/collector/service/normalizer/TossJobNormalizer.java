package posting.job.collector.service.normalizer;

import posting.job.collector.domain.CrawledJobPosting;
import posting.job.collector.domain.RawJobPosting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TossJobNormalizer implements RawJobNormalizer{
    @Override
    public List<CrawledJobPosting> normalize(List<RawJobPosting> rawJobPostings) {
        List<CrawledJobPosting> crawledJobPostings = new ArrayList<>();

        for (RawJobPosting rawJobPosting : rawJobPostings) {
            CrawledJobPosting crawledJobPosting = new CrawledJobPosting();
            crawledJobPosting.setJobCompany(rawJobPosting.getJobCompany());
            crawledJobPosting.setJobTitle(rawJobPosting.getJobTitle());
//            crawledJobPosting.setJobFamily(rawJobPosting.getJobFamily());
            // jobFamily 변환 (내부 Enum 사용)
            String normalizedJobFamily = JobFamily.normalize(rawJobPosting.getJobFamily());
            crawledJobPosting.setJobFamily(normalizedJobFamily);
            crawledJobPosting.setJobType(rawJobPosting.getJobType());
            crawledJobPosting.setJobUrl(rawJobPosting.getJobUrl());
            crawledJobPosting.setJobOptionalInformation(rawJobPosting.getJobOptionalInformation());
            crawledJobPosting.setJobIdentity(crawledJobPosting.generateUniqueId());
            crawledJobPosting.setCrawledAt(crawledJobPosting.generateCrawledAt());
            crawledJobPosting.setCrawledJobId(crawledJobPosting.generateCrawledJobId());

            crawledJobPostings.add(crawledJobPosting);
        }

        return crawledJobPostings;
    }


    /**
     * 변환을 위한 내부 Enum
     */
    private enum JobFamily {
        TECH("Engineering", "Tech");
//        DESIGN("Design", "Creative"),
//        MARKETING("Marketing", "Business"),
//        SALES("Sales", "Business");

        private static final Map<String, String> JOB_FAMILY_MAP = Stream.of(values())
                .collect(Collectors.toMap(JobFamily::getRaw, JobFamily::getNormalized));

        private final String raw;
        private final String normalized;

        JobFamily(String raw, String normalized) {
            this.raw = raw;
            this.normalized = normalized;
        }

        public String getRaw() {
            return raw;
        }

        public String getNormalized() {
            return normalized;
        }

        /**
         * 주어진 jobFamily를 변환 (없으면 원본 유지)
         */
        public static String normalize(String jobFamily) {
            return JOB_FAMILY_MAP.getOrDefault(jobFamily, jobFamily);
        }
    }
}
