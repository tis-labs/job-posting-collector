package posting.job.collector.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobPosting {
    private String id;
    private String title;
    private String jobCategory; //직종 (분야) 예시) 개발, 디자인, 마케팅
    private String jobRole; //직무 예시) 백엔드, 프론트엔드, 풀스택
    private String careerLevel;
    private String employmentType;
    private String period;
    private String company;
    private String jobDetailUrl;
}

