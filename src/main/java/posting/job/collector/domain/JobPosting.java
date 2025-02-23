package posting.job.collector.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
public class JobPosting {
    private String jobIdentity ; //식별자
    private String jobCompany; //회사명
    private String jobTitle; //회사명
    private String jobFamily; //직군 (분야) 예시) 개발자, 디자이너,기획자
    private String jobType; //직종 예시) 백엔드 개발자, 프론트엔드 개발자, 풀스택
    private String jobUrl;
    private Map<String, String> jobOptionalInformation; //부가정보


    @Override
    public String toString() {
        return "JobPosting {" +
                "jobIdentity='" + jobIdentity + '\'' +
                ", jobCompany='" + jobCompany + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", jobFamily='" + jobFamily + '\'' +
                ", jobType='" + jobType + '\'' +
                ", jobUrl='" + jobUrl + '\'' +
                ", jobOptionalInformation=" + jobOptionalInformation +
                '}';
    }

    public String generateUniqueId() {
//        return jobCompany + jobTitle;
        if (jobTitle != null && jobCompany != null && !jobTitle.isEmpty() && !jobCompany.isEmpty()) {
            return new StringBuilder(jobCompany).append(jobTitle).toString();
        }
        return "";
    }

}

