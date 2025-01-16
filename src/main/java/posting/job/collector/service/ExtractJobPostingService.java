package posting.job.collector.service;

import org.springframework.stereotype.Service;
import posting.job.collector.configuration.TargetSource;
import posting.job.collector.service.extractor.NaverJobPostingExtractor;

@Service
public class ExtractJobPostingService {
    public String execute(TargetSource item) throws Exception {
        return switch (item) {
            case NAVER -> new NaverJobPostingExtractor(item.getUrl()).extract();
            case NHN -> "NHN";
            case KAKAO -> "KAKAO";
        };
    }
}
