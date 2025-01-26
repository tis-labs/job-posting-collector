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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import posting.job.collector.service.UrlDecoder;

@AllArgsConstructor
public class KakaoJobPostingExtractor {
    private final String url;

    @Autowired  // UrlDecoder 주입
    private UrlDecoder urlDecoder;

    public String extract() throws Exception {
        List<JobPosting> jobPostings = crawlKakaoCareers();
        return convertToJson(jobPostings);
    }

    private List<JobPosting> crawlKakaoCareers() throws Exception {
        List<JobPosting> jobPostings = new ArrayList<>();
        WebDriverManager.chromedriver().driverVersion("131.0.6778.267").setup();
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
        Elements Jobs = document.select("ul.list_jobs a");
        for (Element card: Jobs){
            JobPosting job = new JobPosting();

            String href = card.attr("href");
            String decordedUrl = urlDecoder.decodeUrl(href);
            job.setJobDetailUrl("https://careers.kakao.com"+decordedUrl);

            job.setId(extractId(href));

            String title = card.select("h4.tit_jobs").text();
            job.setTitle(title);


            String regex = "\\(([^)]*)\\)$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(title);
            while (matcher.find()){
                job.setCareerLevel( matcher.group(1));

            }

            Elements departmentElements = document.select("span.link_tag");

            for (Element departmentElement : departmentElements) {
                String department = departmentElement.ownText().replace("#", "").trim();
                job.setDepartment(department);
            }


            // 영입 마감일 정보
            Elements infoElements = card.select("dl.list_info");
            for (Element infoElement : infoElements) {
                String hiringDeadline = infoElement.select("dt:contains(영입마감일) + dd").text();
                if (!hiringDeadline.isEmpty()) {
                    job.setPeriod(hiringDeadline);
                }
            }

//
            // 직원 유형 정보 추출
            Elements employmentTypeElements = card.select("dl.item_subinfo");
            for (Element employmentTypeElement : employmentTypeElements) {
                if (employmentTypeElement.select("dt:contains(직원유형)").size() > 0) {
                    String employmentType = employmentTypeElement.select("dd").text();
                    job.setEmploymentType(employmentType);
                }
            }

            // 회사 정보
            Elements companyInfoElements = card.select("dl.item_subinfo");
            for (Element companyInfoElement : companyInfoElements) {
                if (companyInfoElement.select("dt:contains(회사정보)").size() > 0) {
                    String company = companyInfoElement.select("dd").text();
                    job.setCompany(company);
                }
            }

            jobPostings.add(job);


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
