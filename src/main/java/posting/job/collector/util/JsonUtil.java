package posting.job.collector.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import posting.job.collector.domain.JobPosting;
import posting.job.collector.domain.JobPostingResult;

import java.time.LocalDate;
import java.util.List;

public class JsonUtil {
    public static String convertToJson(List<JobPosting> jobPostings) {
        JobPostingResult result = new JobPostingResult(jobPostings, jobPostings.size(), LocalDate.now().toString());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(result);
    }
}
