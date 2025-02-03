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
import java.util.List;

import posting.job.collector.domain.JobPosting;
import posting.job.collector.util.JobPostingUtil;


@AllArgsConstructor
public class HyundaiJobPostingExtractor {
    private final String url;

    public String extract() throws Exception {
        List<JobPosting> jobPostings = crawlHyundaiCareers();
        return JobPostingUtil.convertToJson(jobPostings);
    }

    private List<JobPosting> crawlHyundaiCareers() throws Exception {
        List<JobPosting> jobPostings = new ArrayList<>();
        WebDriverManager.chromedriver().driverVersion("131.0.6778.267").setup();
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
            JobPosting job = new JobPosting();

            // 제목 가져오기
            Element titleElement = jobItem.selectFirst("strong");
            if (titleElement == null || titleElement.text().trim().isEmpty()) {
                continue; // title이 없으면 해당 객체는 건너뜁니다.
            }
            job.setTitle(titleElement.text().trim());

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
                job.setJobRole(departmentBuilder.toString());
            }


            // 채용 기간 (예: 채용시까지)
            Element periodElement = jobItem.selectFirst(".d__day");
            if (periodElement != null) {
                job.setPeriod(periodElement.text().trim());
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
                job.setJobCategory(fieldBuilder.toString());
            }



            // 상세 URL
            Element jobDetailLink = jobItem.selectFirst("a");
            if (jobDetailLink != null) {
                job.setJobDetailUrl(jobDetailLink.absUrl("href"));
            }

            if(JobPostingUtil.isValidJobPosting(job)) {
                job.setCompany("HYUNDAI");
                jobPostings.add(job);
            }
        }
        return jobPostings;
    }



}