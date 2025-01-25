package posting.job.collector.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TargetSource {
    NAVER("네이버", "https://recruit.navercorp.com/rcrt/list.do?subJobCdArr=1010001%2C1010002%2C1010003%2C1010004%2C1010006&sysCompanyCdArr=&empTypeCdArr=&entTypeCdArr=&workAreaCdArr=&sw=&subJobCdData=1010001&subJobCdData=1010002&subJobCdData=1010003&subJobCdData=1010004&subJobCdData=1010006"),
    KAKAO("카카오", "https://careers.kakao.com/jobs?skillSet=Android%2CiOS%2CWeb_front%2CServer&part=TECHNOLOGY&company=ALL&keyword=&employeeType=&page=1"),
    NHN("NHN", "https://careers.nhn.com/recruits?jobGroupId=3645799730550663017"),
    COUPANG("쿠팡", "https://rocketyourcareer.kr.coupang.com/")
    ;

    private final String name;
    private final String url;
}
