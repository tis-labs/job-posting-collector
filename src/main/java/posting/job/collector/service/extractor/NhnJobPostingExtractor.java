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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import posting.job.collector.domain.JobPosting;
import posting.job.collector.util.JobPostingUtil;


@AllArgsConstructor
public class NhnJobPostingExtractor {
    private final String url;


    public String extract() throws Exception {
        List<JobPosting> jobPostings = crawlNhnCareers();
        return JobPostingUtil.convertToJson(jobPostings);
    }

    private List<JobPosting> crawlNhnCareers() throws Exception {
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

        // 반복 처리: 각 구인 공고 블록 선택
        Elements jobElements = document.select("div[role=presentation]"); // 각 JobPosting 컨테이너 선택
        for (Element jobElement : jobElements) {
            JobPosting job = new JobPosting();

            // 제목 추출
            Element titleElement = jobElement.selectFirst("h4 span");
            if (titleElement != null) {
                job.setTitle(titleElement.text());
                Pattern pattern = Pattern.compile("\\[(.*?)]");
                Matcher matcher = pattern.matcher(titleElement.text());
                if (matcher.find()) {
                    job.setCompany(matcher.group(1).trim());
                }
            }

            // 상세 정보 추출
            Element detailsContainer = jobElement.selectFirst(".flex.items-center.gap-x-8");
            if (detailsContainer != null) {
                Elements detailSpans = detailsContainer.select("span"); // span 태그만 선택
                if (detailSpans.size() > 0) job.setJobCategory(detailSpans.get(0).text());
                if (detailSpans.size() > 1) job.setJobRole(detailSpans.get(1).text());
                if (detailSpans.size() > 2) job.setCareerLevel(detailSpans.get(2).text());
                if (detailSpans.size() > 3) job.setEmploymentType(detailSpans.get(3).text());
                if (detailSpans.size() > 4) job.setPeriod(detailSpans.get(4).text());
            }

            if(JobPostingUtil.isValidJobPosting(job)) {
                jobPostings.add(job);
            }
        }

//
        return jobPostings;
    }

    private String extractId(String href) {
//        if (href != null && href.contains("/jobs/")) {
//            return href.replaceAll(".*/jobs/([^?]+).*", "$1");
//        }
        return "";
    }



}
