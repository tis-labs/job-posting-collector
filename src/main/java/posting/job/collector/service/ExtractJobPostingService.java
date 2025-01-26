package posting.job.collector.service;

import org.springframework.stereotype.Service;
import posting.job.collector.configuration.TargetSource;
import posting.job.collector.service.extractor.*;

@Service
public class ExtractJobPostingService {
    private final UrlDecoder urlDecoder;  // UrlDecoder를 주입받기 위해 필드 추가

    public ExtractJobPostingService(UrlDecoder urlDecoder) {
        this.urlDecoder = urlDecoder;
    }
    public String execute(TargetSource item) throws Exception {
        return switch (item) {
            case NAVER -> new NaverJobPostingExtractor(item.getUrl()).extract();
            case NHN ->  new NhnJobPostingExtractor(item.getUrl()).extract();
            case KAKAO -> new KakaoJobPostingExtractor(item.getUrl(), urlDecoder).extract();
            case COUPANG -> new CoupangPostingExtractor(item.getUrl()).extract();
            case WOOWAHAN -> new WoowahanJobPostingExtractor(item.getUrl()).extract();
            case SOCARCORP -> new SocarCorpJobPostingExtractor(item.getUrl()).extract();
            case TOSS -> new TossJobPostingExtractor(item.getUrl()).extract();
            case DUNAMU -> new DunamuJobPostingExtractor(item.getUrl()).extract();
            case HYUNDAI -> new HyundaiJobPostingExtractor(item.getUrl()).extract();
            case SAMSUNG -> new SamsungJobPostingExtractor(item.getUrl()).extract();

        };
    }
}
