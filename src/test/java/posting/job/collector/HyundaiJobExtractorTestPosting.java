//package posting.job.collector;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import posting.job.collector.configuration.TargetSource;
//import posting.job.collector.service.extractor.HyundaiJobPostingExtractor;
//
//
//@SpringBootTest
//public class HyundaiJobExtractorTestPosting {
//    @Test
//    void testExtractJobPostings() throws Exception {
//
//        String testUrl = TargetSource.HYUNDAI.getUrl();
//
//        // HyundaiJobPostingExtractor 객체 생성
//        HyundaiJobPostingExtractor extractor = new HyundaiJobPostingExtractor(testUrl);
//
//        // 크롤링한 데이터 추출
//        String extractedData = extractor.extract();
//
//        // 추출된 데이터 출력
//        System.out.println(extractedData);
//
//        // 추출된 데이터 검증 (예시)
//        Assertions.assertNotNull(extractedData);
//        Assertions.assertTrue(extractedData.contains("JobPosting")); // 추출된 데이터에 "JobPosting"이 포함되어 있는지 확인
//
//    }
//
//}
