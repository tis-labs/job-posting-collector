package posting.job.collector.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import posting.job.collector.domain.CrawledJobPosting;
import posting.job.collector.service.ExtractJobPostingService;
import posting.job.collector.service.FindJobPostingService;
import posting.job.collector.service.StandbyCrawledJobService;

import java.util.List;

@Configuration
public class BatchConfiguration {

    public static final String COLLECTOR_JOB = "COLLECTOR_JOB";
    public static final String COLLECT_JOB_POSTING = "COLLECT_JOB_POSTING";
    public static final int CHUNK_SIZE = 10;

    @Bean
    Job collectorJob(
            JobRepository jobRepository,
            Step collectJobPostingStep,
            JobExecutionListener afterSystemExitListener
    ) {
        return new JobBuilder(COLLECTOR_JOB, jobRepository)
                .listener(afterSystemExitListener)
                .start(collectJobPostingStep)
                .build();
    }

    @Bean
    Step collectJobPostingStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<TargetSource> findTargetJobPostingReader,
            ItemProcessor<TargetSource, List<CrawledJobPosting>> collectJobPostingProcessor,
            ItemWriter< List<CrawledJobPosting>> saveJobPostingWriter
    ) {
        return new StepBuilder(COLLECT_JOB_POSTING, jobRepository)
                .<TargetSource, List<CrawledJobPosting>>chunk(CHUNK_SIZE, transactionManager)
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
    ItemProcessor<TargetSource, List<CrawledJobPosting>> collectJobPostingProcessor(
            ExtractJobPostingService extractJobPostingService
    ) {
        return extractJobPostingService::execute;
    }

    @Bean
    ItemWriter<List<CrawledJobPosting>> saveJobPostingWriter(
            StandbyCrawledJobService standbyCrawledJobService
    ) {
        return jobPostings -> {
            for (List<CrawledJobPosting> crawledJobPosting :  jobPostings) {
                for (CrawledJobPosting job : crawledJobPosting) {
                    standbyCrawledJobService.standby(job);
                }
            }
        };
    }

    @Bean
    JobExecutionListener afterSystemExitListener() {
        /*
          After Job 명령은 JOB 작업은 완료됐지만 JOB 이 종료되기 전에 실행한다.
          JOB 종료 후 시스템이 종료되도록 3초 뒤 시스템 종료를 수행한다.
          AXON 프레임워크 사용시 AXON 서버와 연결이 유지되기 때문에 직접 종료해야 한다.
          FIXME : JOB 종료 시점을 파악해 종료하거나 AXON 서버와 연결을 끊어야 한다.
         */
        return new JobExecutionListener() {
            @Override
            public void afterJob(JobExecution jobExecution) {
                new Thread(() -> {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ignored) {
                    }
                    System.exit(0);
                }).start();
            }
        };
    }
}
