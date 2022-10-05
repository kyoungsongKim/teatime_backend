package castis.web;

import castis.domain.board.*;
import castis.domain.model.PointHistory;
import castis.domain.point.PointHistoryDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class HiringController {

    private static final Logger logger = LoggerFactory.getLogger(HiringController.class);

    private BoardSvc boardSvc;
    private BoardGroupSvc boardGroupSvc;
    private PointHistoryDao pointHistoryDao;

    @RequestMapping(value = "/hiring")
    public String boardList(SearchVO searchVO, ModelMap modelMap) {
        if (searchVO.getBgno() == null) {
            searchVO.setBgno("2");
        }

        BoardGroupVO bgInfo = boardGroupSvc.selectBoardGroupOne4Used(searchVO.getBgno());
        if (bgInfo == null) {
            return "board/BoardGroupFail";
        }
        searchVO.pageCalculate(boardSvc.selectBoardCount(searchVO));
        List<BoardVO> listview = boardSvc.selectBoardList(searchVO);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedToday = df.format(new Date());
        int substringCount = "YYYY-mm-ddT".length();
        for (BoardVO bvd : listview) {
            if (bvd.getBrddate().contains(formattedToday) == true) {
                // this article was written today (YYYY-mm-ddT 11char)
                bvd.setBrddate(bvd.getBrddate().substring(substringCount));
            } else {
                // this article was written yesterday or ago
                bvd.setBrddate(bvd.getBrddate().substring(0, substringCount - 1));
            }
        }
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
        logger.info("username:{} point:{}", name, totalPointStr);
        modelMap.addAttribute("userpoint", totalPointStr);
        modelMap.addAttribute("username", name);
        modelMap.addAttribute("userlevel", userLevel);
        modelMap.addAttribute("listview", listview);
        modelMap.addAttribute("searchVO", searchVO);
        modelMap.addAttribute("bgInfo", bgInfo);

        return "board/HiringList";
    }
}
