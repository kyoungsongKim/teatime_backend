package castis.util.holiday;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final Environment environment;
    private Map<String, ArrayList<HolidayDto>> holidayMap = new HashMap<String, ArrayList<HolidayDto>>();
    public List<HolidayDto> getHolidayInfo(String year, String month) throws IOException {
        String mapKey = year+"_"+month;
        if ( holidayMap.containsKey(mapKey)) {
            return holidayMap.get(mapKey);
        }
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + environment.getProperty("holiday.openApi.serviceKey")); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("100", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("solYear","UTF-8") + "=" + URLEncoder.encode(year, "UTF-8")); /*연*/
        urlBuilder.append("&" + URLEncoder.encode("solMonth","UTF-8") + "=" + URLEncoder.encode(month, "UTF-8")); /*월*/
        urlBuilder.append("&" + URLEncoder.encode("_type","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        List<HolidayJsonDto> list = string2Map(sb.toString());
        ArrayList<HolidayDto> result = new ArrayList<>();
        list.forEach(i -> {
            result.add(new HolidayDto(i));
        });
        holidayMap.put(mapKey, result);
        return result;

    }

    private List<HolidayJsonDto> string2Map(String json) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = null;

        try{
            map = mapper.readValue(json, Map.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, Object> response = (Map<String, Object>)map.get("response");
        Map<String, Object> body = (Map<String, Object>)response.get("body");
        List<HolidayJsonDto> list = new ArrayList<>();
        if(body.get("items") != "") {
            HashMap<String, Object> items = (HashMap<String, Object>) body.get("items");
            Object o = items.get("item");
            Gson gson = new Gson();
            if(o instanceof ArrayList) {
                list = gson.fromJson(o.toString(), new TypeToken<ArrayList<HolidayJsonDto>>(){}.getType());
            }else{
                HolidayJsonDto dto = gson.fromJson(o.toString(), HolidayJsonDto.class);
                list.add(dto);
            }
        }
        return list;
    }
}
