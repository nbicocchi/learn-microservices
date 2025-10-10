package cloud.macca.aggregator.model;

import java.util.List;

public class ReportCard {
    private final String studentFullName;
    private final List<Grade> studentGrades;
    public ReportCard(String studentFullName, List<Grade> grades){
        this.studentFullName = studentFullName;
        this.studentGrades = grades;
    }

    @Override
    public String toString() {
        return "ReportCard{" +
                "studentFullName='" + studentFullName + '\'' +
                ", studentGrades=" + studentGrades.stream().map(g -> g.value).toList() +
                ", avg = " + studentGrades.stream().map(g -> (double) g.value / studentGrades.size()).reduce(0.0, Double::sum) +
                '}';
    }
}
