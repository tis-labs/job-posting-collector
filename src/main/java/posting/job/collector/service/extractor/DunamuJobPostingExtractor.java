package posting.job.collector.service.extractor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class DunamuJobPostingExtractor {
    private final String url;

    public String extract() throws Exception {
        List<JobPosting> jobPostings = crawlDunamuCareers();
        return convertToJson(jobPostings);
    }

    private List<JobPosting> crawlDunamuCareers() throws Exception {
        List<JobPosting> jobPostings = new ArrayList<>();
        WebDriverManager.chromedriver().driverVersion("131.0.6778.267").setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");

        WebDriver driver = new ChromeDriver(options);
        driver.get(url);

        Thread.sleep(5000);
        String pageSource = driver.getPageSource();
        driver.quit();
        Document document = Jsoup.parse(pageSource);
        Elements jobElements = document.select("a[href^='/careers/jobs/']");

        for (Element jobElement : jobElements) {
            JobPosting job = new JobPosting();

            // Job Detail URL
            String jobDetailUrl = jobElement.attr("href");
            job.setJobDetailUrl(jobDetailUrl);

            // Title (직무명)
            Element titleElement = jobElement.selectFirst("p");
            if (titleElement != null) {
                job.setTitle(titleElement.text());
            }

            // Department (부서) - <em> 태그에서 값 가져오기
            Element departmentElement = jobElement.selectFirst("em");
            if (departmentElement != null) {
                job.setDepartment(departmentElement.text());
            }

            // Field (기술 스택 정보) - 이 예시에서는 department와 같은 값으로 설정
            job.setField(departmentElement != null ? departmentElement.text() : null);

            // Company (회사명) - 회사명은 DUNAMU로 고정
            job.setCompany("DUNAMU");

            jobPostings.add(job); // 리스트에 JobPosting 객체 추가
        }

        return jobPostings;
    }

    private String extractId(String href) {
        if (href != null && href.contains("/jobs/")) {
            return href.replaceAll(".*/jobs/([^?]+).*", "$1");
        }
        return "";
    }

    private String convertToJson(List<JobPosting> jobPostings) {
        JobPostingResult result = new JobPostingResult(jobPostings, jobPostings.size(), LocalDate.now().toString());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(result);
    }

    @Getter
    @AllArgsConstructor
    private static class JobPostingResult {
        private List<JobPosting> jobs;
        private int totalCount;
        private String lastUpdated;
    }

    @Getter
    @Setter
    private static class JobPosting {
        private String id;
        private String title;
        private String department;
        private String field;
        private String careerLevel;
        private String employmentType;
        private String period;
        private String company;
        private String jobDetailUrl;
    }
}