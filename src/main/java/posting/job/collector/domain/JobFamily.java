package posting.job.collector.domain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum JobFamily {
    TECH("Tech", "Technical","테크", "기술" ,"개발"),
    MARKETING("Marketing", "마케팅"),
    SALES("Sales" ,"영업"),
    MANAGEMENT( "Management", "매니지먼트","지원","경영지원"),;

    private final Set<String> aliases;

    JobFamily(String ... aliases) {
        this.aliases = new HashSet<>(Arrays.asList(aliases));
    }

    public static String normalize(String input){
        return Arrays.stream(values())
                .filter(category -> category.aliases.contains(input))
                .findFirst()
                .map(Enum::name)
                .orElse(input);
    }

}
