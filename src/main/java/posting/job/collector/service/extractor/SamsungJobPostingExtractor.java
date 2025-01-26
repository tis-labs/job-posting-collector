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
public class SamsungJobPostingExtractor {
    private final String url;

    public String extract() throws Exception {
        List<JobPosting> jobPostings = crawlSamsungCareers();
        return convertToJson(jobPostings);
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
                job.setDepartment(departmentBuilder.toString());
            }

            // 고용 형태 (경력 또는 기타)
            Element careerLevelElement = jobItem.selectFirst(".info span");
            if (careerLevelElement != null) {
                job.setCareerLevel(careerLevelElement.text().trim());
            }

            // 위치 (없을 경우 기본값 설정, 예시 HTML에서 위치 정보 없음)
            job.setLocation("위치 정보 없음"); // 위치 정보 없으므로 기본값을 설정

            // 필드 (없을 경우 기본값 설정, 예시 HTML에서 필드 정보 없음)
            job.setField("필드 정보 없음"); // 필드 정보 없으므로 기본값을 설정


            Element jobDetailLink = jobItem.selectFirst("a");
            if (jobDetailLink != null) {
                job.setJobDetailUrl(jobDetailLink.absUrl("href"));
            }

            jobPostings.add(job);
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