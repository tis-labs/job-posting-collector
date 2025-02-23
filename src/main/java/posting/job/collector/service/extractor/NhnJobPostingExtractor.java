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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import posting.job.collector.domain.JobPosting;
import posting.job.collector.domain.RawJobPosting;
import posting.job.collector.service.normalizer.NhnJobNormalizer;
import posting.job.collector.util.JobPostingUtil;


@AllArgsConstructor
public class NhnJobPostingExtractor {
    private final String url;


    public String extract() throws Exception {
        List<RawJobPosting> rawJobPostings = crawlNhnCareers();
        List<JobPosting> jobPostings = new NhnJobNormalizer().normalize(rawJobPostings);
        return JobPostingUtil.convertToJson(jobPostings);
    }

    private List<RawJobPosting> crawlNhnCareers() throws Exception {
        List<RawJobPosting> rawJobPostings = new ArrayList<>();

//        WebDriverManager.chromedriver().driverVersion("131.0.6778.267").setup();
        WebDriverManager.chromedriver().setup();
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
        Elements jobElements = document.select("div[role=presentation]"); // 각 RawJobPosting 컨테이너 선택
        for (Element jobElement : jobElements) {
            RawJobPosting rawJobPosting = new RawJobPosting();

            // 제목 추출
            Element titleElement = jobElement.selectFirst("h4 span");
            if (titleElement != null) {
                rawJobPosting.setJobTitle(titleElement.text());
                Pattern pattern = Pattern.compile("\\[(.*?)]");
                Matcher matcher = pattern.matcher(titleElement.text());
                if (matcher.find()) {
                    rawJobPosting.setJobCompany(matcher.group(1).trim());
                }
            }

            // 상세 정보 추출
            Element detailsContainer = jobElement.selectFirst(".flex.items-center.gap-x-8");
            if (detailsContainer != null) {
                Elements detailSpans = detailsContainer.select("span"); // span 태그만 선택
                if (detailSpans.size() > 0) rawJobPosting.setJobFamily(detailSpans.get(0).text());
                if (detailSpans.size() > 1) rawJobPosting.setJobType(detailSpans.get(1).text());
                Map<String, String> optionalInfo = new HashMap<>();
                if (detailSpans.size() > 2) {
                    optionalInfo.put("jobCareerLevel", detailSpans.get(2).text());
                }
                if (detailSpans.size() > 3) {
                    optionalInfo.put("jobEmploymentType", detailSpans.get(3).text());
                }
                if (detailSpans.size() > 4) {
                    optionalInfo.put("jobPeriod", detailSpans.get(4).text());
                }

                rawJobPosting.setJobOptionalInformation(optionalInfo);
            }

            rawJobPostings.add(rawJobPosting);

        }

//
        return rawJobPostings;
    }

    private String extractId(String href) {
//        if (href != null && href.contains("/jobs/")) {
//            return href.replaceAll(".*/jobs/([^?]+).*", "$1");
//        }
        return "";
    }



}
