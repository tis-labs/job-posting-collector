package posting.job.collector;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import posting.job.collector.domain.JobFamily;

public class CrawledJobPostingTypeTest {

    @Test
    public void testNormalize_withMatchingAlias() {
        Assertions.assertEquals("TECH", JobFamily.normalize("Tech"));
        Assertions.assertEquals("TECH", JobFamily.normalize("Technical"));
        Assertions.assertEquals("TECH", JobFamily.normalize("테크"));
        Assertions.assertEquals("TECH", JobFamily.normalize("기술"));
        Assertions.assertEquals("TECH", JobFamily.normalize("개발"));

    }

    @Test
    public void testNormalize_withNoMatch() {
        Assertions.assertEquals("기타", JobFamily.normalize("기타"));
    }

    @Test
    public void testNormalize_withMatchingManagement() {
        Assertions.assertEquals("MANAGEMENT", JobFamily.normalize("MANAGEMENT"));
        Assertions.assertEquals("MANAGEMENT", JobFamily.normalize("매니지먼트"));
        Assertions.assertEquals("MANAGEMENT", JobFamily.normalize("지원"));
        Assertions.assertEquals("MANAGEMENT", JobFamily.normalize("경영지원"));

    }

}
