package posting.job.collector.service.extractor;


import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import posting.job.collector.domain.JobPosting;
import posting.job.collector.util.JobPostingUtil;


@AllArgsConstructor
public class KakaoJobPostingExtractor {
    private final String url;

    public String extract() throws Exception {
        List<JobPosting> jobPostings = crawlKakaoCareers();
        return JobPostingUtil.convertToJson(jobPostings);
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


        String jobCategory = null;

        Elements contJob = document.select("main.cont_job");
        Elements elements = contJob.select("ul.tab_job li");
        for (Element element : elements) {
            if (element.hasClass("cursor_hand") && element.hasClass("on") && jobCategory == null) {
                String jobType = element.select(".txt_tab").text();  // 테크 문구 가져오기
                jobCategory = jobType;
            }
        }
        Elements Jobs = contJob.select("ul.list_jobs a");
        for (Element card: Jobs){
            JobPosting job = new JobPosting();
            job.setJobCategory(jobCategory);

            String href = card.attr("href");
            job.setJobDetailUrl(href);

            job.setId(extractId(href));

            String title = card.select("h4.tit_jobs").text();
            job.setTitle(title);


            String regex = "\\(([^)]*)\\)$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(title);
            while (matcher.find()){
                job.setCareerLevel( matcher.group(1));

            }

            Elements JobRoleElements = card.select("span.link_tag.cursor_hand.false");

            for (Element jobRoleElement : JobRoleElements) {
                String jobRole = jobRoleElement.ownText().replace("#", "").trim();
                job.setJobRole(jobRole);
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

            if(JobPostingUtil.isValidJobPosting(job)) {
                jobPostings.add(job);
            }


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
