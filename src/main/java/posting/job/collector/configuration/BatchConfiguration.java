package posting.job.collector.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import posting.job.collector.service.ExtractJobPostingService;
import posting.job.collector.service.FindJobPostingService;

import java.util.List;

@Configuration
public class BatchConfiguration {

    public static final String COLLECTOR_JOB = "COLLECTOR_JOB";
    public static final String COLLECT_JOB_POSTING = "COLLECT_JOB_POSTING";
    public static final int CHUNK_SIZE = 10;

    @Bean
    Job collectorJob(
            JobRepository jobRepository,
            Step collectJobPostingStep
    ) {
        return new JobBuilder(COLLECTOR_JOB, jobRepository)
                .start(collectJobPostingStep)
                .build();
    }

    @Bean
    Step collectJobPostingStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<TargetSource> findTargetJobPostingReader,
            ItemProcessor<TargetSource, String> collectJobPostingProcessor,
            ItemWriter<String> saveJobPostingWriter
    ) {
        return new StepBuilder(COLLECT_JOB_POSTING, jobRepository)
                .<TargetSource, String>chunk(CHUNK_SIZE, transactionManager)
                .reader(findTargetJobPostingReader)
                .processor(collectJobPostingProcessor)
                .writer(saveJobPostingWriter)
                .build();
    }

    @Bean
    ItemReader<TargetSource> findTargetJobPostingReader(
            FindJobPostingService findJobPostingService
    ) {
        var targetSources = findJobPostingService.execute().iterator();
        return () -> {
            if (!targetSources.hasNext()) {
                return null;
            }
            return targetSources.next();
        };
    }

    @Bean
    ItemProcessor<TargetSource, String> collectJobPostingProcessor(
            ExtractJobPostingService extractJobPostingService
    ) {
        return extractJobPostingService::execute;
    }

    @Bean
    ItemWriter<String> saveJobPostingWriter() {
        return items -> {
            for (String item : items) {
                System.out.println(item);
            }
        };
    }
}
