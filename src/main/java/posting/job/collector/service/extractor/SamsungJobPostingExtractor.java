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
public class SamsungJobPostingExtractor {
    private final String url;

    public String extract() throws Exception {
        List<JobPosting> jobPostings = crawlSamsungCareers();
        return JsonUtil.convertToJson(jobPostings);
    }

    private List<JobPosting> crawlSamsungCareers() throws Exception {
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
        Elements jobItems = document.select("li"); // li 태그로 반복

        for (Element jobItem : jobItems) {
            JobPosting job = new JobPosting();

            // 회사명 (p 태그 내의 회사명 정보)
            Element companyElement = jobItem.selectFirst(".company");
            if (companyElement != null) {
                job.setCompany(companyElement.text().trim());
            }

            // 제목 (h3 태그 내의 제목 정보)
            Element titleElement = jobItem.selectFirst(".title");
            if (titleElement != null) {
                job.setTitle(titleElement.text().trim());
            }

            // 기간 (p 태그 내의 period 클래스)
            Element periodElement = jobItem.selectFirst(".period");
            if (periodElement != null) {
                job.setPeriod(periodElement.text().trim());
            }

            // 부서 정보 (flagWrap 내의 flag 클래스들에서 "[] 안의" 값들 추출)
            Elements departmentElements = jobItem.select(".flagWrap .flag.grey");
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

            // 고용 형태 (경력 또는 기타)
            Element careerLevelElement = jobItem.selectFirst(".info span");
            if (careerLevelElement != null) {
                job.setCareerLevel(careerLevelElement.text().trim());
            }



            Element jobDetailLink = jobItem.selectFirst("a");
            if (jobDetailLink != null) {
                job.setJobDetailUrl(jobDetailLink.absUrl("href"));
            }

            jobPostings.add(job);
        }


        return jobPostings;
    }


}