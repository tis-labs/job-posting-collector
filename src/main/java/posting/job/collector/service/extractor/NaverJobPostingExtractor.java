package posting.job.collector.service.extractor;

import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import posting.job.collector.domain.CrawledJobPosting;
import posting.job.collector.domain.RawJobPosting;
import posting.job.collector.service.normalizer.NaverJobNormalizer;


@AllArgsConstructor
public class NaverJobPostingExtractor {
    private final String url;
    private static final String NAVER_JOB_DETAIL_URL = "https://recruit.navercorp.com/rcrt/view.do?annoId=";
    public   List<CrawledJobPosting> extract() throws Exception {
        List<RawJobPosting> rawJobPostings = crawlNaverCareers();
        List<CrawledJobPosting> crawledJobPostings = new NaverJobNormalizer().normalize(rawJobPostings);
        return crawledJobPostings;
//        return jobPostings;
    }

    private List<RawJobPosting> crawlNaverCareers() throws Exception {
        List<RawJobPosting> rawJobPostings = new ArrayList<>();

        Document doc = Jsoup.connect(url)
                .timeout(10000)
                .userAgent("Mozilla/5.0")
                .get();

        Elements jobCards = doc.select("li.card_item");

        for (Element card : jobCards) {
            RawJobPosting rawJobPosting = new RawJobPosting();

            String onclick = card.select("a.card_link").attr("onclick");
            rawJobPosting.setJobUrl(onclick);

            rawJobPosting.setJobTitle(card.select("h4.card_title").text());

            Elements infoTexts = card.select("dd.info_text");
            Map<String, String> optionalInfo = new HashMap<>();
            if (infoTexts.size() >= 5) {
                rawJobPosting.setJobFamily(infoTexts.get(0).text());
                rawJobPosting.setJobType(infoTexts.get(1).text());
                optionalInfo.put("jobCareerLevel", infoTexts.get(2).text());
                optionalInfo.put("jobEmploymentType", infoTexts.get(3).text());
                optionalInfo.put("jobPeriod", infoTexts.get(4).text());

            }

            Element companyLogo = card.select("div.company_logo > div").first();
            if (companyLogo != null) {
                rawJobPosting.setJobCompany(companyLogo.className());
            }



            // 상세 페이지 크롤링 및 추가
            if (onclick != null && !onclick.isEmpty()) {
                try {
                    String jobTitleDetail = crawlNaverJobDetail(onclick);
                    optionalInfo.put("jobTitleDetail", jobTitleDetail);
                } catch (Exception e) {
                    System.err.println("상세 페이지 크롤링 실패: " + onclick + " - " + e.getMessage());
                }
            }
            rawJobPosting.setJobOptionalInformation(optionalInfo);
            rawJobPostings.add(rawJobPosting);
        }

        return rawJobPostings;
    }

    private String crawlNaverJobDetail(String onClick) throws IOException {
        String jobTitle = "";

        Document doc = Jsoup.connect(NAVER_JOB_DETAIL_URL + extractId(onClick))
                .timeout(10000)
                .userAgent("Mozilla/5.0")
                .get();

        Elements detailBoxes = doc.select(".detail_wrap .detail_box");

        // 각 "detail_box" 안의 "span" 태그 텍스트 추출
        String spanValue = "";
        for (Element detailBox : detailBoxes) {

            Elements spans = detailBox.select("span:not(button span)");
            if(spans.size() >= 2){
                String firstTitle = spans.get(0).text();
                String secondTitle = spans.get(1).text();

                if (firstTitle.equals("Who We Are")) continue;
                if (secondTitle.equals("Who We Are")) continue;
                if (firstTitle.equals(secondTitle)) { // 두 title이 같은 경우
                    spanValue += firstTitle + " "; // jobTitle에 첫 번째 title만 추가
                } else { // 두 title이 다른 경우
                    spanValue += firstTitle + " " + secondTitle + " "; // jobTitle에 두 title 추가

                }
            } else if (spans.size() == 1) { // <span> 태그가 1개만 존재하는 경우
                String firstTitle = spans.get(0).text();
                if (firstTitle.equals("Who We Are")) continue;
                spanValue += firstTitle + " ";
            }

            jobTitle = spanValue;
        }
        return jobTitle;

    }
    private String extractId(String onclick) {
        if (onclick != null && onclick.contains("show(")) {
            return onclick.replaceAll("[^0-9]", "");
                }
        return "";
    }



}