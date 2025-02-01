package posting.job.collector.service.extractor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import posting.job.collector.domain.JobPosting;
import posting.job.collector.domain.JobPostingResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class WoowahanJobPostingExtractor {
    private final String url;

    public String extract() throws Exception {
        List<JobPosting> jobPostings = crawlWoowahanCareers();
        return convertToJson(jobPostings);
    }

    private List<JobPosting> crawlWoowahanCareers() throws Exception {
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
        // 반복 처리: 각 JobPosting 블록 선택
        Elements jobElements = document.select("li");
        for (Element jobElement : jobElements) {
            JobPosting job = new JobPosting();

            // Job ID 추출
            Element linkElement = jobElement.selectFirst("a.title");
            if (linkElement != null) {
                String jobUrl = linkElement.attr("href");
                job.setJobDetailUrl(jobUrl);

                // ID는 URL에서 추출
                String[] urlParts = jobUrl.split("/");
                if (urlParts.length > 2) {
                    job.setId(urlParts[urlParts.length - 2]);
                }
            }

            // Title 및 Department 추출
            Element titleElement = jobElement.selectFirst("p.fr-view");
            if (titleElement != null) {
                String titleText = titleElement.text();
                job.setTitle(titleText);

                // 부서 정보 추출
                String department = extractDepartmentFromTitle(titleText);
                job.setJobCategory(department);
            }

            // Career Level 추출
            Element careerElement = jobElement.selectFirst("span.flag-career");
            if (careerElement != null) {
                job.setCareerLevel(careerElement.text());
            }

            // Employment Type 및 Period 추출
            Elements typeElements = jobElement.select("div.flag-type span");
            if (typeElements.size() > 0) {
                job.setEmploymentType(typeElements.get(0).text()); // "정규직"
            }
            if (typeElements.size() > 1) {
                job.setPeriod(typeElements.get(1).text().trim()); // "영입 종료시"
            }

            // Tags (Field) 추출
            Elements tagElements = jobElement.select("div.flag-tag button");
            if (tagElements != null && !tagElements.isEmpty()) {
                List<String> tags = new ArrayList<>();
                for (Element tagElement : tagElements) {
                    tags.add(tagElement.text().replace("#", "").trim());
                }

                if (!tags.isEmpty()) {
                    job.setJobRole(String.join(", ", tags));
                } else {
                    job.setJobRole(null); // 값이 없으면 null로 설정

                }

            } else {
                job.setJobRole(null); // 태그 자체가 없을 경우 null로 설정

            }


            // JobPosting 객체를 리스트에 추가
            jobPostings.add(job);
        }

        return jobPostings;
    }

    // Title에서 Department 정보 추출
    private static String extractDepartmentFromTitle(String title) {
        String department = "";

        // 괄호 안의 내용 추출
        int start = title.indexOf("[");
        int end = title.indexOf("]");
        if (start >= 0 && end > start) {
            department = title.substring(start + 1, end).trim();
        }

        return department;
    }

    private String convertToJson(List<JobPosting> jobPostings) {
        JobPostingResult result = new JobPostingResult(jobPostings, jobPostings.size(), LocalDate.now().toString());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(result);
    }



}