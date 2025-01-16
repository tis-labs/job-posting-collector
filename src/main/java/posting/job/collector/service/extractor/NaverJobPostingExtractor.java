package posting.job.collector.service.extractor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

@AllArgsConstructor
public class NaverJobPostingExtractor {
    private final String url;

    public String extract() throws Exception {
        List<JobPosting> jobPostings = crawlNaverCareers();
        return convertToJson(jobPostings);
    }

    private List<JobPosting> crawlNaverCareers() throws Exception {
        List<JobPosting> jobPostings = new ArrayList<>();

        Document doc = Jsoup.connect(url)
                .timeout(10000)
                .userAgent("Mozilla/5.0")
                .get();

        Elements jobCards = doc.select("li.card_item");

        for (Element card : jobCards) {
            JobPosting job = new JobPosting();

            String onclick = card.select("a.card_link").attr("onclick");
            job.setId(extractId(onclick));

            job.setTitle(card.select("h4.card_title").text());

            Elements infoTexts = card.select("dd.info_text");
            if (infoTexts.size() >= 5) {
                job.setDepartment(infoTexts.get(0).text());
                job.setField(infoTexts.get(1).text());
                job.setCareerLevel(infoTexts.get(2).text());
                job.setEmploymentType(infoTexts.get(3).text());
                job.setPeriod(infoTexts.get(4).text());
            }

            Element companyLogo = card.select("div.company_logo > div").first();
            if (companyLogo != null) {
                job.setCompany(getCompanyName(companyLogo.className()));
            }

            jobPostings.add(job);
        }

        return jobPostings;
    }

    private String extractId(String onclick) {
        if (onclick != null && onclick.contains("show(")) {
            return onclick.replaceAll("[^0-9]", "");
        }
        return "";
    }

    private String getCompanyName(String className) {
        return switch (className) {
            case "snow" -> "SNOW";
            case "nfin" -> "NAVER FINANCIAL";
            case "navercloud" -> "NAVER Cloud";
            case "webtoon" -> "NAVER WEBTOON";
            default -> "NAVER";
        };
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
    }

}