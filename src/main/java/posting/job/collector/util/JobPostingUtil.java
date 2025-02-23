package posting.job.collector.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import posting.job.collector.domain.JobPosting;
import posting.job.collector.domain.JobPostingResult;

import java.time.LocalDate;
import java.util.List;

public class JobPostingUtil {
    public static String convertToJson(List<JobPosting> jobPostings) {
        JobPostingResult result = new JobPostingResult(jobPostings, jobPostings.size(), LocalDate.now().toString());
        String string = result.getJobPostings().toString();
        System.out.println("//JobPostingResult///"+string);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(result);
    }

}
