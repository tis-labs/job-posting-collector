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
public class SocarCorpJobPostingExtractor {
    private final String url;

    public String extract() throws Exception {
        List<JobPosting> jobPostings = crawlSocarCorpCareers();
        return convertToJson(jobPostings);
    }

    private List<JobPosting> crawlSocarCorpCareers() throws Exception {
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

        Elements jobElements = document.select(".ResultItem_CareerItem__pplVI");

        for (Element jobElement : jobElements) {
            JobPosting job = new JobPosting();

            // Job Detail URL
            Element jobLink = jobElement.selectFirst("a");
            if (jobLink != null) {
                job.setJobDetailUrl(jobLink.attr("href"));
            }

            // Title and Career Level
            Element titleElement = jobElement.selectFirst(".ResultItem_title__TW_Eq");
            if (titleElement != null) {
                String title = titleElement.select("span").text();
                String careerLevel = titleElement.select("em").text();
                job.setTitle(title);
                job.setCareerLevel(careerLevel);
            }

            // Department: 첫 번째 <span> 태그를 찾아서 부서 정보로 설정
            Element departmentElement = jobElement.selectFirst(".ResultItem_tag__N9pI_");
            if (departmentElement != null) {
                job.setDepartment(departmentElement.text()); // 부서 정보 가져오기
            }

            // Location and Employment Type
            Element typeElement = jobElement.selectFirst(".ResultItem_type__3iOgH");
            if (typeElement != null) {
                Elements typeSpans = typeElement.select("span");
                if (typeSpans.size() > 0) {
                    job.setLocation(typeSpans.get(0).text());
                    if (typeSpans.size() > 1) {
                        job.setEmploymentType(typeSpans.get(1).text()); // 두 번째 span: 고용 형태
                    }
                }
            }

            // Field (태그 정보)
            Elements fieldTags = jobElement.select(".ResultItem_tag__N9pI_");
            if (fieldTags != null && !fieldTags.isEmpty()) {
                List<String> fields = new ArrayList<>();
                for (Element tag : fieldTags) {
                    fields.add(tag.text());
                }
                job.setField(String.join(", ", fields));
            }

            // Company
            Element companyElement = jobElement.selectFirst(".ResultItem_tag__N9pI_");
            if (companyElement != null) {
                job.setCompany(companyElement.text());
            }

            jobPostings.add(job); // 리스트에 JobPosting 객체 추가
        }

        return jobPostings;
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
        private String location;
        private String field;
        private String careerLevel;
        private String employmentType;
        private String period;
        private String company;
        private String jobDetailUrl;
    }
}