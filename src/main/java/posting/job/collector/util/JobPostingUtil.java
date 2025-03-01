package posting.job.collector.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import posting.job.collector.domain.CrawledJobPosting;
import posting.job.collector.domain.JobPostingResult;

import java.time.LocalDate;
import java.util.List;

public class JobPostingUtil {
    public static String convertToJson(List<CrawledJobPosting> crawledJobPostings) {
        JobPostingResult result = new JobPostingResult(crawledJobPostings, crawledJobPostings.size(), LocalDate.now().toString());
        String string = result.getCrawledJobPostings().toString();
        System.out.println(string);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(result);
    }

}
