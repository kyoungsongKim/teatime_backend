package castis.web;

import castis.domain.model.AgentPointOnMonth;
import castis.domain.model.PointHistory;
import castis.domain.point.PointHistoryDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class AgentController {

    private PointHistoryDao pointHistoryDao;

    @RequestMapping(value = "agent_info/{currentYear}/{currentMonth}", method = RequestMethod.GET)
    public String MonthlyStat(@PathVariable("currentYear") int currentYear, @PathVariable("currentMonth") int currentMonth, Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String name = user.getUsername(); // get logged in username
        List<PointHistory> pointHistoryList = pointHistoryDao.getPointHistoryByUser(name);
        int totalPoint = 0;
        for (PointHistory pHistory : pointHistoryList) {
            if (pHistory.getUsedate() != null) {
                //add only used code
                totalPoint += pHistory.getPoint();
            }
        }
        int userLevel = (int) Math.sqrt((int) (totalPoint / 1000));
        String totalPointStr = String.format("%,d", totalPoint);
        model.addAttribute("userpoint", totalPointStr);
        model.addAttribute("username", name);
        model.addAttribute("userlevel", userLevel);

        log.info("this is 'agent_info" + "/" + currentYear + "/" + currentMonth + "'");

        // [key]username, [value]userStatOnMonth object list
        Map<String, List<AgentPointOnMonth>> agentStatMap = new HashMap<String, List<AgentPointOnMonth>>();
        List<PointHistory> pointHistoryByMonth = pointHistoryDao.getPointHistoryByYearAndMonth(currentYear, currentMonth);
        for (PointHistory ph : pointHistoryByMonth) {
            if (ph != null && ph.getCode().equalsIgnoreCase("AUTO")) {
                continue;
            }
            if (ph.getSender().equalsIgnoreCase("kskim") || ph.getSender().equalsIgnoreCase("awesomehan77") || ph.getSender().equalsIgnoreCase("donghyun") || ph.getSender().equalsIgnoreCase("test")) {
                // calculate only agent point
                if (agentStatMap.containsKey(ph.getSender()) == true) {
                    // already in map for user
                    List<AgentPointOnMonth> madeList = agentStatMap.get(ph.getSender());
                    madeList.add(new AgentPointOnMonth(ph.getSender(), ph.getRecver(), ph.getMemo(), ph.getPoint(), ph.getUsedate().toString()));
                } else {
                    // insert first time
                    AgentPointOnMonth agentObj = new AgentPointOnMonth(ph.getSender(), ph.getRecver(), ph.getMemo(), ph.getPoint(), ph.getUsedate().toString());
                    List<AgentPointOnMonth> newUserPointList = new ArrayList<AgentPointOnMonth>();
                    newUserPointList.add(agentObj);
                    agentStatMap.put(ph.getSender(), newUserPointList);
                }
            }
        }
        model.addAttribute("year", currentYear);
        model.addAttribute("month", currentMonth);
        model.addAttribute("agentStatMap", agentStatMap);
        return "agent_info";
    }
}
