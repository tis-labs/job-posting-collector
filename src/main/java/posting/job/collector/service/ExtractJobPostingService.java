package posting.job.collector.service;

import org.springframework.stereotype.Service;
import posting.job.collector.configuration.TargetSource;
import posting.job.collector.domain.CrawledJobPosting;
import posting.job.collector.service.extractor.*;

import java.util.List;

@Service
public class ExtractJobPostingService {


    public List<CrawledJobPosting> execute(TargetSource item) throws Exception {
        return switch (item) {
            case NAVER -> new NaverJobPostingExtractor(item.getUrl()).extract();
            case TOSS -> new TossJobPostingExtractor(item.getUrl()).extract();
//            case NHN ->  new NhnJobPostingExtractor(item.getUrl()).extract();
//            case KAKAO -> new KakaoJobPostingExtractor(item.getUrl()).extract();
//            case WOOWAHAN -> new WoowahanJobPostingExtractor(item.getUrl()).extract();
//            case SOCARCORP -> new SocarCorpJobPostingExtractor(item.getUrl()).extract();

//            case DUNAMU -> new DunamuJobPostingExtractor(item.getUrl()).extract();
//            case HYUNDAI -> new HyundaiJobPostingExtractor(item.getUrl()).extract();

        };
    }
}
