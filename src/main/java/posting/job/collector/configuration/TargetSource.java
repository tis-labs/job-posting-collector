package posting.job.collector.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TargetSource {
    NAVER("네이버", "https://recruit.navercorp.com/rcrt/list.do?subJobCdArr=1010001%2C1010002%2C1010003%2C1010004%2C1010006&sysCompanyCdArr=&empTypeCdArr=&entTypeCdArr=&workAreaCdArr=&sw=&subJobCdData=1010001&subJobCdData=1010002&subJobCdData=1010003&subJobCdData=1010004&subJobCdData=1010006"),
//    KAKAO("카카오", "https://careers.kakao.com/jobs?skillSet=Android%2CiOS%2CWeb_front%2CServer%2CStatistics_Analysis%2CWindows%2CCloud%2CDB%2CNetwork%2CSecurity%2CSystem%2CAlgorithm_ML%2CHadoop_eco_system%2CQA%2Cetc&part=TECHNOLOGY&company=ALL&keyword=&employeeType=&page=1"),
//    NHN("NHN", "https://careers.nhn.com/recruits?jobGroupId=3645799730550663017"),
//    WOOWAHAN("우아한형제들", "https://career.woowahan.com/?jobCodes=&employmentTypeCodes=&serviceSectionCodes=&careerPeriod=&category=#recruit-list"),
//    SOCARCORP("쏘카", "https://www.socarcorp.kr/careers/jobs"),
    TOSS("토스", "https://toss.im/career/jobs?category=Backend");
//    DUNAMU("두나무", "https://www.dunamu.com/careers/jobs?category=engineering"),
//    HYUNDAI("현대", "https://talent.hyundai.com/apply/applyList.hc?nfGubnC=ac85892205b92e8cecdc87185a3fbf039f04b2a7751ccf0e8a1f547d53b9945a&tagArray=&sortDataTagArray=&areaDataTagArray=&fieldDataTagArray=&occupDataTagArray=&intnsvYn=");


    private final String name;
    private final String url;
}
