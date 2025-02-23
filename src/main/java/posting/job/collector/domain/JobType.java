package posting.job.collector.domain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum JobType {

    BACKEND("Backend", "백엔드"),
    FRONTEND("Frontend", "프론트엔드", "프론트"),
    DATA_ENGINEERING("Data Engineering", "데이터 엔지니어링", "데이터 엔지니어"),
    IOS("iOS", "아이오에스", "아이폰 개발"),
    ANDROID("Android", "안드로이드", "안드"),
    INFRA_ENGINEERING("Infra Engineering", "인프라 엔지니어링", "인프라"),
    IT_SECURITY("IT Security", "보안", "정보 보안"),
    AI_ML("AI/ML", "머신러닝", "인공지능", "AI", "ML");

    private final Set<String> aliases;

    JobType(String ... aliases) {
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
