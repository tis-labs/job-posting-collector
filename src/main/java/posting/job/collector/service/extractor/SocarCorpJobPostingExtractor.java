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

import posting.job.collector.domain.CrawledJobPosting;
import posting.job.collector.domain.RawJobPosting;
import posting.job.collector.service.normalizer.SocarCorpJobNormalizer;
import posting.job.collector.util.JobPostingUtil;

@AllArgsConstructor
public class SocarCorpJobPostingExtractor {
    private final String url;

    public String extract() throws Exception {
        List<RawJobPosting> rawJobPostings = crawlSocarCorpCareers();
        List<CrawledJobPosting> crawledJobPostings = new SocarCorpJobNormalizer().normalize(rawJobPostings);
        return JobPostingUtil.convertToJson(crawledJobPostings);
    }

    private List<RawJobPosting> crawlSocarCorpCareers() throws Exception {
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

        Elements jobElements = document.select(".ResultItem_CareerItem__pplVI");

        for (Element jobElement : jobElements) {
            RawJobPosting rawJobPosting = new RawJobPosting();

            // Job Detail URL
            Element jobLink = jobElement.selectFirst("a");
            if (jobLink != null) {
                rawJobPosting.setJobUrl(url + jobLink.attr("href"));
            }

            // Title and Career Level
            Element titleElement = jobElement.selectFirst(".ResultItem_title__TW_Eq");
            if (titleElement != null) {
                String title = titleElement.select("span").text();
                String careerLevel = titleElement.select("em").text();
                rawJobPosting.setJobTitle(title);
                Map<String, String> optional = new HashMap<>();
                optional.put("jobCareerLevel", careerLevel);
                rawJobPosting.setJobOptionalInformation(optional);
            }

            // Department: 첫 번째 <span> 태그를 찾아서 부서 정보로 설정
            Element departmentElement = jobElement.selectFirst(".ResultItem_tag__N9pI_");
            if (departmentElement != null) {
                rawJobPosting.setJobType(departmentElement.text()); // 부서 정보 가져오기
            }


            // Field (태그 정보)
            Elements fieldTags = jobElement.select(".ResultItem_tag__N9pI_");
            if (fieldTags != null && !fieldTags.isEmpty()) {
                List<String> fields = new ArrayList<>();
                for (Element tag : fieldTags) {
                    fields.add(tag.text());
                }
                rawJobPosting.setJobFamily(String.join(", ", fields));
            }

            // Company
            Element companyElement = jobElement.selectFirst(".ResultItem_tag__N9pI_");
            if (companyElement != null) {
                rawJobPosting.setJobCompany(companyElement.text());
            }


            rawJobPostings.add(rawJobPosting);

        }

        return rawJobPostings;
    }



}