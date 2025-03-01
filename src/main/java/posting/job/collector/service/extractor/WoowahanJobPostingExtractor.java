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
import posting.job.collector.domain.CrawledJobPosting;
import posting.job.collector.domain.RawJobPosting;
import posting.job.collector.domain.JobPostingResult;
import posting.job.collector.service.normalizer.WoowahanJobNormalizer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class WoowahanJobPostingExtractor {
    private final String url;

    public String extract() throws Exception {
        List<RawJobPosting> rawJobPostings = crawlWoowahanCareers();
        List<CrawledJobPosting> crawledJobPostings = new WoowahanJobNormalizer().normalize(rawJobPostings);
        return convertToJson(crawledJobPostings);
    }

    private List<RawJobPosting> crawlWoowahanCareers() throws Exception {
        List<RawJobPosting> rawJobPostings = new ArrayList<>();
//        WebDriverManager.chromedriver().driverVersion("131.0.6778.267").setup();
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");

        WebDriver driver = new ChromeDriver(options);
        driver.get(url);

        Thread.sleep(5000);

        String pageSource = driver.getPageSource();
        driver.quit();
        Document document = Jsoup.parse(pageSource);
        // 반복 처리: 각 RawJobPosting 블록 선택
        Elements jobElements = document.select("li");
        for (Element jobElement : jobElements) {
            RawJobPosting rawJobPosting = new RawJobPosting();

            // Job ID 추출
            Element linkElement = jobElement.selectFirst("a.title");
            if (linkElement != null) {
                String jobUrl = linkElement.attr("href");
                rawJobPosting.setJobUrl(url + jobUrl);

                // ID는 URL에서 추출
                String[] urlParts = jobUrl.split("/");
//                if (urlParts.length > 2) {
                    rawJobPosting.setJobUrl(url + urlParts[urlParts.length - 2]);
//                }
            }

            // Title 및 Department 추출
            Element titleElement = jobElement.selectFirst("p.fr-view");
            if (titleElement != null) {
                String titleText = titleElement.text();
                rawJobPosting.setJobTitle(titleText);

                // 부서 정보 추출
//                String department = extractDepartmentFromTitle(titleText);
                rawJobPosting.setJobFamily(titleText);
            }

            // Career Level 추출
            Element careerElement = jobElement.selectFirst("span.flag-career");
            Map<String, String> optional = new HashMap<>();
            if (careerElement != null) {
                optional.put("jobCareerLevel", careerElement.text());
            }

            // Employment Type 및 Period 추출
            Elements typeElements = jobElement.select("div.flag-type span");
            if (typeElements.size() > 0) {
                optional.put("jobEmploymentType", typeElements.get(0).text());
            }
            if (typeElements.size() > 1) {
                optional.put("jobPeriod", typeElements.get(1).text().trim());

            }
            rawJobPosting.setJobOptionalInformation(optional);

            // Tags (Field) 추출
            Elements tagElements = jobElement.select("div.flag-tag button");
            if (tagElements != null && !tagElements.isEmpty()) {
                List<String> tags = new ArrayList<>();
                for (Element tagElement : tagElements) {
                    tags.add(tagElement.text().replace("#", "").trim());
                }

                if (!tags.isEmpty()) {
                    rawJobPosting.setJobType(String.join(", ", tags));
                } else {
                    rawJobPosting.setJobType(null); // 값이 없으면 null로 설정

                }

            } else {
                rawJobPosting.setJobType(null); // 태그 자체가 없을 경우 null로 설정

            }

            // Company 정보 설정


                rawJobPosting.setJobCompany("WOOWAHAN");
                rawJobPostings.add(rawJobPosting);

        }

        return rawJobPostings;
    }

    // Title에서 Department 정보 추출
//    private static String extractDepartmentFromTitle(String title) {
//        String department = "";
//
//        // 괄호 안의 내용 추출
//        int start = title.indexOf("[");
//        int end = title.indexOf("]");
//        if (start >= 0 && end > start) {
//            department = title.substring(start + 1, end).trim();
//        }
//
//        return department;
//    }

    private String convertToJson(List<CrawledJobPosting> crawledJobPostings) {
        JobPostingResult result = new JobPostingResult(crawledJobPostings, crawledJobPostings.size(), LocalDate.now().toString());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(result);
    }



}