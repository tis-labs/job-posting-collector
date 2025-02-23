package posting.job.collector.service.extractor;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import posting.job.collector.domain.JobPosting;
import posting.job.collector.domain.RawJobPosting;
import posting.job.collector.domain.JobType;
import posting.job.collector.service.normalizer.TossJobNormalizer;
import posting.job.collector.util.JobPostingUtil;

@AllArgsConstructor
public class TossJobPostingExtractor {
    private final String url;

    public String extract() throws Exception {
        List<RawJobPosting> rawJobPostings = crawlTossCareers();
        List<JobPosting> jobPostings = new TossJobNormalizer().normalize(rawJobPostings);
        return JobPostingUtil.convertToJson(jobPostings);
    }

    private List<RawJobPosting> crawlTossCareers() throws Exception {
        List<RawJobPosting> rawJobPostings = new ArrayList<>();
//        WebDriverManager.chromedriver().driverVersion("131.0.6778.267").setup();
        WebDriverManager.chromedriver().setup();
        //브라우저 숨김처리
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");

        WebDriver driver = new ChromeDriver(options);
        // Selenium을 사용해 동적으로 페이지 로드
        driver.get(url);

        // 페이지 로딩 대기 (필요한 경우 WebDriverWait을 사용해 로딩을 기다릴 수 있음)
        Thread.sleep(5000); // 5초 대기, 필요시 더 길게 조정
        // 페이지 HTML 가져오기
        String pageSource = driver.getPageSource();
        driver.quit();
        Document document = Jsoup.parse(pageSource);

        // 여러 개의 채용 항목을 찾기 위해 select() 사용
        Elements jobElements = document.select("div.css-g65o95");

        for (Element jobElement : jobElements) {
            RawJobPosting rawJobPosting = new RawJobPosting();

            // Job Detail URL
            Element jobLink = jobElement.selectFirst("div[href]");
            if (jobLink != null) {
                rawJobPosting.setJobUrl(url + jobLink.attr("href"));
            }

            // Title
            Element titleElement = jobElement.selectFirst(".typography--h5.typography--bold");
            if (titleElement != null) {
                rawJobPosting.setJobTitle(titleElement.text());
            }

            // Department (부서 정보) - 첫 번째 'SAP' 값을 추출
            Element departmentElement = jobElement.selectFirst(".typography--p.typography--regular");
            if (departmentElement != null) {
                String[] departmentParts = departmentElement.text().split(" ・ ");
                if (departmentParts.length > 0) {
                    rawJobPosting.setJobType(JobType.normalize(departmentParts[0]));  // 첫 번째 값(SAP)을 부서로 설정
                }
            }

            // Field (기술 스택 등) - 여기에 대해선 그대로
//            job.setField(departmentElement != null ? departmentElement.text() : null);

            // Career Level (경력 정보) - "(2년 이상)" 텍스트 추출
//            String careerLevel = extractCareerLevel(rawJobPosting.getTitle());
            Map<String, String> optional = new HashMap<>();
            optional.put("jobCareerLevel", rawJobPosting.getJobTitle());
            rawJobPosting.setJobOptionalInformation(optional);


            // Period (채용 기간) - "~1/30" 정보 추출
//            String period = extractPeriod(rawJobPosting.getTitle());
            optional.put("jobPeriod", rawJobPosting.getJobTitle());
            rawJobPosting.setJobOptionalInformation(optional);

                // Company (회사명) - 'Toss'로 설정
                rawJobPosting.setJobCompany("Toss");
                rawJobPostings.add(rawJobPosting);
        }

        return rawJobPostings;


    }

    // Career Level을 제목에서 추출하는 메소드
//    private String extractCareerLevel(String title) {
//        if (title != null && title.contains("년 이상")) {
//            return title.substring(title.indexOf("(") + 1, title.indexOf("년 이상")).trim();
//        }
//        return null;
//    }

    // Period를 제목에서 추출하는 메소드
//    private String extractPeriod(String title) {
//        if (title != null && title.contains("~")) {
//            return title.substring(title.indexOf("~") + 1).trim();
//        }
//        return null;
//    }



}