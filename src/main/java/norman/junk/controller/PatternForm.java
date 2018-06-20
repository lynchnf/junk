package norman.junk.controller;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import norman.junk.DatabaseException;
import norman.junk.NotFoundException;
import norman.junk.domain.Pattern;
import norman.junk.service.CategoryService;

public class PatternForm {
    @Valid
    private List<PatternRow> patternRows = new ArrayList<>();

    public PatternForm() {
    }

    public PatternForm(Iterable<Pattern> patterns) {
        for (Pattern pattern : patterns) {
            PatternRow patternRow = new PatternRow(pattern);
            patternRows.add(patternRow);
        }
    }

    public List<Pattern> toPatterns(CategoryService categoryService) throws DatabaseException, NotFoundException {
        List<Pattern> patterns = new ArrayList<>();
        for (int i = 0; i < patternRows.size(); i++) {
            PatternRow patternRow = patternRows.get(i);
            Pattern pattern = patternRow.toPattern(categoryService);
            pattern.setSeq(i + 1);
            patterns.add(pattern);
        }
        return patterns;
    }

    public List<PatternRow> getPatternRows() {
        return patternRows;
    }

    public void setPatternRows(List<PatternRow> patternRows) {
        this.patternRows = patternRows;
    }
}