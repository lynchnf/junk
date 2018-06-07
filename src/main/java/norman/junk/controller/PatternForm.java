package norman.junk.controller;

import java.util.ArrayList;
import java.util.List;
import norman.junk.domain.Pattern;

public class PatternForm {
    private List<PatternRow> patternRows = new ArrayList<>();

    public PatternForm() {
    }

    public PatternForm(Iterable<Pattern> patterns) {
        for (Pattern pattern : patterns) {
            PatternRow patternRow = new PatternRow(pattern);
            patternRows.add(patternRow);
        }
    }

    public List<PatternRow> getPatternRows() {
        return patternRows;
    }

    public void setPatternRows(List<PatternRow> patternRows) {
        this.patternRows = patternRows;
    }
}