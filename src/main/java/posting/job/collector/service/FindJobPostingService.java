package posting.job.collector.service;

import org.springframework.stereotype.Service;
import posting.job.collector.configuration.TargetSource;

import java.util.Arrays;
import java.util.List;

@Service
public class FindJobPostingService {
    public List<TargetSource> execute() {
        return Arrays.stream(TargetSource.values()).toList();
    }
}
