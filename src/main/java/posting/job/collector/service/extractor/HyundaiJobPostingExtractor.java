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

import java.util.*;

import posting.job.collector.domain.JobFamily;
import posting.job.collector.domain.JobPosting;
import posting.job.collector.domain.RawJobPosting;
import posting.job.collector.service.normalizer.HyundaiJobNormalizer;
import posting.job.collector.util.JobPostingUtil;


@AllArgsConstructor
public class HyundaiJobPostingExtractor {
    private final String url;

    public String extract() throws Exception {
        List<RawJobPosting> rawJobPostings = crawlHyundaiCareers();
        List<JobPosting> jobPostings = new HyundaiJobNormalizer().normalize(rawJobPostings);
        return JobPostingUtil.convertToJson(jobPostings);
    }

    private List<RawJobPosting> crawlHyundaiCareers() throws Exception {
        List<RawJobPosting> rawJobPostings = new ArrayList<>();
//        WebDriverManager.chromedriver().driverVersion("113.0.5672.126").setup();
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");

        WebDriver driver = new ChromeDriver(options);
        driver.get(url);

        Thread.sleep(5000); // 5초 대기, 필요시 더 길게 조정
        String pageSource = driver.getPageSource();
        driver.quit();
        Document document = Jsoup.parse(pageSource);

        // li 태그 반복
        Elements jobItems = document.select("li"); // li 태그로 반복

        for (Element jobItem : jobItems) {
            RawJobPosting rawJobPosting = new RawJobPosting();

            // 제목 가져오기
            Element titleElement = jobItem.selectFirst("strong");
            if (titleElement == null || titleElement.text().trim().isEmpty()) {
                continue; // title이 없으면 해당 객체는 건너뜁니다.
            }
            rawJobPosting.setJobTitle(titleElement.text().trim());

            // 부서 정보 (예: #IT, #Security Engineering)
            Elements departmentElements = jobItem.select("span[data-type='sec'], span[data-type='fld']");
            StringBuilder departmentBuilder = new StringBuilder();
            for (Element department : departmentElements) {
                String departmentText = department.text().trim();
                if (!departmentText.isEmpty()) {
                    if (departmentBuilder.length() > 0) {
                        departmentBuilder.append(" | ");
                    }
                    departmentBuilder.append(departmentText);
                }
            }
            if (departmentBuilder.length() > 0) {
                rawJobPosting.setJobType(departmentBuilder.toString());
            }


            // 채용 기간 (예: 채용시까지)
            Element periodElement = jobItem.selectFirst(".d__day");
            if (periodElement != null) {
                //jobOptionalInformation
                Map<String, String> jobOptionalInformation = new HashMap<>();
                jobOptionalInformation.put("jobPeriod",periodElement.text().trim());
                rawJobPosting.setJobOptionalInformation(jobOptionalInformation);
            }

            // 필드 정보 (예: #IT)
            Elements fieldElements = jobItem.select("span[data-type='sec']");
            StringBuilder fieldBuilder = new StringBuilder();
            for (Element field : fieldElements) {
                String fieldText = field.text().trim();
                if (!fieldText.isEmpty()) {
                    if (fieldBuilder.length() > 0) {
                        fieldBuilder.append(" | ");
                    }
                    fieldBuilder.append(fieldText);
                }
            }
            if (fieldBuilder.length() > 0) {
                rawJobPosting.setJobFamily(JobFamily.normalize(fieldBuilder.toString().replace("#", "")));
            }



            // 상세 URL
            Element jobDetailLink = jobItem.selectFirst("a");
            if (jobDetailLink != null) {
                rawJobPosting.setJobUrl( url + jobDetailLink.absUrl("href"));
            }

            rawJobPosting.setJobCompany("HYUNDAI");
            rawJobPostings.add(rawJobPosting);
        }
        return rawJobPostings;
    }



}