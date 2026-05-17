package servlet;

import dao.StudentD;
import dao.ScoreD;
import vo.Score;
import vo.Teacher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * 成绩统计 Servlet — 教师功能
 *
 * <p>统计四科的最高分 / 最低分 / 平均分 / 及格率 / 优秀率（≥90）/ 五段分布，
 * <br>转发到 statistics.jsp 展示 Chart.js 柱状图。
 * <br>仅教师可操作。
 */
@WebServlet("/score_statistics")
public class score_statistics extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(score_statistics.class.getName());
    private static final int MAX_QUERY_LIMIT = 10_000;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        Object info = session.getAttribute("info");
        if (!(info instanceof Teacher)) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        try {
            ScoreD sd = new ScoreD();
            StudentD studentD = new StudentD();
            int totalStudents = studentD.getStudentCount();

            ArrayList<Score> scores = sd.getOnePage(1, MAX_QUERY_LIMIT);

            // 初始化四科统计
            Map<String, SubjectStat> stats = new LinkedHashMap<>();
            stats.put("数据结构", new SubjectStat("数据结构"));
            stats.put("操作系统", new SubjectStat("操作系统"));
            stats.put("计算机网络", new SubjectStat("计算机网络"));
            stats.put("计算机组成原理", new SubjectStat("计算机组成原理"));

            // 逐条计入统计
            for (Score s : scores) {
                addScore(stats.get("数据结构"), s.getDataStructure());
                addScore(stats.get("操作系统"), s.getOperatingSystem());
                addScore(stats.get("计算机网络"), s.getComputerNetwork());
                addScore(stats.get("计算机组成原理"), s.getComputerOrganization());
            }

            for (SubjectStat st : stats.values()) st.calculate();

            req.setAttribute("statList", new ArrayList<>(stats.values()));
            req.setAttribute("totalStudents", totalStudents);
            req.getRequestDispatcher("/teacher/statistics.jsp").forward(req, resp);
        } catch (Exception e) {
            LOG.severe("成绩统计失败: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/teacher/main.jsp");
        }
    }

    /** 将单科成绩值加入统计，跳过空值和非法值 */
    private void addScore(SubjectStat stat, String val) {
        if (val == null || val.trim().isEmpty()) return;
        try {
            stat.add(Integer.parseInt(val.trim()));
        } catch (NumberFormatException ignored) {}
    }

    /**
     * 单科成绩统计数据结构
     */
    public static class SubjectStat {
        public String name;
        public int count, sum, max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
        public int passCount, failCount, excellentCount;
        public double avg;
        /** 分数段分布：[0]=&lt;60, [1]=60-69, [2]=70-79, [3]=80-89, [4]=90-100 */
        public int[] distribution = new int[5];

        public SubjectStat(String name) { this.name = name; }

        public void add(int v) {
            count++; sum += v;
            if (v > max) max = v;
            if (v < min) min = v;
            if (v >= 60) passCount++; else failCount++;
            if (v >= 90) excellentCount++;
            if (v < 60) distribution[0]++;
            else if (v < 70) distribution[1]++;
            else if (v < 80) distribution[2]++;
            else if (v < 90) distribution[3]++;
            else distribution[4]++;
        }

        public void calculate() {
            if (count > 0) {
                avg = Math.round((double) sum / count * 10.0) / 10.0;
            } else {
                max = 0; min = 0;
            }
        }
    }
}
