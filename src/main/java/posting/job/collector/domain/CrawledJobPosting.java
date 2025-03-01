package posting.job.collector.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrawledJobPosting {
    private String crawledJobId; //크롤링된 식별자 : jobIdentity+ crawledAt
    private LocalDate crawledAt; // JobPosting 생성 시점에 담고
    private String jobIdentity ; //식별자
    private String jobCompany; //회사명
    private String jobTitle; //회사명
    private String jobFamily; //직군 (분야) 예시) 개발자, 디자이너,기획자
    private String jobType; //직종 예시) 백엔드 개발자, 프론트엔드 개발자, 풀스택
    private String jobUrl;
    private Map<String, String> jobOptionalInformation;//부가정보



    @Override
    public String toString() {
        return  "jobIdentity : " + jobIdentity   + "\n"  +
                "jobCompany : " + jobCompany   + "\n" +
                "jobTitle : " + jobTitle  + "\n"  +
                "jobFamily : " + jobFamily   +"\n"  +
                "jobType : " + jobType   + "\n" +
                "jobUrl : " + jobUrl   + "\n"  +
                "jobOptionalInformation : " + jobOptionalInformation + "\n"
                ;
    }

    public String generateUniqueId() {
        if (jobTitle != null && jobCompany != null && !jobTitle.isEmpty() && !jobCompany.isEmpty()) {
            return new StringBuilder(jobCompany).append(jobTitle).toString();
        }
        return "";
    }

    public LocalDate generateCrawledAt() {
        LocalDate today = LocalDate.now();
        return today;
    }

    public String generateCrawledJobId() {
        if (jobIdentity != null &&  !jobIdentity.isEmpty() && crawledAt != null && !crawledAt.toString().isEmpty()) {
            return new StringBuilder(jobIdentity).append(crawledAt).toString();
        }
        return "";
    }

}

