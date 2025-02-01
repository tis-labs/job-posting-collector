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
import posting.job.collector.util.JsonUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
public class CoupangPostingExtractor {
    private final String url;


    public String extract() throws Exception {
        List<JobPosting> jobPostings = crawlCoupangCareers();
        return JsonUtil.convertToJson(jobPostings);
    }

    private List<JobPosting> crawlCoupangCareers() throws Exception {
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

        Elements jobElements = document.select("div.card.card-job");
        for (Element jobElement : jobElements) {
            JobPosting job = new JobPosting();

            // 제목과 URL 추출
            Element titleElement = jobElement.selectFirst("h2.card-title a");
            if (titleElement != null) {
                String title = titleElement.text();
                job.setTitle(title);
                job.setJobDetailUrl(titleElement.attr("href"));

                // Team 관련 정보를 추출하여 department에 설정
                String team = extractTeam(title);
                if (!team.isEmpty()) {
                    job.setJobRole(team);
                }
            }

            jobPostings.add(job);


//

        }
        return jobPostings;
    }

    private static String extractTeam(String title) {
        StringBuilder department = new StringBuilder();

        // 괄호 안의 내용 추출
        Pattern parenthesesPattern = Pattern.compile("\\(([^)]*)\\)");
        Matcher parenthesesMatcher = parenthesesPattern.matcher(title);
        if (parenthesesMatcher.find()) {
            department.append(parenthesesMatcher.group(1).trim());
        }

        // 쉼표 뒤의 내용 추출
        Pattern commaPattern = Pattern.compile(",\\s*([^,]*)$");
        Matcher commaMatcher = commaPattern.matcher(title);
        if (commaMatcher.find()) {
            if (department.length() > 0) {
                department.append(" | ");
            }
            department.append(commaMatcher.group(1).trim());
        }

        return department.toString();
    }



}
