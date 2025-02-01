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
import posting.job.collector.util.JsonUtil;


@AllArgsConstructor
public class DunamuJobPostingExtractor {
    private final String url;

    public String extract() throws Exception {
        List<JobPosting> jobPostings = crawlDunamuCareers();
        return JsonUtil.convertToJson(jobPostings);
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
                job.setJobRole(departmentElement.text());
            }

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



}