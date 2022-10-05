package castis.domain.model;

import com.ibm.icu.util.ChineseCalendar;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Holiday {
    private Map<String, String> holidayMap;

    public Map<String, String> getHolidayMap() {
        return holidayMap;
    }

    @PostConstruct
    public void setHoliday() {
        int holidayStartYear = 2016;
        int holidayEndYear = 2200;
        Map<String, String> holidayMap = new HashMap<String, String>();

        Map<String, String> solarHolidayMap = new HashMap<String, String>();
        Map<String, String> lunarHolidayMap = new HashMap<String, String>();

        solarHolidayMap.put("0101", "신정");
        solarHolidayMap.put("0301", "삼일절");
        solarHolidayMap.put("0505", "어린이날");
        solarHolidayMap.put("0606", "현충일");
        solarHolidayMap.put("0815", "광복절");
        solarHolidayMap.put("1003", "개천절");
        solarHolidayMap.put("1009", "한글날");
        solarHolidayMap.put("1225", "성탄절");
        solarHolidayMap.put("0724", "창립기념일");
        solarHolidayMap.put("0501", "근로자의날");

        lunarHolidayMap.put("0101", "설날");
        lunarHolidayMap.put("0102", "설날");
        lunarHolidayMap.put("0408", "석가탄신일");
        lunarHolidayMap.put("0814", "추석");
        lunarHolidayMap.put("0815", "추석");
        lunarHolidayMap.put("0816", "추석");

        Calendar c = Calendar.getInstance();
        c.set(holidayStartYear, 0, 1); // 1월 1일부터 시작

        String solarDate = "";
        String lunarDate = "";
        for (int i = holidayStartYear; i <= holidayEndYear; ) {
            solarDate = getDateByString(c.getTime(), "");
            lunarDate = converSolarToLunar(solarDate, "");

            c.add(Calendar.DAY_OF_MONTH, 1);

            // 양력휴일 체크
            String solarMmdd = solarDate.substring(4, 8);
            if (solarHolidayMap.containsKey(solarMmdd)) {
                holidayMap.put(getDay(solarDate, 0), solarHolidayMap.get(solarMmdd));
            }

            // 음력휴일 체크
            String lunarMmdd = lunarDate.substring(4, 8);
            if (lunarHolidayMap.containsKey(lunarMmdd)) {
                // 음력 12월은 마지막날이 29일, 30일 계속 번갈아가면서 있으므로
                // 양력에서 하루를 빼준날이 구정시작하는 날짜이다.
                if (lunarMmdd.equals("0101")) {
                    holidayMap.put(getDay(solarDate, -1), "설날");
                }
                holidayMap.put(getDay(solarDate, 0), lunarHolidayMap.get(lunarMmdd));
            }

            holidayStartYear = c.get(Calendar.YEAR);
            if (holidayStartYear != i) {
                i++;
            }
            if (i > holidayEndYear) break;
        } // end for_i

        holidayMap.put("2017-05-09", "대통령선거");
        holidayMap.put("2017-10-06", "추석");
        holidayMap.put("2017-10-02", "아무튼 휴일");
        holidayMap.put("2018-05-01", "근로자의 날");
        holidayMap.put("2018-05-07", "대체 휴일");
        holidayMap.put("2018-09-26", "대체 휴일");
        holidayMap.put("2019-05-06", "대체 휴일");
        holidayMap.put("2021-10-04", "대체 휴일");
        holidayMap.put("2021-10-11", "대체 휴일");
        holidayMap.put("2022-03-09", "대통령 선거");
        holidayMap.put("2022-09-12", "대체 휴일");
        holidayMap.put("2022-10-10", "대체 휴일");

        this.holidayMap = holidayMap;

        log.info("holiday set success");
    }

    public String getDateByString(Date date) {
        return getDateByString(date, "-");
    }

    public String getDateByString(Date date, String separator) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + separator + "MM" + separator + "dd");
        return sdf.format(date);
    }

    private String converSolarToLunar(String date, String separator) {
        ChineseCalendar cc = new ChineseCalendar();
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));
        cal.set(Calendar.MONTH, Integer.parseInt(date.substring(4, 6)) - 1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(6)));

        cc.setTimeInMillis(cal.getTimeInMillis());

        int y = cc.get(ChineseCalendar.EXTENDED_YEAR) - 2637;
        int m = cc.get(ChineseCalendar.MONTH) + 1;
        int d = cc.get(ChineseCalendar.DAY_OF_MONTH);

        StringBuffer ret = new StringBuffer();
        ret.append(String.format("%04d", y)).append(separator);
        ret.append(String.format("%02d", m)).append(separator);
        ret.append(String.format("%02d", d));

        return ret.toString();
    } // end converSolarToLunar

    public String getDay(String date, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(4, 6)) - 1, Integer.parseInt(date.substring(6)));
        cal.add(Calendar.DAY_OF_MONTH, amount);

        return getDateByString(cal.getTime(), "-");
    }
}
