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
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(result);
    }

    public static boolean isValidJobPosting(JobPosting job) {
        return job != null &&
                job.getTitle() != null &&
                    (job.getId() != null ||
                        job.getJobCategory() != null ||
                        job.getJobRole() != null ||
                        job.getCareerLevel() != null ||
                        job.getEmploymentType() != null ||
                        job.getPeriod() != null ||
                        job.getCompany() != null ||
                        job.getJobDetailUrl() != null );
    }
}
